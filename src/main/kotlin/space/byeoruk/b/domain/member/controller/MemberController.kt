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
        @AuthenticationPrincipal memberDetails: MemberDetails): ResponseEntity<*> {
        memberService.update(request, memberDetails)
        return ResponseEntity.noContent().build<ResponseDto<*>>()
    }

    @Operation(summary = "계정 리소스(아바타, 배너) 수정", description = "계정 아바타 또는 배너를 수정합니다.")
    @PutMapping("/resource")
    fun update(
        @RequestBody request: MemberDto.ResourceUpdateRequest,
        @RequestPart file: MultipartFile,
        @AuthenticationPrincipal memberDetails: MemberDetails): ResponseEntity<*> {
        memberService.update(request, file, memberDetails)
        return ResponseEntity.noContent().build<ResponseDto<*>>()
    }
}