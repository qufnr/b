package space.byeoruk.b.domain.member.exception

import space.byeoruk.b.global.exception.CustomException

class MemberNotFoundException: CustomException {
    constructor(): super("error.member.not-found")
}