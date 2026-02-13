package space.byeoruk.b.domain.member.exception

import space.byeoruk.b.global.exception.CustomException

class PasswordConfirmMismatchException: CustomException {
    constructor(): super("error.member.password.confirm-mismatch")
}
