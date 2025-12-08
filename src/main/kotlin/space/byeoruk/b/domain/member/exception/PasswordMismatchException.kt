package space.byeoruk.b.domain.member.exception

import space.byeoruk.b.global.exception.CustomException

class PasswordMismatchException: CustomException {
    constructor(): super("비밀번호가 올바르지 않습니다.")
}
