package space.byeoruk.b.security.exception

import space.byeoruk.b.global.exception.CustomException

class TokenValidationException(key: String, args: Array<out Any>? = null):
    CustomException(key, args)
