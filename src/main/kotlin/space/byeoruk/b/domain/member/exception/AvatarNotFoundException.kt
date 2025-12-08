package space.byeoruk.b.domain.member.exception

import space.byeoruk.b.global.exception.CustomException

class AvatarNotFoundException: CustomException {
    constructor(): super("아바타가 없습니다.")
}
