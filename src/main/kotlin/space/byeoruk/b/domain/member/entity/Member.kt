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
import java.time.LocalDateTime

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
@Entity
class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", nullable = false, unique = true, comment = "UID")
    private val uid: Long = 0;

    @Column(name = "id", length = 32, nullable = false, unique = true, comment = "계정")
    private val id: String = "";

    @Column(name = "password", length = 512, comment = "비밀번호")
    private val password: String = "";

    @Column(name = "name", length = 16, comment = "계정 명")
    private val name: String? = null;

    @Column(name = "avatar", length = 512, comment = "아바타 (파일명)")
    private val avatar: String? = null;

    @Column(name = "banner", length = 512, comment = "배너 (파일명)")
    private val banner: String? = null;

    @Column(name = "signed_at", nullable = false, comment = "계정 생성 날짜")
    private val signedAt: LocalDateTime = LocalDateTime.now();
}