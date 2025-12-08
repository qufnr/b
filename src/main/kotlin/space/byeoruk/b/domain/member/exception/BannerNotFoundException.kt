package space.byeoruk.b.domain.member.exception

import space.byeoruk.b.global.exception.CustomException

class BannerNotFoundException: CustomException {
    constructor(): super("배너가 없습니다.")
}
