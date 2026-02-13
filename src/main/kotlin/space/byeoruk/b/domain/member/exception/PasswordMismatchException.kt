package space.byeoruk.b.domain.member.exception

import space.byeoruk.b.global.exception.CustomException

class PasswordMismatchException: CustomException {
    constructor(): super("error.member.password.mismatch")
}
