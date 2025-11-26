package space.byeoruk.b.global.dto

import lombok.Builder
import lombok.Data
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

class ResponseDto<T>(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val data: T? = null,
    val status: HttpStatus = HttpStatus.OK,
    val message: String = ""
) {

    companion object {
        fun <T>build(data: T, status: HttpStatus, message: String = ""): ResponseDto<T> {
            return ResponseDto(LocalDateTime.now(), data, status, message)
        }
    }
}