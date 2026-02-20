package space.byeoruk.b.domain.member.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import space.byeoruk.b.domain.member.model.MemberPrivacyType

@Table(name = "member_privacy")
@Entity
class MemberPrivacy(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", nullable = false, unique = true, comment = "UID")
    var uid: Long = 0L,

    @Column(name = "profile", nullable = false, comment = "프로필 공개 여부")
    @Enumerated(EnumType.STRING)
    var profile: MemberPrivacyType,

    @Column(name = "birthday", nullable = false, comment = "탄생일 공개 여부")
    @Enumerated(EnumType.STRING)
    var birthday: MemberPrivacyType,

    @Column(name = "feed", nullable = false, comment = "피드 공개 여부")
    @Enumerated(EnumType.STRING)
    var feed: MemberPrivacyType,
) {
    constructor(): this(
        profile = MemberPrivacyType.PUBLIC,
        birthday = MemberPrivacyType.FOLLOW_ONLY,
        feed = MemberPrivacyType.PUBLIC
    )
}
