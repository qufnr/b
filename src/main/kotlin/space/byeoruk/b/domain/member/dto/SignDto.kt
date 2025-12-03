package space.byeoruk.b.domain.member.dto

import io.swagger.v3.oas.annotations.media.Schema

class SignDto {
    class IdRequest(
        @Schema(description = "계정 ID 또는 이메일", example = "username1234")
        val value: String
    )

    class IdDetails(
        @Schema(description = "계정 ID", example = "username1234")
        val id: String,
        @Schema(description = "계정 이름 (없을 경우 ID 로 대체 됨)", example = "김수한무")
        val name: String?,
        @Schema(description = "JWT 문자열")
        val token: String,
        @Schema(description = "JWT 만료 시간", example = "1234421412")
        val expiration: Long,
    ) {
        companion object {
            fun build(token: String, expiration: Long, value: Map<String, String>): IdDetails {
                return IdDetails(value["id"]!!, value["name"], token, expiration)
            }
        }
    }

    class Request(
        @Schema(description = "계정 비밀번호", example = "1q2w3e4r!!")
        val password: String
    )

    class Details(
        @Schema(description = "접근 토큰", example = "JWT...")
        val access: String,
        @Schema(description = "접근 토큰 만료 시간", example = "123124124124")
        val accessExpiration: Long,
        @Schema(description = "리프레시 토큰", example = "JWT...")
        val refresh: String,
        @Schema(description = "리프레시 토큰 만료 시간", example = "123124124124")
        val refreshExpiration: Long,
        val member: MemberDto.Details
    )
}