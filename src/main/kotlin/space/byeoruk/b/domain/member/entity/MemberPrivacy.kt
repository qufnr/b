package space.byeoruk.b.domain.member.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import space.byeoruk.b.domain.member.model.FollowAcceptType
import space.byeoruk.b.domain.member.model.PrivacyStatus

@Table(name = "member_privacy")
@Entity
class MemberPrivacy(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", nullable = false, unique = true, comment = "UID")
    var uid: Long = 0L,

    @Enumerated(EnumType.STRING)
    @Column(name = "profile", nullable = false, comment = "프로필 공개 여부")
    var profile: PrivacyStatus = PrivacyStatus.PUBLIC,

    @Enumerated(EnumType.STRING)
    @Column(name = "birthday", nullable = false, comment = "탄생일 공개 여부")
    var birthday: PrivacyStatus = PrivacyStatus.FOLLOW_ONLY,

    @Enumerated(EnumType.STRING)
    @Column(name = "feed", nullable = false, comment = "피드 공개 여부")
    var feed: PrivacyStatus = PrivacyStatus.PUBLIC,

    @Enumerated(EnumType.STRING)
    @Column(name = "follow_accept", nullable = false, comment = "팔로우 수락 유형")
    val followAccept: FollowAcceptType = FollowAcceptType.DEFAULT
)