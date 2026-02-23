package space.byeoruk.b.domain.common.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import space.byeoruk.b.domain.common.service.EnumService
import space.byeoruk.b.global.dto.ResponseDto

@Tag(name = "Enumeration", description = "서버에 정의된 Enum 관리")
@RequestMapping("/api/enum-management/enums")
@RestController
class EnumController(private val enumService: EnumService) {

    @Operation(summary = "공통 Enum 전체 조회", description = "클라이언트에서 사용할 공통 Enum 목록을 반환합니다.")
    @GetMapping
    fun read(): ResponseEntity<*> {
        val response = ResponseDto.build(enumService.read(), HttpStatus.OK, "Success!!!")
        return ResponseEntity.status(response.status).body(response)
    }

    @Operation(summary = "공통 Enum 단건 조회", description = "클라이언트에서 사용할 공통 Enum 목록을 반환합니다.")
    @GetMapping("/{name}")
    fun read(@PathVariable name: String): ResponseEntity<*> {
        val response = ResponseDto.build(enumService.read(name), HttpStatus.OK, "Success!!!")
        return ResponseEntity.status(response.status).body(response)
    }
}