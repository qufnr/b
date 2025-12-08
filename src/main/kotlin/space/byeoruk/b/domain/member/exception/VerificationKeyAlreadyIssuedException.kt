package space.byeoruk.b.domain.member.exception

import space.byeoruk.b.global.exception.CustomException

class VerificationKeyAlreadyIssuedException: CustomException {
    constructor(): super("이미 발급된 인증 키가 존재합니다.")
}
