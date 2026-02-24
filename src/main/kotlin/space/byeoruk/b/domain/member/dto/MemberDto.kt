package space.byeoruk.b.domain.member.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import space.byeoruk.b.domain.member.entity.Member
import space.byeoruk.b.domain.member.model.IdentityCanUse
import space.byeoruk.b.domain.member.model.PrivacyStatus
import space.byeoruk.b.domain.member.model.ResourceType
import space.byeoruk.b.domain.member.model.MemberRole
import space.byeoruk.b.global.utility.StringUtilities.mask
import space.byeoruk.b.global.utility.StringUtilities.maskEmail
import java.time.LocalDate
import java.time.LocalDateTime

class MemberDto {
    /**
     * 계정 생성
     */
    class CreateRequest(
        @Schema(description = "계정 ID", example = "username1234")
        @NotBlank(message = "{validation.member.id.mandatory}")
        @Size(max = 32, message = "{validation.member.id.size}")
        val id: String,
        @Schema(description = "계정 비밀번호", example = "1q2w3e4r!")
        @NotBlank(message = "{validation.member.password.mandatory")
        @Size(max = 64, message = "{validation.member.password.size}")
        val password: String,
        @Schema(description = "계정 비밀번호 확인", example = "1q2w3e4r!")
        @NotBlank(message = "{validation.member.password-confirm.mandatory}")
        val passwordConfirm: String,
        @Schema(description = "이메일", example = "member@byeoruk.space")
        @Email(message = "{validation.member.email.format}")
        val email: String,
        @Schema(description = "계정 이름", example = "김수한무")
        @Size(max = 16, message = "{validation.member.name.size}")
        val name: String?,
        @Schema(description = "소개글", example = "<p>안녕하세요.</p><p>반갑습니다.</p>")
        @Size(max = 512, message = "{validation.member.bio.size}")
        val bio: String,
    )

    /**
     * 계정 수정
     */
    class UpdateRequest(
        @Schema(description = "계정 이름", example = "아리텐동")
        @Size(max = 16, message = "{validation.member.name.size}")
        val name: String?,
        @Schema(description = "소개글", example = "<p>안녕하세요.</p><p>반갑습니다.</p>")
        @Size(max = 512, message = "{validation.member.bio.size}")
        val bio: String?,
        @Schema(description = "생일", example = "1972-12-31")
        val birthday: LocalDate?,
        val privacy: MemberPrivacyDto.UpdateRequest?,
    )

    /**
     * 계정 리소스(아바타, 배너) 수정 요청
     */
    class UpdateResourceRequest(
        @Schema(description = "리소스 유형 (AVATAR | BANNER)", example = "AVATAR")
        val type: ResourceType,
        @Schema(description = "삭제 여부", example = "false")
        val isDelete: Boolean,
    )

    /**
     * 계정 비밀번호 변경 요청
     */
    class UpdatePasswordRequest(
        @Schema(description = "이메일 인증 키", example = "453648")
        val key: String,
        @Schema(description = "변경 비밀번호", example = "1q2w3e4r!!@")
        val password: String,
        @Schema(description = "변경 비밀번호 확인", example = "1q2w3e4r!!@")
        val passwordConfirm: String
    )

    /**
     * 계정 ID 또는 이메일 사용 가능 확인 요청
     */
    class CanUseRequest(
        @Schema(description = "유형 (ID | EMAIL)", example = "EMAIL")
        val type: IdentityCanUse,
        @Schema(description = "확인 값", example = "aris@kivotos.jp")
        val value: String
    )

    /**
     * 계정 ID 또는 이메일 사용 가능 여부 응답
     */
    class CanUseResponse(
        @Schema(description = "사용 가능 여부", example = "true")
        val canUse: Boolean,
    )

    /**
     * 계정 ID 또는 비밀번호 찾기 요청
     */
    class ForgetRequest(
        @Schema(description = "찾으려고 하는 계정의 ID 또는 이메일", example = "aris@kivotos.jp")
        val value: String,
    )

    /**
     * 이메일 인증 요청
     */
    class VerifyEmailRequest(
        @Schema(description = "인증 키", example = "F127FBV")
        val key: String,
    )

