package space.byeoruk.b.domain.member.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import space.byeoruk.b.domain.member.dto.SignDto
import space.byeoruk.b.domain.member.service.SignService
import space.byeoruk.b.global.dto.ResponseDto

@Tag(name = "Member Sign", description = "로그인 API")
@RequestMapping("/api/member-management/signs")
@RestController
class SignController(private val signService: SignService) {
    @Operation(summary = "ID 로그인", description = "계정 로그인 전 ID 인증을 위한 처리입니다.")
    @PostMapping
    fun signId(@RequestBody request: SignDto.IdRequest): ResponseEntity<*> {
        val response = ResponseDto.build(signService.signId(request), HttpStatus.CREATED)

        return ResponseEntity.status(response.status).body(response)
    }
}