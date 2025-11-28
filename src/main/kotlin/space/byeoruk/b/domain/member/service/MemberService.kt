package space.byeoruk.b.domain.member.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import space.byeoruk.b.domain.member.details.MemberDetails
import space.byeoruk.b.domain.member.dto.MemberDto
import space.byeoruk.b.domain.member.entity.Member
import space.byeoruk.b.domain.member.exception.MemberNotFoundException
import space.byeoruk.b.domain.member.exception.MemberPasswordConfirmMismatchException
import space.byeoruk.b.domain.member.repository.MemberRepository

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder) {
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
     * @param imageRequest 이미지 관련 요청 정보 (아바타, 배너)
     * @param memberDetails 계정 디테일
     */
    fun update(request: MemberDto.UpdateRequest, imageRequest: MemberDto.ImageUpdateRequest, memberDetails: MemberDetails) {
        val member = memberRepository.findById(memberDetails.username)
            .orElseThrow{ MemberNotFoundException() }

        member.update(request, imageRequest)
        memberRepository.save(member)
    }
}