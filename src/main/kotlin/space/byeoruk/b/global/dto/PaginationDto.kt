package space.byeoruk.b.global.dto

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import space.byeoruk.b.global.exception.IllegalPaginationArgumentException

class PaginationDto {
    open class Request(
        @Schema(description = "페이지 수", example = "0")
        var page: Int?,
        @Schema(description = "페이지 크기", example = "10")
        var size: Int?
    ) {
        fun nullToZero() {
            page = page ?: 0
            size = size ?: 10
        }

        fun toPageRequest(): PageRequest {
            if(page == null || size == null)
                throw IllegalPaginationArgumentException()

            return PageRequest.of(page!!, size!!)
        }
    }

    /**
     * 페이지네이션 응답
     */
    class Response<T>(
        val pagination: PaginationInfo,
        val items: List<T>
    ) {
        companion object {
            fun <T: Any> from(pageable: Pageable, items: Page<T>): Response<T> =
                Response(
                    pagination = PaginationInfo(
                        page = pageable.pageNumber,
                        size = pageable.pageSize,
                        totalItems = items.totalElements.toInt(),
                        totalPages = items.totalPages
                    ),
                    items = items.content
                )
        }
    }

    class PaginationInfo(
        @Schema(description = "페이지 수", example = "0")
        val page: Int,
        @Schema(description = "페이지 크기", example = "10")
        val size: Int,
        @Schema(description = "전체 항목 개수", example = "100")
        val totalItems: Int,
        @Schema(description = "전체 페이지 수", example = "10")
        val totalPages: Int
    )
}