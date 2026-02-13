package space.byeoruk.b.domain.member.exception

import space.byeoruk.b.global.exception.CustomException

class AvatarNotFoundException: CustomException {
    constructor(): super("error.member.avatar.not-found")
}
