package space.byeoruk.b.domain.member.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import space.byeoruk.b.domain.member.model.FollowStatus
import space.byeoruk.b.global.entity.BaseEntity

@Entity
@Table(name = "member_follow", uniqueConstraints = [UniqueConstraint(columnNames = ["member_follower_uid", "member_followee_uid"])])
class MemberFollow(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", nullable = false, unique = true, comment = "UID")
    val uid: Long = 0L,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", comment = "팔로우 상태", nullable = false, length = 32)
    val status: FollowStatus = FollowStatus.FOLLOW,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_follower_uid", nullable = false, foreignKey = ForeignKey(name = "FK_member_follow_follower_TO_member"))
    val follower: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_followee_uid", nullable = false, foreignKey = ForeignKey(name = "FK_member_follow_followee_TO_member"))
    val followee: Member
): BaseEntity()