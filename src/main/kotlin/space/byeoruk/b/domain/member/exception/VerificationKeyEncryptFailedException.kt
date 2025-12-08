package space.byeoruk.b.domain.member.exception

import space.byeoruk.b.global.exception.CustomException

class VerificationKeyEncryptFailedException: CustomException {
    constructor(): super("인증 키를 암호화하는 데 실패했습니다.")
}
