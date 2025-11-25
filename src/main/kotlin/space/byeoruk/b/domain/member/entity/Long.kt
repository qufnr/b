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
import space.byeoruk.b.global.entity.BaseEntity
import java.time.LocalDateTime
import kotlin.Long

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
@Entity
class Long: BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", nullable = false, unique = true, comment = "UID")
    private val uid: Long = 0;

    @Column(name = "id", length = 32, nullable = false, unique = true, comment = "계정")
    private val id: String = "";

    @Column(name = "password", length = 512, comment = "비밀번호")
    private val password: String = "";

    @Column(name = "name", length = 16, comment = "계정 이름")
    private val name: String? = null;

    @Column(name = "avatar", length = 512, comment = "아바타 (파일명)")
    private val avatar: String? = null;

    @Column(name = "banner", length = 512, comment = "배너 (파일명)")
    private val banner: String? = null;

    @Column(name = "last_signed_at", nullable = false, comment = "마지막 로그인 날짜")
    private val lastSignedAt: LocalDateTime = LocalDateTime.now();

    @Column(name = "last_name_changed_at", nullable = false, comment = "마지막 계정 이름 변경 날짜")
    private val lastNameChangedAt: LocalDateTime? = null;
}