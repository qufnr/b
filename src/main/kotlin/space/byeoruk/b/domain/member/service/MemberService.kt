package space.byeoruk.b.domain.member.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
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

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
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
    fun create(request: MemberDto.CreateRequest): MemberDto.Details {
        if(request.password == request.passwordConfirm)
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
    fun update(request: MemberDto.UpdateRequest, memberDetails: MemberDetails) {
        println("계정 수정?")

        val member = memberRepository.findById(memberDetails.username)
            .orElseThrow { MemberNotFoundException() }

        println("계정 수정 222?")

        member.update(request)
        memberRepository.save(member)
    }

    /**
     * 계정 리소스(아바타, 배너) 수정
     *
     * @param request 요청 정보
     * @param file 파일
     * @param memberDetails 계정 디테일
     */
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
}