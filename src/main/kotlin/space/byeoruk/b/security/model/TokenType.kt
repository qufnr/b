package space.byeoruk.b.security.model

enum class TokenType(private val description: String) {
    SIGN("ID 검증 토큰"),
    ACCESS("접근 토큰"),
    REFRESH("리프레시 토큰"),
    PASSWORD("비밀번호 변경"),
}