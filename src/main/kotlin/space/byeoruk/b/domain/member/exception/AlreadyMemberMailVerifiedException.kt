package space.byeoruk.b.domain.member.exception

import space.byeoruk.b.global.exception.CustomException

class AlreadyMemberMailVerifiedException: CustomException {
    constructor(): super("error.member.email.already-verified")
}
