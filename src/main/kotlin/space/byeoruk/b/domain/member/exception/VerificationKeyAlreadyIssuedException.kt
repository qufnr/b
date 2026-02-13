package space.byeoruk.b.domain.member.exception

import space.byeoruk.b.global.exception.CustomException

class VerificationKeyAlreadyIssuedException: CustomException {
    constructor(): super("error.member.verify-key.already-issued")
}
