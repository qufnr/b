package space.byeoruk.b.domain.member.dto

import io.swagger.v3.oas.annotations.media.Schema
import space.byeoruk.b.domain.member.entity.MemberPrivacy
import space.byeoruk.b.domain.member.model.PrivacyStatus

class MemberPrivacyDto {
    class Details(
        @Schema(description = "프로필 공개 여부", example = "PUBLIC")
        val profile: PrivacyStatus,
        @Schema(description = "탄생일 공개 여부", example = "PRIVATE")
        val birthday: PrivacyStatus,
        @Schema(description = "피드 공개 여부", example = "FOLLOW_ONLY")
        val feed: PrivacyStatus
    ) {
        companion object {
            fun fromEntity(privacy: MemberPrivacy): Details {
                return Details(privacy.profile, privacy.birthday, privacy.feed)
            }
        }
    }
}