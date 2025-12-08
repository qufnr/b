package space.byeoruk.b.domain.member.exception

import space.byeoruk.b.global.exception.CustomException

class InvalidVerificationKeyException: CustomException {
    constructor(): super("인증 키가 올바르지 않습니다.")
}
