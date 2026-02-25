package space.byeoruk.b.domain.notification.dto

import io.swagger.v3.oas.annotations.media.Schema
import space.byeoruk.b.domain.member.dto.MemberDto
import space.byeoruk.b.domain.notification.entity.Notification
import space.byeoruk.b.domain.notification.model.NotificationType
import space.byeoruk.b.global.dto.PaginationDto
import java.time.LocalDateTime

class NotificationDto {
    /**
     * 조회 요청
     */
    class ReadRequest(
        page: Int?,
        size: Int?,
        @Schema(description = "읽음 여부 (선택)", example = "false")
        val isRead: Boolean?,
        @Schema(description = "조회 기준 날짜 (선택)", example = "2026-02-25T12:00:00.000")
        val standardDate: LocalDateTime?
    ): PaginationDto.Request(page, size)

    /**
     * 수정 요청
     */
    class UpdateRequest(
        @Schema(description = "읽음 여부", example = "true")
        val isRead: Boolean,
    )

    /**
     * 삭제 요청 (일괄 삭제 시에만 사용함)
     */
    class DeleteRequest(
        @Schema(description = "삭제할 알림 UID 목록", example = "[1,2,3,4]")
        val uids: List<Long>? = emptyList(),
        @Schema(description = "읽은 것만 삭제 여부", example = "true")
        val onlyRead: Boolean,
    )

    /**
     * 응답 내용
     */
    class Response(
        @Schema(description = "알림 UID", example = "1")
        val uid: Long,
        val receiver: MemberDto.ShortDetails,
        val sender: MemberDto.ShortDetails?,
        @Schema(description = "알림 메시지(또는 JSON 문자열)", example = "알림 메시지입니다.")
        val message: String,
        @Schema(description = "알림 유형", example = "GUILD_MENTION")
        val type: NotificationType,
        @Schema(description = "읽음 여부", example = "false")
        val isRead: Boolean = false,
        @Schema(description = "전송 날짜", example = "2026-02-20T12:00:00.000")
        val sentAt: LocalDateTime
    ) {
        companion object {
            fun fromEntity(notification: Notification): Response =
                Response(
                    uid = notification.uid,
                    receiver = MemberDto.ShortDetails.fromEntity(notification.receiver),
                    sender = if(notification.sender != null) MemberDto.ShortDetails.fromEntity(notification.sender!!) else null,
                    message = notification.message ?: "No messages.",
                    type = notification.type,
                    isRead = notification.isRead,
                    sentAt = notification.sentAt
                )
        }
    }
}