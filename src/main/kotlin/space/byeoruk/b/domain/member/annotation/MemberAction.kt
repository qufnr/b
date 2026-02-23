package space.byeoruk.b.domain.member.annotation

import space.byeoruk.b.domain.member.model.HistoryLevel

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class MemberAction (
    val type: HistoryLevel,
    val message: String = "",
    val trackUpdates: Boolean = false
)