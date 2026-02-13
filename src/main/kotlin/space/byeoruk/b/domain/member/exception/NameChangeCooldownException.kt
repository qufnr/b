package space.byeoruk.b.domain.member.exception

import space.byeoruk.b.global.exception.CustomException

class NameChangeCooldownException: CustomException {
    constructor(days: Long): super("error.member.name.delayed", arrayOf(days))
}
