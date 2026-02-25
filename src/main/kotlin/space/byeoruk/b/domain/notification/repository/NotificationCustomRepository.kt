package space.byeoruk.b.domain.notification.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import space.byeoruk.b.domain.member.details.MemberDetails
import space.byeoruk.b.domain.notification.dto.NotificationDto
import space.byeoruk.b.domain.notification.entity.Notification

interface NotificationCustomRepository {
    fun findAllByPage(request: NotificationDto.ReadRequest, memberDetails: MemberDetails, pageable: Pageable): Page<Notification>

    fun deleteByRequest(request: NotificationDto.DeleteRequest, memberDetails: MemberDetails): Long
}