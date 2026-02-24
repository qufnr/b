package space.byeoruk.b.domain.member.model

import space.byeoruk.b.global.model.EnumMapper

enum class FollowAcceptType(override val description: String, override val locale: String?): EnumMapper {
    DEFAULT("기본", "enum.follow-accept-type.default"),
    MANUAL("수락 필요", "enum.follow-accept-type.manual"),
    DENIED("받지 않음", "enum.follow-accept-type.denied");

    override val code: String
        get() = name
}
