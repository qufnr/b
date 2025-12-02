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
        val token: String
    ) {
        companion object {
            fun build(token: String, value: Map<String, String>): IdDetails {
                return IdDetails(value["id"]!!, value["name"], token)
            }
        }
    }

    class Request(
        @Schema(description = "계정 비밀번호", example = "1q2w3e4r!!")
        val password: String
    )

    class Details(
        @Schema(description = "Access Token", example = "JWT...")
        val access: String,
        @Schema(description = "Refresh Token", example = "JWT...")
        val refresh: String,
        val member: MemberDto.Details
    )
}