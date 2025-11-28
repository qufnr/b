package space.byeoruk.b.domain.member.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import space.byeoruk.b.domain.member.dto.MemberDto

@Table(name = "member_privacy")
@Entity
class MemberPrivacy(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", nullable = false, unique = true, comment = "UID")
    var uid: Long = 0L,

    @Column(name = "is_public", nullable = false, comment = "프로필 공개 여부")
    var isPublic: Boolean = true,

    @Column(name = "is_birthday_public", nullable = false, comment = "탄생일 공개 여부")
    var isBirthdayPublic: Boolean = true,
) {
    constructor(): this(
        isPublic = true,
        isBirthdayPublic = true
    )
}
