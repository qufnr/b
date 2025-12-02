package space.byeoruk.b.domain.member.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import space.byeoruk.b.domain.member.dto.SignDto
import space.byeoruk.b.domain.member.service.SignService
import space.byeoruk.b.global.dto.ResponseDto

@Tag(name = "Member Sign", description = "로그인 API")
@RequestMapping("/api/member-management/signs")
@RestController
class SignController(private val signService: SignService) {
    @Operation(summary = "ID 로그인", description = "계정 로그인 전 ID 인증을 위한 처리입니다.")
    @PostMapping("/id")
    fun signId(@RequestBody request: SignDto.IdRequest): ResponseEntity<*> {
        val response = ResponseDto.build(signService.signId(request), HttpStatus.CREATED)

        return ResponseEntity.status(response.status).body(response)
    }

    @Operation(summary = "로그인", description = "계정으로 로그인합니다.")
    @PostMapping
    fun sign(@RequestBody request: SignDto.Request, @RequestHeader("X-BServer-Sign-Authentication") authentication: String): ResponseEntity<*> {
        val response = ResponseDto.build(signService.sign(request, authentication), HttpStatus.CREATED)

        return ResponseEntity.status(response.status).body(response)
    }

    @Operation(summary = "토큰 리프레시", description = "접근 토큰을 다시 발급 받습니다.")
    @PostMapping("/refresh")
    fun refresh(@RequestHeader("X-BServer-Refresh-Authentication") authentication: String): ResponseEntity<*> {
        val response = ResponseDto.build(signService.refresh(authentication), HttpStatus.CREATED)

        return ResponseEntity.status(response.status).body(response)
    }
}