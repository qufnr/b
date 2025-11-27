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
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.Long

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

    //  계정 생성 시 입력받은 비밀번호를 암호화해야 해서 변경 가능해야 함.
    @Column(name = "password", length = 512, comment = "비밀번호")
    var password: String = "",

    @Column(name = "name", length = 16, comment = "계정 이름")
    var name: String? = null,

    @Column(name = "bio", length = 512, comment = "설명")
    var bio: String? = null,

    @Column(name = "avatar", length = 512, comment = "아바타 (파일명)")
    var avatar: String? = null,

    @Column(name = "banner", length = 512, comment = "배너 (파일명)")
    var banner: String? = null,

    @Column(name = "birthday", comment = "탄생일")
    var birthday: LocalDate? = null,

    @Column(name = "last_signed_at", nullable = false, comment = "마지막 로그인 날짜")
    var lastSignedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "last_name_changed_at", nullable = false, comment = "마지막 계정 이름 변경 날짜")
    var lastNameChangedAt: LocalDateTime? = null,
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

    fun update(request: MemberDto.UpdateRequest) {
        name = request.name
        bio = request.bio
    }
}