package space.byeoruk.b.domain.member.dto

import io.swagger.v3.oas.annotations.media.Schema
import space.byeoruk.b.domain.member.entity.MemberVerification
import space.byeoruk.b.domain.member.model.MemberVerifyType
import java.time.LocalDateTime

class MemberVerificationDto {
    class Details(
        @Schema(description = "인증 키", example = "{bcrypt}암호화 문자열")
        val key: String,
        @Schema(description = "인증 유형", example = "EMAIL_VERIFICATION")
        val type: MemberVerifyType,
        @Schema(description = "인증 만료 날짜", example = "2025-12-31T12:00:00.000")
        val expiredAt: LocalDateTime,
        @Schema(description = "인증 날짜", example = "2026-01-01T00:00:00.000")
        val usedAt: LocalDateTime? = null
    ) {
        companion object {
            fun fromEntity(memberVerification: MemberVerification): Details = Details(
                key = memberVerification.key,
                type = memberVerification.type,
                expiredAt = memberVerification.expiredAt,
                usedAt = memberVerification.usedAt
            )
        }
    }
}