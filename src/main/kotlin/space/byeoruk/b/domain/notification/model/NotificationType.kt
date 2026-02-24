package space.byeoruk.b.domain.notification.model

import space.byeoruk.b.global.model.EnumMapper

enum class NotificationType(override val description: String, override val locale: String): EnumMapper {
    FOLLOW("팔로우", "enum.notification-type.follow"),
    FOLLOW_REQUEST("팔로우 요청", "enum.notification-type.follow-request"),
    GUILD_MENTION("멘션", "enum.notification-type.guild-mention"),
    SYSTEM("시스템 알림", "enum.notification-type.system");

    override val code: String
        get() = name
}
