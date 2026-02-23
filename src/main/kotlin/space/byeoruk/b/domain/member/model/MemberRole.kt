package space.byeoruk.b.domain.member.model

import space.byeoruk.b.global.model.EnumMapper

enum class MemberRole(override val description: String, override val locale: String): EnumMapper {
    ROLE_MEMBER("Member", "enum.member-role.member"),
    ROLE_STAFF("Staff", "enum.member-role.staff"),
    ROLE_ROOT("Root", "enum.member-role.root"),
    ROLE_DONATE("Donate", "enum.member-role.donate");

    override val code: String
        get() = name
}