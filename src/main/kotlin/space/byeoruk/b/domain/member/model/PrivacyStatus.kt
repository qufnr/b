package space.byeoruk.b.domain.member.model

enum class PrivacyStatus(val description: String, val localeKey: String) {
    PUBLIC("공개", "text.publicState.public"),
    FOLLOW_ONLY("팔로우 공개", "text.publicState.followOnly"),
    PRIVATE("비공개", "text.publicState.private"),
}