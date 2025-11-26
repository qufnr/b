package space.byeoruk.b.domain.member.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import space.byeoruk.b.domain.member.service.MemberService
import space.byeoruk.b.global.dto.ResponseDto

@RequestMapping(name = "/api/member-management/members")
@RestController
class MemberController(private val memberService: MemberService) {

    @GetMapping("/{uid}")
    fun read(@PathVariable uid: Long): ResponseEntity<*> {
        return ResponseEntity.ok().body(ResponseDto.build(memberService.read(uid), HttpStatus.OK))
    }
}