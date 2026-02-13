package space.byeoruk.b.domain.member.exception

import space.byeoruk.b.global.exception.CustomException

class InvalidVerificationKeyException: CustomException {
    constructor(): super("error.member.verify-key.invalid")
}
