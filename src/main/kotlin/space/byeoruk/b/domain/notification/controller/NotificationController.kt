package space.byeoruk.b.domain.notification.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import space.byeoruk.b.domain.member.details.MemberDetails
import space.byeoruk.b.domain.notification.dto.NotificationDto
import space.byeoruk.b.domain.notification.service.NotificationService
import space.byeoruk.b.global.dto.ResponseDto

@Tag(name = "Notification", description = "알림 API")
@RequestMapping("/api/notification-management/notifications")
@RestController
class NotificationController(private val notificationService: NotificationService) {

    @Operation(summary = "알림 조회", description = "사용자 알림을 조회합니다.")
    @GetMapping
    fun read(
        @RequestAttribute request: NotificationDto.ReadRequest,
        @AuthenticationPrincipal memberDetails: MemberDetails
    ): ResponseEntity<*> =
        ResponseEntity.ok(
            ResponseDto.build(
                notificationService.read(request, memberDetails),
                HttpStatus.OK,
                "Success!!!")
        )

    @Operation(summary = "알림 수정", description = "사용자 알림을 수정합니다.")
    @PutMapping("/{uid}")
    fun update(
        @PathVariable uid: Long,
        @RequestBody request: NotificationDto.UpdateRequest,
        @AuthenticationPrincipal memberDetails: MemberDetails): ResponseEntity<Unit> {
        notificationService.update(uid, request, memberDetails)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "알림 단건 삭제", description = "사용자 알림 한 건을 삭제합니다.")
    @DeleteMapping("/{uid}")
    fun delete(
        @PathVariable uid: Long,
        @AuthenticationPrincipal memberDetails: MemberDetails): ResponseEntity<Unit> {
        notificationService.delete(uid, memberDetails)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "알림 일괄 삭제", description = "사용자 알림 여러 건을 삭제합니다.")
    @DeleteMapping
    fun deleteAll(
        @RequestBody request: NotificationDto.DeleteRequest,
        @AuthenticationPrincipal memberDetails: MemberDetails): ResponseEntity<Unit> {
        notificationService.deleteAll(request, memberDetails)
        return ResponseEntity.noContent().build()
    }
}