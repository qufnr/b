package space.byeoruk.b.domain.notification.service

import com.google.gson.Gson
import org.springframework.data.domain.Page
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import space.byeoruk.b.domain.member.details.MemberDetails
import space.byeoruk.b.domain.member.entity.Member
import space.byeoruk.b.domain.notification.dto.NotificationDto
import space.byeoruk.b.domain.notification.entity.Notification
import space.byeoruk.b.domain.notification.exception.NotificationNotFoundException
import space.byeoruk.b.domain.notification.model.NotificationType
import space.byeoruk.b.domain.notification.repository.NotificationRepository
import space.byeoruk.b.global.dto.PaginationDto

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val messagingTemplate: SimpMessagingTemplate
) {
    /**
     * Send it to notifications.
     *
     * @param message 메시지
     * @param type 알림 유형
     * @param receiver 받는자
     * @param sender 발송자 (선택)
     */
    @Transactional
    fun send(message: String, type: NotificationType, receiver: Member, sender: Member?) {
        val notification = Notification(
            receiver = receiver,
            sender = sender,
            message = message,
            type = type
        )

        notificationRepository.save(notification)

        val destination = "/queue/member/${receiver.uid}/notification"

        val payload = NotificationDto.Response.fromEntity(notification)
        messagingTemplate.convertAndSend(destination, payload)
    }

    /**
     * 내 알림 목록 조회
     *
     * @param request 요청 정보
     * @return 알림 목록
     */
    fun read(request: NotificationDto.ReadRequest, memberDetails: MemberDetails): PaginationDto.Response<NotificationDto.Response> {
        request.nullToZero()

        val items: Page<Notification> = notificationRepository.findAllByPage(request, memberDetails, request.toPageRequest())

        return PaginationDto.Response.from(items.pageable, items.map { item -> NotificationDto.Response.fromEntity(item) })
    }

    /**
     * 알림 업데이트
     *
     * @param uid 알림 UID
     * @param request 요청 정보
     * @param memberDetails 사용자 인증 객체
     */
    @Transactional
    fun update(uid: Long, request: NotificationDto.UpdateRequest, memberDetails: MemberDetails) {
        val notification = notificationRepository.findByUidAndReceiverUid(uid, memberDetails.getIdentifier())
            .orElseThrow { throw NotificationNotFoundException() }

        notification.update(request)
    }

    /**
     * 알림 단건 삭제
     *
     * @param uid 알림 UID
     * @param memberDetails 사용자 인증 객체
     */
    @Transactional
    fun delete(uid: Long, memberDetails: MemberDetails) {
        val notification = notificationRepository.findByUidAndReceiverUid(uid, memberDetails.getIdentifier())
            .orElseThrow { throw NotificationNotFoundException() }

        notificationRepository.deleteById(notification.uid)
    }

    /**
     * 알림 일괄 삭제
     *
     * @param request 요청 정보
     * @param memberDetails 사용자 인증 객체
     */
    @Transactional
    fun deleteAll(request: NotificationDto.DeleteRequest, memberDetails: MemberDetails) {
        val count = notificationRepository.deleteByRequest(request, memberDetails)
        if(count <= 0)
            throw NotificationNotFoundException()
    }
}