package space.byeoruk.b.domain.member.model

import space.byeoruk.b.global.model.EnumMapper

enum class PrivacyStatus(override val description: String, override val locale: String): EnumMapper {
    PUBLIC("공개", "enum.privacy-status.public"),
    FOLLOW_ONLY("팔로우 공개", "enum.privacy-status.follow-only"),
    PRIVATE("비공개", "enum.privacy-status.private");

    override val code: String
        get() = name
}