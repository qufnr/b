package space.byeoruk.b.domain.member.model

import space.byeoruk.b.global.model.EnumMapper

enum class HistoryLevel(override val description: String, override val locale: String): EnumMapper {
    ID_SIGN("로그인 전 ID 검증", "enum.history-level.id-sign"),
    SIGN("로그인", "enum.history-level.sign"),
    SIGN_OUT("로그아웃", "enum.history-level.sign-out"),
    SIGN_FAILED("로그인 실패", "enum.history-level.sign-failed"),
    ACCOUNT_FORGET("계정 찾기", "enum.history-level.account-forget"),
    ACCOUNT_FORGET_PASSWORD("계정 비밀번호 찾기", "enum.history-level.account-forget-password"),
    ACCOUNT_LOCKED("계정 잠김", "enum.history-level.account-locked"),
    ACCOUNT_UNLOCKED("계정 잠금 해제", "enum.history-level.account-unlocked"),
    ACCOUNT_UPDATED("계정 정보 업데이트", "enum.history-level.account-updated"),
    ACCOUNT_RESOURCE_UPDATED("계정 리소스 업데이트", "enum.history-level.account-resource-updated"),
    ACCOUNT_PRIVACY_UPDATED("계정 프라이버시 업데이트", "enum.history-level.account-privacy-updated"),
    ACCOUNT_PASSWORD_UPDATED("계정 비밀번호 업데이트", "enum.history-level.account-password-updated"),
    ACCOUNT_EMAIL_VERIFIED("계정 이메일 인증", "enum.history-level.account-email-verified");

    override val code: String
        get() = name
}
