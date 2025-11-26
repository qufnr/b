package space.byeoruk.b.global.exception

import java.lang.RuntimeException

open class CustomException: RuntimeException {
    constructor(message: String): super(message)
}