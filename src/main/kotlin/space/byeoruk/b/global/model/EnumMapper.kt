package space.byeoruk.b.global.model

interface EnumMapper {
    val code: String
    val description: String
    val locale: String? get() = null
}