package space.byeoruk.b.global.exception

import java.lang.RuntimeException

open class CustomException(
    val key: String,
    val args: Array<out Any>? = null
): RuntimeException(key)