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
            throw TokenValidationException("토큰이 누락되었습니다.")

        if(!bearerToken.startsWith(authorizationBearer))
            throw TokenValidationException("토큰에 \"%s\"이(가) 누락되었습니다.".format(authorizationBearer))

        return bearerToken.substring(authorizationBearer.length).trim()
    }

    fun isValidToken(token: String? = null): Boolean {
        if(token == null)
            throw TokenValidationException("토큰이 누락되었습니다.")

        try {
            val payload = getTokenPayload(token)

            if(issuer != payload.issuer)
                throw TokenValidationException("발행자가 올바르지 않습니다.")

            //  여기서 토큰 유형이 없으면 예외 터짐
            val tokenType = TokenType.valueOf(payload["type"] as String)

            if(tokenType == TokenType.ACCESS && (payload["authorities"] == null || payload["authorities"].toString().isBlank()))
                throw TokenValidationException("계정 역할이 배분되지 않았습니다.")

            return !payload.expiration.before(Date())
        }
        catch(e: SecurityException) {
            throw TokenValidationException("토큰 시그니처가 올바르지 않습니다.")
        }
        catch(e: MalformedJwtException) {
            throw TokenValidationException("토큰이 유효하지 않습니다.")
        }
        catch(e: ExpiredJwtException) {
            throw TokenValidationException("토큰이 유효 기간이 만료되었습니다.")
        }
        catch(e: UnsupportedJwtException) {
            throw TokenValidationException("지원하지 않는 토큰입니다.")
        }
        catch(e: IllegalArgumentException) {
            throw TokenValidationException("토큰의 Argument 혹은 Compact 가 올바르지 않습니다.")
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
     * @param now 현재 시간
     * @return 만료 시간 (Date 형식)
     */
    private fun getExpirationByTokenType(type: TokenType, now: Date): Date {
        var nowDateTime = DateUtilities.dateToLocalDateTime(now)

        nowDateTime = when(type) {
            TokenType.SIGN -> nowDateTime.plusMinutes(signExpiration)
            TokenType.ACCESS -> nowDateTime.plusMinutes(accessExpiration)
            TokenType.REFRESH -> nowDateTime.plusMinutes(refreshExpiration)
        }

        return DateUtilities.localDateTimeToDate(nowDateTime)
    }
}