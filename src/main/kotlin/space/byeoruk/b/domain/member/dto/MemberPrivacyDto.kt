package space.byeoruk.b.domain.member.dto

import space.byeoruk.b.domain.member.entity.MemberPrivacy

class MemberPrivacyDto {
    class Details(
        val isPublic: Boolean,
        val isBirthdayPublic: Boolean
    ) {
        companion object {
            fun fromEntity(privacy: MemberPrivacy): Details {
                return Details(privacy.isPublic, privacy.isBirthdayPublic)
            }
        }
    }
}