    /**
     * 기본 계정 조회
     */
    class Details(
        @Schema(description = "계정 UID", example = "1")
        val uid: Long,
        @Schema(description = "계정 ID", example = "username1234")
        val id: String,
        @Schema(description = "계정 이메일", example = "aris@kivotos.jp")
        var email: String = "",
        @Schema(description = "계정 이름", example = "아리텐스동동스")
        val name: String? = null,
        @Schema(description = "소개글", example = "<p>안녕하세요.</p><p>반갑습니다.</p>")
        var bio: String? = "<p></p>",
        @Schema(description = "색상", example = "#FFFF00")
        val colour: String,
        @Schema(description = "아바타 이미지 파일", example = "filename.jpg")
        val avatar: String? = "",
        @Schema(description = "배너 이미지 파일", example = "filename.jpg")
        val banner: String? = "",
        @Schema(description = "마지막 로그인 날짜")
        val lastSignedAt: LocalDateTime? = null,
        @Schema(description = "마지막 계정 이름 변경 날짜")
        val lastNameChangedDate: LocalDate? = null,
        @Schema(description = "탄생일", example = "2025-03-30")
        var birthday: LocalDate? = null,
        @Schema(description = "계정 잠금 여부", example = "false")
        val isLocked: Boolean,
        @Schema(description = "계정 활성화 여부", example = "true")
        val isEnabled: Boolean,
        @Schema(description = "계정 인증 여부", example = "true")
        val isVerified: Boolean,

        val followStatus: MemberFollowDto.Status,
        val privacy: MemberPrivacyDto.Details,
        val authorities: List<MemberRole>
    ) {
        /**
         * 마스킹된 계정 ID 반환
         *
         * @return 마스킹된 계정 ID
         */
        fun getMaskedId(): String = id.mask()

        /**
         * 마스킹된 계정 이메일 반환
         *
         * @return 마스킹된 계정 이메일
         */
        fun getMaskedEmail(): String = email.maskEmail()

        /**
         * 프라이버시 설정에 반영하여 데이터 숨김 처리
         */
        fun privacyField() {
            if(privacy.profile == PrivacyStatus.PRIVATE || (privacy.profile == PrivacyStatus.FOLLOW_ONLY && !followStatus.amIFollowing)) {
                bio = "<p></p>"
                email = ""
                birthday = null
            }

            else if(privacy.birthday == PrivacyStatus.PRIVATE || (privacy.birthday == PrivacyStatus.FOLLOW_ONLY && !followStatus.amIFollowing))
                birthday = null
        }

        companion object {
            /**
             * 계정 Entity 를 MemberDto.Details 로 변환
             *
             * @param member 계정 Entity
             * @return MemberDto.Details 항목
             */
            fun fromEntity(member: Member): Details {
                return Details(
                    uid = member.uid,
                    id = member.id,
                    email = member.email,
                    name = member.name,
                    bio = member.bio,
                    colour = member.colour,
                    avatar = member.avatar,
                    banner = member.banner,
                    lastSignedAt = member.lastSignedAt,
                    lastNameChangedDate = member.lastNameChangedDate,
                    birthday = member.birthday,
                    isLocked = member.isLocked,
                    isEnabled = member.isEnabled,
                    isVerified = member.isVerified,
                    followStatus = MemberFollowDto.Status(),
                    privacy = MemberPrivacyDto.Details.fromEntity(member.privacy),
                    authorities = member.authorities.map { authority -> authority.authority }.toList()
                )
            }

            /**
             * 로그 찍을 때 쓰는 스냅샷
             *
             * @param member 계정 Entity
             * @return 스냅샷 맵
             */
            fun snapshots(member: Member): Map<String, Any?> = mapOf(
                "member.id" to member.id,
                "member.name" to member.name,
                "member.email" to member.email,
                "member.bio" to member.bio,
                "member.avatar" to member.avatar,
                "member.banner" to member.banner,
                "member.birthday" to member.birthday,
                "member.privacy.profile" to member.privacy.profile,
                "member.privacy.birthday" to member.privacy.birthday,
                "member.privacy.feed" to member.privacy.feed,
            )
        }
    }
}