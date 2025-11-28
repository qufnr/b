package space.byeoruk.b.security.exception

import space.byeoruk.b.global.exception.CustomException

class TokenInvalidTypeException: CustomException {
    constructor(): super("알 수 없는 토큰 유형입니다.")
}
