package space.byeoruk.b.domain.member.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import space.byeoruk.b.domain.member.details.MemberDetails
import space.byeoruk.b.domain.member.dto.MemberDto
import space.byeoruk.b.domain.member.service.MemberService
import space.byeoruk.b.global.dto.ResponseDto

@Tag(name = "Member", description = "계정 API")
@RequestMapping("/api/member-management/members")
@RestController
class MemberController(private val memberService: MemberService) {

    @Operation(summary = "계정 조회", description = "인증된 계정 정보로 계정을 다시 조회합니다.")
    @GetMapping
    fun read(@AuthenticationPrincipal memberDetails: MemberDetails): ResponseEntity<*> {
        return ResponseEntity.ok().body(ResponseDto.build(memberService.read(memberDetails), HttpStatus.OK))
    }

    @Operation(summary = "계정 조회", description = "계정 UID 로 상세 계정 정보를 조회합니다.")
    @GetMapping("/{uid}")
    fun read(
        @Parameter(description = "계정 UID", example = "1")
        @PathVariable uid: Long): ResponseEntity<*> {
        return ResponseEntity.ok().body(ResponseDto.build(memberService.read(uid), HttpStatus.OK))
    }

    @Operation(summary = "계정 생성", description = "계정을 생성합니다.")
    @PostMapping
    fun create(@RequestBody request: MemberDto.CreateRequest): ResponseEntity<*> {
        val response = ResponseDto.build(memberService.create(request), HttpStatus.CREATED)
        return ResponseEntity.status(response.status).body(response)
    }

    @Operation(summary = "계정 수정", description = "계정 정보를 수정합니다.")
    @PutMapping
    fun update(
        @RequestBody request: MemberDto.UpdateRequest,
        @AuthenticationPrincipal memberDetails: MemberDetails): ResponseEntity<Void> {
        memberService.update(request, memberDetails)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "계정 리소스(아바타, 배너) 수정", description = "계정 아바타 또는 배너를 수정합니다.")
    @PutMapping("/resource")
    fun update(
        @RequestBody request: MemberDto.UpdateResourceRequest,
        @RequestPart file: MultipartFile,
        @AuthenticationPrincipal memberDetails: MemberDetails): ResponseEntity<Void> {
        memberService.update(request, file, memberDetails)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "계정 ID 또는 이메일 사용 가능 여부 확인", description = "입력한 계정 ID 또는 이메일을 사용할 수 있는지 검증합니다.")
    @GetMapping("/can-use")
    fun canUse(request: MemberDto.CanUseRequest): ResponseEntity<*> {
        return ResponseEntity.ok().body(memberService.canUse(request))
    }

    @Operation(summary = "계정 ID 찾기", description = "계정 ID를 잊어버렸을 때 찾아줍니다.")
    @PostMapping("/forget/id")
    fun forget(@RequestBody request: MemberDto.ForgetRequest): ResponseEntity<*> {
        memberService.forget(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(null)
    }

    @Operation(summary = "계정 비밀번호 찾기", description = "계정 비밀번호를 잊어버렸을 때 찾아줍니다.")
    @PostMapping("/forget/password")
    fun forgetPassword(@RequestBody request: MemberDto.ForgetRequest): ResponseEntity<*> {
        val response = ResponseDto.build(memberService.forgetPassword(request), HttpStatus.CREATED)
        return ResponseEntity.status(response.status).body(response)
    }

    @Operation(summary = "계정 비밀번호 변경", description = "계정 비밀번호를 변경합니다.")
    @PutMapping("/password")
    fun updatePassword(
        @RequestBody request: MemberDto.UpdatePasswordRequest,
        @RequestHeader("X-BServer-Password-Authorization") authorization: String
    ): ResponseEntity<Void> {
        memberService.updatePassword(request, authorization)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "이메일 인증 키 전송", description = "이메일 인증 키를 보냅니다.")
    @PostMapping("/verify/email/send")
    fun sendVerifyEmail(@AuthenticationPrincipal memberDetails: MemberDetails): ResponseEntity<*> {
        val response = ResponseDto.build(memberService.sendVerifyEmail(memberDetails), HttpStatus.CREATED)
        return ResponseEntity.status(response.status).body(response)
    }

    @Operation(summary = "이메일 인증", description = "발급 받은 인증 키로 이메일을 인증합니다.")
    @PutMapping("/verify/email")
    fun verifyEmail(
        @RequestBody request: MemberDto.VerifyEmailRequest,
        @AuthenticationPrincipal memberDetails: MemberDetails
    ): ResponseEntity<Void> {
        memberService.verifyEmail(request, memberDetails)
        return ResponseEntity.noContent().build()
    }
}