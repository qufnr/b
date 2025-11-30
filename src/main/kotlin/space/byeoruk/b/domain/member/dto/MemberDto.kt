package space.byeoruk.b.domain.member.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.web.multipart.MultipartFile
import space.byeoruk.b.domain.member.entity.Member
import java.time.LocalDateTime

class MemberDto {
    /**
     * 계정 생성
     */
    class CreateRequest(
        @Schema(description = "계정 ID", example = "username1234")
        @NotBlank
        @Size(max = 32)
        val id: String,
        @Schema(description = "계정 비밀번호", example = "1q2w3e4r!")
        @NotBlank
        @Size(max = 64)
        val password: String,
        @Schema(description = "계정 비밀번호 확인", example = "1q2w3e4r!")
        @NotBlank
        val passwordConfirm: String,
        @Schema(description = "계정 이름", example = "김수한무")
        @Size(max = 16)
        val name: String,
        @Schema(description = "소개글", example = "<p>안녕하세요.</p><p>반갑습니다.</p>")
        @Size(max = 512)
        val bio: String,
    )

    /**
     * 계정 수정
     */
    class UpdateRequest(
        @Schema(description = "계정 이름", example = "김수한무")
        @Size(max = 16)
        val name: String,
        @Schema(description = "소개글", example = "<p>안녕하세요.</p><p>반갑습니다.</p>")
        @Size(max = 512)
        val bio: String,
    )

    /**
     * 계정 수정 :: 아바타, 배너
     */
    class ImageUpdateRequest(
        val avatar: MultipartFile,
        val banner: MultipartFile,
    )

    /**
     * 기본 계정 조회
     */
    class Details(
        @Schema(description = "계정 UID", example = "1")
        val uid: Long,
        @Schema(description = "계정 ID", example = "username1234")
        val id: String,
        @Schema(description = "계정 이름", example = "김수한무")
        val name: String?,
        @Schema(description = "소개글", example = "<p>안녕하세요.</p><p>반갑습니다.</p>")
        val bio: String?,
        @Schema(description = "아바타 이미지 파일", example = "filename.jpg")
        val avatar: String?,
        @Schema(description = "배너 이미지 파일", example = "filename.jpg")
        val banner: String?,
        @Schema(description = "마지막 로그인 날짜")
        val lastSignedAt: LocalDateTime,
        @Schema(description = "마지막 계정 이름 변경 날짜")
        val lastNameChangedAt: LocalDateTime?,
        val privacy: MemberPrivacyDto.Details
    ) {
        companion object {
            fun fromEntity(member: Member): Details {
                return Details(
                    member.uid!!,
                    member.id,
                    member.name,
                    member.bio,
                    member.avatar,
                    member.banner,
                    member.lastSignedAt,
                    member.lastNameChangedAt,
                    MemberPrivacyDto.Details.fromEntity(member.privacy!!)
                )
            }
        }
    }
}