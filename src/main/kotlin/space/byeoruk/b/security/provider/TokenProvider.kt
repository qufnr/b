package space.byeoruk.b.security.provider

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SecurityException
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import space.byeoruk.b.global.utility.DateUtilities
import space.byeoruk.b.security.exception.TokenValidationException
import space.byeoruk.b.security.model.TokenType
import java.util.Date
import javax.crypto.SecretKey

@Component
open class TokenProvider(
    @Value($$"${bserver.jwt.secret}")
    private val secret: String,
    @Value($$"${bserver.jwt.issuer}")
    private val issuer: String,
    @Value($$"${bserver.jwt.expiration.sign}")
    private val signExpiration: Long,
    @Value($$"${bserver.jwt.expiration.password}")
    private val passwordExpiration: Long,
    @Value($$"${bserver.jwt.expiration.access}")
    private val accessExpiration: Long,
    @Value($$"${bserver.jwt.expiration.refresh}")
    private val refreshExpiration: Long,

    private var key: SecretKey? = null,
    private final val authorizationBearer: String = "Bearer "
) {
    @PostConstruct
    protected fun initialize() {
        key = Keys.hmacShaKeyFor(secret.toByteArray(Charsets.UTF_8))
    }

    /**
     * 토큰 생성
     *
     * @param value 토큰 Claim 에 담을 값
     * @param type 토큰 유형
     * @return JWT 문자열
     */
    fun createToken(value: Map<String, String>, type: TokenType): String {
        val date = Date()
        val jwts = Jwts.builder()

        value.forEach { (key, value) -> jwts.claim(key, value) }
        jwts.issuer(issuer)
            .issuedAt(date)
            .claim("type", type.name)
            .expiration(getExpirationByTokenType(type, date))
            .signWith(key)

        return jwts.compact()
    }

    /**
     * HTTP 요청 정보의 해더에서 사용자 토큰 추출
     *
     * @param bearerToken Authentication(혹은 사용자 지정)헤더에 있는 Bearer 토큰 문자열
     * @return JWT 문자열
     */
    fun getBearerToken(bearerToken: String): String {
        if(bearerToken.isBlank())
            throw TokenValidationException("error.token.missing")

        if(!bearerToken.startsWith(authorizationBearer))
            throw TokenValidationException("error.token.missing.bearer", arrayOf(authorizationBearer))

        return bearerToken.substring(authorizationBearer.length).trim()
    }

    fun isValidToken(token: String? = null): Boolean {
        if(token == null)
            throw TokenValidationException("error.token.missing")

        try {
            val payload = getTokenPayload(token)

            if(issuer != payload.issuer)
                throw TokenValidationException("error.token.invalid.issuer")

            //  여기서 토큰 유형이 없으면 예외 터짐
            val tokenType = TokenType.valueOf(payload["type"] as String)

            if(tokenType == TokenType.ACCESS && (payload["authorities"] == null || payload["authorities"].toString().isBlank()))
                throw TokenValidationException("error.token.invalid.authority")

            return !payload.expiration.before(Date())
        }
        catch(e: SecurityException) {
            throw TokenValidationException("error.token.invalid.signature")
        }
        catch(e: MalformedJwtException) {
            throw TokenValidationException("error.token.invalid")
        }
        catch(e: ExpiredJwtException) {
            throw TokenValidationException("error.token.expired")
        }
        catch(e: UnsupportedJwtException) {
            throw TokenValidationException("error.token.unsupported")
        }
        catch(e: IllegalArgumentException) {
            throw TokenValidationException("error.token.invalid.args", arrayOf("Argument", "Compact"))
        }
        catch(e: Exception) {
            throw e
        }
    }

    fun getTokenType(token: String): TokenType? {
        try {
            val payload = getTokenPayload(token)
            return TokenType.valueOf(payload["type"] as String)
        }
        catch(e: Exception) {
            return null
        }
    }

    /**
     * 토큰 Payload 반환
     *
     * @param token JWT 문자열
     * @return 토큰 Claims
     */
    fun getTokenPayload(token: String): Claims {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
    }

    /**
     * 토큰 유형으로 토큰 만료 시간 반환
     *
     * @param type 토큰 유형
     * @param standardDate 기준 시간 (선택, 없을 경우 현재 시간 기준으로 자동 설정)
     * @return 만료 시간 (Date 형식)
     */
    private fun getExpirationByTokenType(type: TokenType, standardDate: Date? = null): Date {
        var nowDateTime = DateUtilities.dateToLocalDateTime(standardDate ?: Date())

        nowDateTime = when(type) {
            TokenType.SIGN -> nowDateTime.plusMinutes(signExpiration)
            TokenType.PASSWORD -> nowDateTime.plusMinutes(passwordExpiration)
            TokenType.ACCESS -> nowDateTime.plusMinutes(accessExpiration)
            TokenType.REFRESH -> nowDateTime.plusMinutes(refreshExpiration)
        }

        return DateUtilities.localDateTimeToDate(nowDateTime)
    }
}