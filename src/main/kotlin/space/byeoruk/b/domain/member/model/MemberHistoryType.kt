package space.byeoruk.b.domain.member.model

enum class MemberHistoryType(val description: String) {
    ID_SIGN("로그인 전 ID 검증"),
    SIGN("로그인"),
    SIGN_OUT("로그아웃"),
    SIGN_FAILED("로그인 실패"),
    ACCOUNT_FORGET("계정 찾기"),
    ACCOUNT_FORGET_PASSWORD("계정 비밀번호 찾기"),
    ACCOUNT_LOCKED("계정 잠김"),
    ACCOUNT_UNLOCKED("계정 잠금 해제"),
    ACCOUNT_UPDATED("계정 정보 업데이트"),
    ACCOUNT_RESOURCE_UPDATED("계정 리소스 업데이트"),
    ACCOUNT_PRIVACY_UPDATED("계정 프라이버시 업데이트"),
    ACCOUNT_PASSWORD_UPDATED("계정 비밀번호 업데이트")
}
