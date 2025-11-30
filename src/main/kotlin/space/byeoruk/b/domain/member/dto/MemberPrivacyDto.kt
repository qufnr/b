package space.byeoruk.b.domain.member.dto

import io.swagger.v3.oas.annotations.media.Schema
import space.byeoruk.b.domain.member.entity.MemberPrivacy

class MemberPrivacyDto {
    class Details(
        @Schema(description = "프로필 공개 여부", example = "false")
        val isPublic: Boolean,
        @Schema(description = "탄생일 공개 여부", example = "false")
        val isBirthdayPublic: Boolean
    ) {
        companion object {
            fun fromEntity(privacy: MemberPrivacy): Details {
                return Details(privacy.isPublic, privacy.isBirthdayPublic)
            }
        }
    }
}