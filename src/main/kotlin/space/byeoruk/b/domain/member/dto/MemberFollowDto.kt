package space.byeoruk.b.domain.member.dto

import io.swagger.v3.oas.annotations.media.Schema
import space.byeoruk.b.domain.member.dto.MemberDto.Details
import space.byeoruk.b.domain.member.model.FollowState

class MemberFollowDto {

    class Response(
        @Schema(description = "팔로우 여부", example = "UNFOLLOW / FOLLOW")
        val state: FollowState,
        val followee: Details
    )

    class Status(
        @Schema(description = "팔로워 수", example = "1")
        var followerCount: Int = 0,
        @Schema(description = "팔로잉 수", example = "10")
        var followingCount: Int = 0,
        @Schema(description = "나를 팔로우 중인지 여부", example = "true")
        var isFollowingMe: Boolean = false,
        @Schema(description = "내가 팔로우 중인지 여부", example = "false")
        var amIFollowing: Boolean = false,
    )
}