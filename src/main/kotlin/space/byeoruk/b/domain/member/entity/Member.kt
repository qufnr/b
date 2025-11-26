package space.byeoruk.b.domain.member.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import lombok.AccessLevel
import lombok.Getter
import lombok.NoArgsConstructor
import space.byeoruk.b.domain.member.dto.MemberDto
import space.byeoruk.b.global.entity.BaseEntity
import java.time.LocalDateTime
import kotlin.Long

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
@Entity
class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", nullable = false, unique = true, comment = "UID")
    val uid: Long? = null,

    @Column(name = "id", length = 32, nullable = false, unique = true, comment = "계정")
    val id: String = "",

    @Column(name = "password", length = 512, comment = "비밀번호")
    val password: String = "",

    @Column(name = "name", length = 16, comment = "계정 이름")
    val name: String? = null,

    @Column(name = "bio", length = 512, comment = "설명")
    val bio: String? = null,

    @Column(name = "avatar", length = 512, comment = "아바타 (파일명)")
    val avatar: String? = null,

    @Column(name = "banner", length = 512, comment = "배너 (파일명)")
    val banner: String? = null,

    @Column(name = "last_signed_at", nullable = false, comment = "마지막 로그인 날짜")
    val lastSignedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "last_name_changed_at", nullable = false, comment = "마지막 계정 이름 변경 날짜")
    val lastNameChangedAt: LocalDateTime? = null,
): BaseEntity() {
    /**
     * 계정 생성 생성자
     */
    constructor(request: MemberDto.CreateRequest) : this(
        id = request.id,
        password = request.password,
        name = request.name,
        bio = request.bio,
    )
}