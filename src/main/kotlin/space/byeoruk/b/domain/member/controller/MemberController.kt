package space.byeoruk.b.domain.member.controller

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
import space.byeoruk.b.domain.member.details.MemberDetails
import space.byeoruk.b.domain.member.dto.MemberDto
import space.byeoruk.b.domain.member.service.MemberService
import space.byeoruk.b.global.dto.ResponseDto

@RequestMapping(name = "/api/member-management/members")
@RestController
class MemberController(private val memberService: MemberService) {

    @GetMapping("/{uid}")
    fun read(@PathVariable uid: Long): ResponseEntity<*> {
        return ResponseEntity.ok().body(ResponseDto.build(memberService.read(uid), HttpStatus.OK))
    }

    @PostMapping
    fun create(@RequestBody request: MemberDto.CreateRequest): ResponseEntity<*> {
        val response = ResponseDto.build(memberService.create(request), HttpStatus.CREATED)
        return ResponseEntity.status(response.status).body(response)
    }

    @PutMapping
    fun update(
        @RequestBody request: MemberDto.UpdateRequest,
        @RequestPart imageRequest: MemberDto.ImageUpdateRequest,
        @AuthenticationPrincipal memberDetails: MemberDetails): ResponseEntity<*> {
        memberService.update(request, imageRequest, memberDetails)
        return ResponseEntity.noContent().build<ResponseDto<*>>()
    }
}