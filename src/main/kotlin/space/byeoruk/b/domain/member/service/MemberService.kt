package space.byeoruk.b.domain.member.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import space.byeoruk.b.domain.member.annotation.MemberAction
import space.byeoruk.b.domain.member.details.MemberDetails
import space.byeoruk.b.domain.member.dto.MemberDto
import space.byeoruk.b.domain.member.dto.MemberVerificationDto
import space.byeoruk.b.domain.member.dto.SignDto
import space.byeoruk.b.domain.member.entity.Member
import space.byeoruk.b.domain.member.exception.AlreadyMemberMailVerifiedException
import space.byeoruk.b.domain.member.exception.InvalidVerificationKeyException
import space.byeoruk.b.domain.member.exception.MemberNotFoundException
import space.byeoruk.b.domain.member.exception.NameChangeCooldownException
import space.byeoruk.b.domain.member.exception.PasswordConfirmMismatchException
import space.byeoruk.b.domain.member.exception.VerificationKeyEncryptFailedException
import space.byeoruk.b.domain.member.model.MemberCanUseType
import space.byeoruk.b.domain.member.model.MemberHistoryType
import space.byeoruk.b.domain.member.model.MemberResourceType
import space.byeoruk.b.domain.member.model.MemberVerifyType
import space.byeoruk.b.domain.member.provider.MemberAvatarProvider
import space.byeoruk.b.domain.member.provider.MemberBannerProvider
import space.byeoruk.b.domain.member.provider.MemberTokenProvider
import space.byeoruk.b.domain.member.repository.MemberFollowRepository
import space.byeoruk.b.domain.member.repository.MemberRepository
import space.byeoruk.b.domain.member.repository.MemberVerificationRepository
import space.byeoruk.b.global.utility.StringUtilities
import space.byeoruk.b.infra.mail.dto.MailDto
import space.byeoruk.b.infra.mail.service.MailSender
import space.byeoruk.b.security.model.TokenType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.Locale

