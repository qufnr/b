package space.byeoruk.b.domain.notification.service

import com.google.gson.Gson
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import space.byeoruk.b.domain.member.entity.Member
import space.byeoruk.b.domain.notification.dto.NotificationDto
import space.byeoruk.b.domain.notification.entity.Notification
import space.byeoruk.b.domain.notification.model.NotificationType
import space.byeoruk.b.domain.notification.repository.NotificationRepository

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
}