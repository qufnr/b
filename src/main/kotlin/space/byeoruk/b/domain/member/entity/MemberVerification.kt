package space.byeoruk.b.domain.member.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import space.byeoruk.b.domain.member.model.VerificationType
import java.time.LocalDateTime

@Table(name = "member_verification")
@Entity
class MemberVerification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", unique = true, nullable = false, comment = "UID")
    var uid: Long = 0L,

    @Column(name = "verify_key", length = 256, nullable = false, comment = "인증 키")
    var key: String,

    @Column(name = "type", length = 32, nullable = false, comment = "키 유형")
    @Enumerated(EnumType.STRING)
    var type: VerificationType,

    @Column(name = "expired_at", nullable = false, comment = "만료 시간")
    var expiredAt: LocalDateTime,

    @Column(name = "used_at", comment = "사용 시간")
    var usedAt: LocalDateTime? = null,

    @ManyToOne
    @JoinColumn(name = "member_uid", comment = "계정 UID", foreignKey = ForeignKey(name = "FK_member_verification_TO_member"))
    var member: Member
) {
    constructor(member: Member, type: VerificationType, key: String, expiration: Long): this(
        key = key,
        type = type,
        expiredAt = LocalDateTime.now().plusMinutes(expiration),
        member = member
    )
}