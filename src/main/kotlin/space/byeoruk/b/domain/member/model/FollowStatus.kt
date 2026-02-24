package space.byeoruk.b.domain.member.model

import space.byeoruk.b.global.model.EnumMapper

enum class FollowStatus(override val description: String): EnumMapper {
    FOLLOW("팔로우"),
    FOLLOW_PENDING("팔로우 대기"),
    UNFOLLOW("언팔로우");

    override val code: String
        get() = name
}