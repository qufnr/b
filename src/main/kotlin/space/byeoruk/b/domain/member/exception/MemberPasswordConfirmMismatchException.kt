package space.byeoruk.b.domain.member.exception

import space.byeoruk.b.global.exception.CustomException

class MemberPasswordConfirmMismatchException: CustomException {
    constructor(): super("비밀번호와 비밀번호 재확인이 일치하지 않습니다.")
}
