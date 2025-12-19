package space.byeoruk.b.domain.member.exception

import space.byeoruk.b.global.exception.CustomException

class AlreadyMemberMailVerifiedException: CustomException {
    constructor(): super("이미 이메일이 인증된 계정입니다.")
}
