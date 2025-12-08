package space.byeoruk.b.domain.member.exception

import space.byeoruk.b.global.exception.CustomException

class ResourceTransferFailedException: CustomException {
    constructor(e: Exception): super("아바타 파일을 업로드하는 데 실패했습니다. ${e.message}")
}
