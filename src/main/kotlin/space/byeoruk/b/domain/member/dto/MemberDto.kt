package space.byeoruk.b.domain.member.dto

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
        @NotBlank
        @Size(max = 32)
        val id: String,
        @NotBlank
        @Size(max = 64)
        val password: String,
        @NotBlank
        val passwordConfirm: String,
        @Size(max = 16)
        val name: String,
        @Size(max = 512)
        val bio: String,
    )

    /**
     * 계정 수정
     */
    class UpdateRequest(
        @Size(max = 16)
        val name: String,
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
        val uid: Long,
        val id: String,
        val name: String?,
        val bio: String?,
        val avatar: String?,
        val banner: String?,
        val lastSignedAt: LocalDateTime,
        val lastNameChangedAt: LocalDateTime?,
    ) {
        companion object {
            fun fromEntity(member: Member): Details {
                return Details(member.uid!!, member.id, member.name, member.bio, member.avatar, member.banner, member.lastSignedAt, member.lastNameChangedAt)
            }
        }
    }
}