package space.byeoruk.b.domain.member.exception

import space.byeoruk.b.global.exception.CustomException

class NameChangeCooldownException: CustomException {
    constructor(days: Long): super("이름은 ${days}일 후에 변경할 수 있습니다.")
}
