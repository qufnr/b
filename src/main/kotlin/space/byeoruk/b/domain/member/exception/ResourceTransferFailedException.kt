package space.byeoruk.b.domain.member.exception

import space.byeoruk.b.global.exception.CustomException

class ResourceTransferFailedException: CustomException {
    constructor(e: Exception): super("error.member.avatar.transfer-failed", arrayOf(e.message ?: "Error"))
}
