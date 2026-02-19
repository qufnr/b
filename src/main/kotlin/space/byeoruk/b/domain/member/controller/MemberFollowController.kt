package space.byeoruk.b.domain.member.controller

import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import space.byeoruk.b.domain.member.details.MemberDetails
import space.byeoruk.b.domain.member.service.MemberFollowService
import space.byeoruk.b.global.dto.ResponseDto

@RequestMapping("/api/member-management/follows")
@RestController
class MemberFollowController(
    private val memberFollowService: MemberFollowService,
) {
    @Operation(summary = "팔로우/언팔로우 처리", description = "대상을 팔로우/언팔로우 합니다.")
    @PostMapping("/toggle/{uid}")
    fun toggle(@PathVariable uid: Long, @AuthenticationPrincipal memberDetails: MemberDetails): ResponseEntity<*> {
        val response = ResponseDto.build(memberFollowService.toggle(uid, memberDetails), HttpStatus.CREATED, "Success!!!")
        return ResponseEntity.status(response.status).body(response)
    }
}