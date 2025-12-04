package space.byeoruk.b.domain.member.annotation

import space.byeoruk.b.domain.member.model.MemberHistoryType

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class MemberAction (
    val type: MemberHistoryType,
    val trackUpdates: Boolean = false
)