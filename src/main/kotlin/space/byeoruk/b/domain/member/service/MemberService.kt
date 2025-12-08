package space.byeoruk.b.domain.member.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import space.byeoruk.b.domain.member.annotation.MemberAction
import space.byeoruk.b.domain.member.details.MemberDetails
import space.byeoruk.b.domain.member.dto.MemberDto
import space.byeoruk.b.domain.member.entity.Member
import space.byeoruk.b.domain.member.exception.MemberNotFoundException
import space.byeoruk.b.domain.member.exception.MemberPasswordConfirmMismatchException
import space.byeoruk.b.domain.member.model.MemberCanUseType
import space.byeoruk.b.domain.member.model.MemberHistoryType
import space.byeoruk.b.domain.member.model.MemberResourceType
import space.byeoruk.b.domain.member.provider.MemberAvatarProvider
import space.byeoruk.b.domain.member.provider.MemberBannerProvider
import space.byeoruk.b.domain.member.repository.MemberRepository
import space.byeoruk.b.infra.mail.dto.MailDto
import space.byeoruk.b.infra.mail.service.MailSender

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val mailSender: MailSender,
    private val memberAvatarProvider: MemberAvatarProvider,
    private val memberBannerProvider: MemberBannerProvider
) {
    /**
     * 계정 UID 로 정보 조회
     *
     * @param uid 계정 UID
     * @return 계정 상세 정보
     */
    fun read(uid: Long): MemberDto.Details {
        val member = memberRepository.findById(uid)
            .orElseThrow { MemberNotFoundException() }

        return MemberDto.Details.fromEntity(member)
    }

    /**
     * 계정 생성
     *
     * @param request 요청 정보
     * @return 생성된 계정 정보
     */
    @Transactional
    fun create(request: MemberDto.CreateRequest): MemberDto.Details {
        if(request.password != request.passwordConfirm)
            throw MemberPasswordConfirmMismatchException()

        val member = Member(request)
        member.password = passwordEncoder.encode(request.password).toString()

        return MemberDto.Details.fromEntity(memberRepository.save(member))
    }

    /**
     * 계정 수정
     *
     * @param request 요청 정보
     * @param memberDetails 계정 디테일
     */
    @MemberAction(type = MemberHistoryType.ACCOUNT_UPDATED, trackUpdates = true)
    @Transactional
    fun update(request: MemberDto.UpdateRequest, memberDetails: MemberDetails): MemberDto.Details {
        val member = memberRepository.findById(memberDetails.username)
            .orElseThrow { MemberNotFoundException() }

        member.update(request)
        memberRepository.save(member)

        return MemberDto.Details.fromEntity(member)
    }

    /**
     * 계정 리소스(아바타, 배너) 수정
     *
     * @param request 요청 정보
     * @param file 파일
     * @param memberDetails 계정 디테일
     */
    @Transactional
    fun update(request: MemberDto.ResourceUpdateRequest, file: MultipartFile, memberDetails: MemberDetails) {
        val member = memberRepository.findById(memberDetails.username)
            .orElseThrow { MemberNotFoundException() }

        when(request.type) {
            MemberResourceType.AVATAR -> {
                if(request.isDelete) {
                    memberAvatarProvider.delete(member)
                    member.updateAvatar()
                }
                else
                    member.updateAvatar(memberAvatarProvider.save(member, file))
            }
            MemberResourceType.BANNER -> {
                if(request.isDelete) {
                    memberBannerProvider.delete(member)
                    member.updateBanner()
                }
                else
                    member.updateBanner(memberBannerProvider.save(member, file))
            }
        }

        memberRepository.save(member)
    }

    /**
     * 계정 ID 사용 가능 여부 확인
     *
     * @param request 요청 정보
     * @return 사용 가능 여부
     */
    fun canUse(request: MemberDto.CanUseRequest): MemberDto.CanUseResponse {
        val member = when(request.type) {
            MemberCanUseType.ID -> memberRepository.findById(request.value)
            MemberCanUseType.EMAIL -> memberRepository.findByEmail(request.value)
        }.orElse(null)

        return MemberDto.CanUseResponse(member == null)
    }

    /**
     * 계정 ID 찾기
     *
     * @param request 요청 정보
     * @return 계정 정보
     */
    @Transactional
    @MemberAction(type = MemberHistoryType.ACCOUNT_FORGET)
    fun forget(request: MemberDto.ForgetRequest): MemberDto.Details {
        val member = memberRepository.findByIdOrEmail(request.value, request.value)
            .orElseThrow { MemberNotFoundException() }

        val memberDetails = MemberDto.Details.fromEntity(member)

        mailSender.send(member.email, MailDto.Content(
            subject = "기억에서 잠깐 사라졌던 계정을 찾았어요!",
            message = "${memberDetails.getMaskedId()}(으)로 계정을 만들었고, ${memberDetails.getMaskedEmail()} 이메일을 사용하고 있어요."
        ))

        return memberDetails
    }

    /**
     * 계정 비밀번호 찾기
     *
     * @param request 요청 정보
     * @return 계정 정보
     */
    @Transactional
    @MemberAction(type = MemberHistoryType.ACCOUNT_FORGET_PASSWORD)
    fun forgetPassword(request: MemberDto.ForgetRequest): MemberDto.Details {
        val member = memberRepository.findByIdOrEmail(request.value, request.value)
            .orElseThrow { MemberNotFoundException() }

        val memberDetails = MemberDto.Details.fromEntity(member)

        //  TODO :: 인증번호 발급 후 비밀번호 초기화 URL 재공

        mailSender.send(member.email, MailDto.Content(
            subject = "계정 비밀번호를 잊어버리셨나요?",
            message = ""
        ))

        return memberDetails
    }
}