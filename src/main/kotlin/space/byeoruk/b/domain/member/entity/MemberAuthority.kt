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
import space.byeoruk.b.domain.member.model.MemberRole

@Table(name = "member_authority")
@Entity
class MemberAuthority(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", nullable = false, unique = true, comment = "UID")
    val uid: Long = 0L,

    @Column(name = "authority", length = 32, nullable = false, comment = "권한")
    @Enumerated(EnumType.STRING)
    var authority: MemberRole,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_uid", nullable = false, foreignKey = ForeignKey(name = "FK_member_authority_TO_member"))
    var member: Member
) {

    constructor(member: Member, role: MemberRole) : this(
        authority = role,
        member = member
    )
}