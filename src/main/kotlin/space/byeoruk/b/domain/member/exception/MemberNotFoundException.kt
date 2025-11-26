package space.byeoruk.b.domain.member.exception

import space.byeoruk.b.global.exception.CustomException

class MemberNotFoundException: CustomException {
    constructor(): super("존재하지 않는 계정입니다.")
}