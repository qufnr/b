package space.byeoruk.b.global.exception

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.ConstraintViolationException
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mail.MailAuthenticationException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.transaction.TransactionSystemException
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException
import space.byeoruk.b.global.dto.ResponseDto
import space.byeoruk.b.global.dto.ValidationDto
import space.byeoruk.b.security.exception.TokenValidationException
import java.util.Locale
import java.util.stream.Collectors

private val log = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler(
    private val messageSource: MessageSource
) {
    /**
     * 정의안한 예외
     *
     * @param e 일반 예외
     * @return 응답
     */
    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(e: Exception): ResponseEntity<*> {
        log.error(e) { "Exception" }

        return ResponseEntity.internalServerError()
            .body(ResponseDto.build(HttpStatus.INTERNAL_SERVER_ERROR, e.message!!))
    }

    /**
     * 사용자 지정 예외
     *
     * @param e CustomException
     * @return 응답
     */
    @ExceptionHandler(CustomException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleException(e: CustomException): ResponseEntity<*> {
        log.error(e) { "사용자 지정 예외" }

        return ResponseEntity.badRequest()
            .body(ResponseDto.build(HttpStatus.BAD_REQUEST, getLocaleMessage(e, e.key, e.args)))
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleException(e: NoHandlerFoundException): ResponseEntity<*> {
        log.error(e) { "찾을 수 없음 예외" }

        val response = ResponseDto.build(HttpStatus.NOT_FOUND, getLocaleMessage(e, "error.not-found"))
        return ResponseEntity.status(response.status)
            .body(response)
    }

    /**
     * 인증 실패 예외
     *
     * @param e AuthenticationException
     * @return 응답
     */
    @ExceptionHandler(AuthenticationException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleException(e: AuthenticationException): ResponseEntity<*> {
        log.error(e) { "인증 실패 예외" }

        val response = ResponseDto.build(HttpStatus.UNAUTHORIZED, getLocaleMessage(e, "error.authentication"))
        return ResponseEntity.status(response.status)
            .body(response)
    }

    @ExceptionHandler(BadCredentialsException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleException(e: BadCredentialsException): ResponseEntity<*> {
        log.error(e) { "자격 증명 실패 예외" }

        val response = ResponseDto.build(HttpStatus.UNAUTHORIZED, getLocaleMessage(e, "error.bad-credentials"))
        return ResponseEntity.status(response.status)
            .body(response)
    }

    @ExceptionHandler(TokenValidationException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleException(e: TokenValidationException): ResponseEntity<*> {
        log.error(e) { "토큰 검증 실패 에외" }

        val response = ResponseDto.build(HttpStatus.UNAUTHORIZED, getLocaleMessage(e, e.key, e.args))
        return ResponseEntity.status(response.status)
            .body(response)
    }

    /**
     * 접근 거부 예외
     *
     * @param e AccessDeniedException
     * @return 응답
     */
    @ExceptionHandler(AccessDeniedException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleException(e: AccessDeniedException): ResponseEntity<*> {
        log.error(e) { "접근 거부 예외" }

        val response = ResponseDto.build(HttpStatus.FORBIDDEN, getLocaleMessage(e, "error.access-denied"))
        return ResponseEntity.status(response.status)
            .body(response)
    }

    /**
     * Validation 예외
     *
     * @param e MethodArgumentNotValidException
     * @return 응답
     */
    @ExceptionHandler(MethodArgumentNotValidException::class, BindException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleException(e: MethodArgumentNotValidException): ResponseEntity<*> {
        log.error(e) { "Validation Exception" }

        val fieldError = e.bindingResult.fieldErrors.firstOrNull()
        val message = fieldError?.defaultMessage ?: getLocaleMessage(e, "error.request.invalid")

        val response = ResponseDto.build(HttpStatus.BAD_REQUEST, message)

        return ResponseEntity.status(response.status)
            .body(response)

//        val errors = e.bindingResult.fieldErrors
//        val response = ResponseDto.build(errors.parallelStream()
//            .map { error ->
//                ValidationDto(
//                    getLocaleMessage(e, error.defaultMessage!!, arrayOf(error.field, error.rejectedValue.toString())),
//                    error.field,
//                    error.rejectedValue.toString())
//            }.collect(Collectors.toList()), HttpStatus.BAD_REQUEST, getLocaleMessage(e, "error.request.invalid"))
//
//        return ResponseEntity.status(response.status)
//            .body(response)
    }

    @ExceptionHandler(MailAuthenticationException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(e: MailAuthenticationException): ResponseEntity<*> {
        log.error(e) { "메일 서버 인증 실패 예외" }

        val response = ResponseDto.build(HttpStatus.INTERNAL_SERVER_ERROR, getLocaleMessage(e, "error.mail.authentication"))
        return ResponseEntity.status(response.status).body(response)
    }

    /**
     * 트렌젝션 예외
     *
     * @param e TransactionSystemException
     * @return 응답 (예외의 Root Cause 가 ConstraintViolationException 이면 하단 예외로 반환)
     */
    @ExceptionHandler(TransactionSystemException::class)
    fun handleException(e: TransactionSystemException): ResponseEntity<*> {
        if(e.rootCause is ConstraintViolationException)
            return handleException(e.rootCause as ConstraintViolationException)

        return handleException(e)
    }

    /**
     * 테이블 Validation 예외
     *
     * @param e ConstraintViolationException
     * @return 응답
     */
    @ExceptionHandler(ConstraintViolationException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(e: ConstraintViolationException): ResponseEntity<*> {
        log.error(e) { "Database Table Validation Exception" }

        val constraintViolations = e.constraintViolations;
        val response = ResponseDto.build(constraintViolations.parallelStream()
            .map { error ->
                ValidationDto(
                    error.message!!,
                    error.propertyPath.toString(),
                    error.invalidValue.toString())
            }.collect(Collectors.toList()), HttpStatus.INTERNAL_SERVER_ERROR, getLocaleMessage(e, "error.database.constraint-violation"))

        return ResponseEntity.status(response.status)
            .body(response)
    }

    /**
     * 예외 로케일 메시지 가져오기
     *
     * @param e 예외
     * @param key 로케일 키
     * @param args 로케일 매개변수
     * @return 메시지 문자열
     */
    private fun getLocaleMessage(e: Exception, key: String, args: Array<out Any>? = null): String {
        val locale: Locale = LocaleContextHolder.getLocale()
        return messageSource.getMessage(key, args, "error.default", locale) ?: e.localizedMessage
    }
}