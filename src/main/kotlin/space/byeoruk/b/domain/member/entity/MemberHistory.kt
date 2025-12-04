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
import space.byeoruk.b.domain.member.dto.MemberHistoryDto
import space.byeoruk.b.domain.member.model.MemberHistoryType
import space.byeoruk.b.global.entity.BaseEntity

@Table(name = "member_history", comment = "계정 행동 기록")
@Entity
class MemberHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", nullable = false, unique = true, comment = "UID")
    var uid: Long = 0L,

    @Column(name = "type", length = 32, nullable = false, comment = "기록 유형")
    @Enumerated(EnumType.STRING)
    var type: MemberHistoryType,

    @Column(name = "before_value", length = 4096, comment = "이전 값")
    var before: String? = null,

    @Column(name = "after_value", length = 4096, nullable = false, comment = "이후 값")
    var after: String? = null,

    @Column(name = "ip_address", length = 32, nullable = false, comment = "IP 주소")
    var ipAddress: String,

    @Column(name = "user_agent", length = 2048, nullable = false, comment = "User Agent")
    var userAgent: String,

    @Column(name = "message", length = 256, nullable = false, comment = "메시지")
    var message: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_uid", nullable = false, comment = "계정 UID", foreignKey = ForeignKey(name = "FK_member_history_TO_member"))
    var member: Member,

): BaseEntity() {

    constructor(member: Member, before: String, after: String, record: MemberHistoryDto.RecordMap): this(
        type = record.type,
        before = before,
        after = after,
        ipAddress = record.ipAddress,
        userAgent = record.userAgent,
        message = record.message,
        member = member
    )
}