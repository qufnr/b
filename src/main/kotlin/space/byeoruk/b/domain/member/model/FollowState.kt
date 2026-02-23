package space.byeoruk.b.domain.member.model

import space.byeoruk.b.global.model.EnumMapper

enum class FollowState(override val description: String): EnumMapper {
    FOLLOW("팔로우"),
    UNFOLLOW("언팔로우");

    override val code: String
        get() = name
}