@Service
class MemberService(
    //  비밀번호 초기화 전 인증 키 유효 시간 (분)
    @Value($$"${bserver.verification.password.expiration}")
    private val resetPasswordKeyExpiration: Long,
    //  이메일 인증 키 유효 시간 (분)
    @Value($$"${bserver.verification.email.expiration}")
    private val emailVerifyKeyExpiration: Long,
    //  이름 변경 딜레이 (일)
    @Value($$"${bserver.name-change-delay}")
    private val nameChangeDelay: Long,

    private val memberRepository: MemberRepository,
    private val memberFollowRepository: MemberFollowRepository,
    private val memberVerificationRepository: MemberVerificationRepository,

    private val messageSource: MessageSource,
    private val passwordEncoder: PasswordEncoder,
    private val mailSender: MailSender,

    private val memberAvatarProvider: MemberAvatarProvider,
    private val memberBannerProvider: MemberBannerProvider,
    private val memberTokenProvider: MemberTokenProvider,
) {
    /**
     * 시큐리티 콘텍스트에 담긴 Principal 로 계정 정보 조회
     *
     * @param memberDetails 인증된 계정 정보
     * @return 계정 상세 정보
     */
    fun read(memberDetails: MemberDetails): MemberDto.Details {
        val member = memberRepository.findById(memberDetails.username)
            .orElseThrow { MemberNotFoundException() }

        return MemberDto.Details.fromEntity(member)
    }

    /**
     * 계정 UID 로 정보 조회
     *
     * @param uid 계정 UID
     * @param memberDetails 사용자 인증 객체
     * @return 계정 상세 정보
     */
    fun read(uid: Long, memberDetails: MemberDetails): MemberDto.Details {
        val opponent = memberRepository.findById(uid)
            .orElseThrow { MemberNotFoundException() }

        var details = MemberDto.Details.fromEntity(opponent)

        val member = memberRepository.findById(memberDetails.username)
            .orElse(null)

        if(member != null) {
            details.isFollowingMe = memberFollowRepository.existsByFollowerAndFollowee(opponent, member)
            details.amIFollowing = memberFollowRepository.existsByFollowerAndFollowee(member, opponent)
        }

        return details
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
            throw PasswordConfirmMismatchException()

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
    @Transactional
    @MemberAction(type = MemberHistoryType.ACCOUNT_UPDATED, trackUpdates = true)
    fun update(request: MemberDto.UpdateRequest, memberDetails: MemberDetails): MemberDto.Details {
        val member = memberRepository.findById(memberDetails.username)
            .orElseThrow { MemberNotFoundException() }

        //  이름 변경 날짜 확인
        if(request.name != member.name && member.lastNameChangedDate != null) {
            val nameCanChangeDate = member.lastNameChangedDate!!.plusDays(nameChangeDelay)
            val now = LocalDate.now()

            if(nameCanChangeDate > now) {
                val days = ChronoUnit.DAYS.between(now, nameCanChangeDate)
                throw NameChangeCooldownException(days)
            }
        }

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
    fun update(request: MemberDto.UpdateResourceRequest, file: MultipartFile, memberDetails: MemberDetails) {
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
    @MemberAction(MemberHistoryType.ACCOUNT_FORGET)
    fun forget(request: MemberDto.ForgetRequest): MemberDto.Details {
        val member = memberRepository.findByIdOrEmail(request.value, request.value)
            .orElseThrow { MemberNotFoundException() }

        val memberDetails = MemberDto.Details.fromEntity(member)

        val locale: Locale = LocaleContextHolder.getLocale()
        val subject = messageSource.getMessage("mail.member.forget.id.subject", null, "mail.member.forget.id.subject", locale)
        val message = messageSource.getMessage("mail.member.forget.id.message", arrayOf(memberDetails.getMaskedId(), memberDetails.getMaskedEmail()), "mail.member.forget.id.message", locale)

        mailSender.send(member.email, MailDto.Content(
            subject = subject!!,
            message = message!!
        ))

        return memberDetails
    }

    /**
     * 계정 비밀번호 찾기
     * 비밀번호 확인 인증 키를 생성했다면 사용자 ID 검증 토큰과 함께 응답을 보냅니다.
     * 이 후에 `/api/member-management/members/password/verification` 에서 인증 키를 검증하고, 비밀번호를 재설정 합니다.
     *
     * @param request 요청 정보
     * @return 사용자 ID 상세 정보 (사용자 ID 검증과 같은 응답 보냄)
     */
    @Transactional
    @MemberAction(MemberHistoryType.ACCOUNT_FORGET_PASSWORD)
    fun forgetPassword(request: MemberDto.ForgetRequest): SignDto.IdDetails {
        val member = memberRepository.findByIdOrEmail(request.value, request.value)
            .orElseThrow { MemberNotFoundException() }

        val key = StringUtilities.random(6)
        val encryptedKey =
            passwordEncoder.encode(key)
                ?: throw VerificationKeyEncryptFailedException()

        member.addVerification(MemberVerifyType.RESET_PASSWORD, encryptedKey, resetPasswordKeyExpiration)

        val locale: Locale = LocaleContextHolder.getLocale()
        val subject = messageSource.getMessage("mail.member.forget.password.subject", null, "mail.member.forget.password.subject", locale)
        val message = messageSource.getMessage("mail.member.forget.password.message", null, "mail.member.forget.password.message", locale)

        mailSender.send(member.email, MailDto.Content(
            subject = subject!!,
            message = message!!,
            code = key,
            actionUrl =  "" //  TODO :: 프론트로 바로 이동할 수 있는 URL 을 제공할지 생각 해보기
        ))

        return memberTokenProvider.issueSignToken(member, TokenType.PASSWORD)
    }

    /**
     * 비밀번호 변경
     *
     * @param request 요청 정보
     * @param authorization 계정 ID 검증 토큰
     * @return 사용자 상세 정보
     */
    @Transactional
    @MemberAction(MemberHistoryType.ACCOUNT_PASSWORD_UPDATED)
    fun updatePassword(request: MemberDto.UpdatePasswordRequest, authorization: String): MemberDto.Details {
        val payload = memberTokenProvider.getTokenPayload(authorization, TokenType.PASSWORD)

        if(request.password != request.passwordConfirm)
            throw PasswordConfirmMismatchException()

        //  claims에서 "uid"를 가져오면 형태를 알 수 없기 때문에 캐스팅을 두 번 한다. 매개변수가 String 형태면 계정 ID로 조회함..
        val member = memberRepository.findById(payload["uid"].toString().toLong())
            .orElseThrow { MemberNotFoundException() }

        val verifications = memberVerificationRepository.findValidKeys(member, MemberVerifyType.RESET_PASSWORD)
        val verification = verifications.find { passwordEncoder.matches(request.key, it.key) }
            ?: throw InvalidVerificationKeyException()

        //  인증 키 사용 처리
        verification.usedAt = LocalDateTime.now()
        memberVerificationRepository.save(verification)

        //  비밀번호 변경 처리
        member.password = passwordEncoder.encode(request.password)!!
        memberRepository.save(member)

        return MemberDto.Details.fromEntity(member)
    }

    @Transactional
    fun sendVerifyEmail(memberDetails: MemberDetails): MemberVerificationDto.Details {
        val member = memberRepository.findById(memberDetails.username)
            .orElseThrow { MemberNotFoundException() }

        if(member.isVerified)
            throw AlreadyMemberMailVerifiedException()

        val key = passwordEncoder.encode(StringUtilities.random(6))!!

        val verification = member.addVerification(MemberVerifyType.EMAIL_VERIFICATION, key, emailVerifyKeyExpiration)
        memberRepository.save(member)

        return verification
    }

    @Transactional
    @MemberAction(MemberHistoryType.ACCOUNT_EMAIL_VERIFIED)
    fun verifyEmail(request: MemberDto.VerifyEmailRequest, memberDetails: MemberDetails): MemberDto.Details {
        val member = memberRepository.findById(memberDetails.username)
            .orElseThrow { MemberNotFoundException() }

        val verifications = memberVerificationRepository.findValidKeys(member, MemberVerifyType.EMAIL_VERIFICATION)
        val verification = verifications.find { passwordEncoder.matches(request.key, it.key) }
            ?: throw InvalidVerificationKeyException()

        verification.usedAt = LocalDateTime.now()
        member.isVerified = true

        memberRepository.save(member)
        return MemberDto.Details.fromEntity(member)
    }
}