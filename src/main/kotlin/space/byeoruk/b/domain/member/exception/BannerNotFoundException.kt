package space.byeoruk.b.domain.member.exception

import space.byeoruk.b.global.exception.CustomException

class BannerNotFoundException: CustomException {
    constructor(): super("error.member.banner.not-found")
}
