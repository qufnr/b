package space.byeoruk.b.domain.member.dto

import space.byeoruk.b.domain.member.model.MemberHistoryType

class MemberHistoryDto {
    class RecordMap(
        val type: MemberHistoryType,
        val before: Map<String, Any?>?,
        val after: Map<String, Any?>?,
        var ipAddress: String,
        var userAgent: String,
        var message: String
    ) {
        constructor(type: MemberHistoryType, message: String): this(
            type = type,
            before = null,
            after = null,
            ipAddress = "",
            userAgent = "",
            message = message
        )

        constructor(type: MemberHistoryType, before: Map<String, Any?>?, after: Map<String, Any?>?, message: String): this(
            type = type,
            before = before,
            after = after,
            ipAddress = "",
            userAgent = "",
            message = message
        )
    }
}
