package space.byeoruk.b.global.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity(
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @CreatedBy
    @Column(name = "created_by")
    val createdBy: kotlin.Long? = null,

    @LastModifiedDate
    @Column(name = "modified_at")
    val modifiedAt: LocalDateTime? = null,

    @LastModifiedBy
    @Column(name = "modified_by")
    val modifiedBy: kotlin.Long? = null,
)