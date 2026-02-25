package space.byeoruk.b.domain.notification.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import space.byeoruk.b.domain.notification.entity.Notification
import java.util.Optional

interface NotificationRepository: JpaRepository<Notification, Long>, NotificationCustomRepository {
    @Query("select n from Notification n where n.uid = :uid and n.receiver.uid = :receiverUid")
    fun findByUidAndReceiverUid(uid: Long, receiverUid: Long): Optional<Notification>
}