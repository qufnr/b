package space.byeoruk.b.domain.notification.repository

import org.springframework.data.jpa.repository.JpaRepository
import space.byeoruk.b.domain.notification.entity.Notification

interface NotificationRepository: JpaRepository<Notification, Long> {}
