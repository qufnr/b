package space.byeoruk.b.domain.notification.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import space.byeoruk.b.domain.member.entity.Member
import space.byeoruk.b.domain.notification.model.NotificationType
import java.time.LocalDateTime

@Entity
@Table(name = "notification")
class Notification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", nullable = false, unique = true, comment = "UID")
    var uid: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_uid", nullable = false, comment = "알림 받는자", foreignKey = ForeignKey(name = "FK_notification_receiver_TO_member"))
    val receiver: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_uid", comment = "알림 발생자 (시스템 알림일 경우 null)", foreignKey = ForeignKey(name = "FK_notification_sender_TO_member"))
    val sender: Member? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", comment = "알림 유형", nullable = false, length = 64)
    val type: NotificationType,

    @Column(name = "is_read", comment = "읽음 여부", nullable = false)
    val isRead: Boolean = false,

    @Column(name = "message", comment = "간단한 메시지 또는 JSON 문자열", length = 512)
    var message: String? = null,

    @Column(name = "sent_at", comment = "전송 날짜", nullable = false)
    val sentAt: LocalDateTime = LocalDateTime.now(),
)