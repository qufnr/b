package space.byeoruk.b.domain.member.model

import space.byeoruk.b.global.model.EnumMapper

/**
 * 사용 가능 유형
 */
enum class IdentityCanUse(override val description: String): EnumMapper {
    ID("ID"), EMAIL("이메일");

    override val code: String
        get() = name
}
