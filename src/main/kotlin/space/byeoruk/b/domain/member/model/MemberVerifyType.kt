package space.byeoruk.b.domain.member.model

enum class MemberVerifyType(var description: String) {
    EMAIL_VERIFY("이메일 인증"),
    RESET_PASSWORD("비밀번호 초기화"),
}