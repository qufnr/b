package space.byeoruk.b.domain.member.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import space.byeoruk.b.domain.member.dto.MemberDto
import space.byeoruk.b.domain.member.dto.MemberVerificationDto
import space.byeoruk.b.domain.member.exception.VerificationKeyAlreadyIssuedException
import space.byeoruk.b.domain.member.model.MemberRole
import space.byeoruk.b.domain.member.model.VerificationType
import space.byeoruk.b.global.entity.BaseEntity
import space.byeoruk.b.global.utility.ColourUtilities
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.Long

@Table(name = "member", comment = "계정")
@Entity
class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", nullable = false, unique = true, comment = "UID")
    var uid: Long = 0L,

    @Column(name = "id", length = 32, nullable = false, unique = true, comment = "계정")
    var id: String,

    @Column(name = "email", length = 64, nullable = false, unique = true, comment = "이메일")
    var email: String,

    //  계정 생성 시 입력받은 비밀번호를 암호화해야 해서 변경 가능해야 함.
    @Column(name = "password", length = 512, comment = "비밀번호")
    var password: String,

    @Column(name = "name", length = 16, comment = "계정 이름")
    var name: String? = null,

    @Column(name = "bio", length = 512, comment = "설명")
    var bio: String? = null,

    @Column(name = "colour", updatable = false, length = 8, comment = "색상")
    var colour: String,

    @Column(name = "avatar", length = 512, comment = "아바타 (파일명)")
    var avatar: String? = null,

    @Column(name = "banner", length = 512, comment = "배너 (파일명)")
    var banner: String? = null,

    @Column(name = "birthday", comment = "탄생일")
    var birthday: LocalDate? = null,

    @Column(name = "last_signed_at", nullable = false, comment = "마지막 로그인 날짜")
    var lastSignedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "last_name_changed_date", comment = "마지막 계정 이름 변경 날짜")
    var lastNameChangedDate: LocalDate? = null,

    @Column(name = "is_locked", comment = "잠금 여부")
    var isLocked: Boolean = false,

    @Column(name = "is_enabled", comment = "계정 활성화 여부")
    var isEnabled: Boolean = true,

    @Column(name = "is_verified", comment = "계정 인증 여부")
    var isVerified: Boolean = false,

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "member_privacy_uid", foreignKey = ForeignKey(name = "FK_member_TO_member_privacy"))
    var privacy: MemberPrivacy
): BaseEntity() {

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true)
    val authorities: MutableList<MemberAuthority> = mutableListOf()

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true)
    val verifications: MutableList<MemberVerification> = mutableListOf()

//    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true)
//    val histories: MutableList<MemberHistory> = mutableListOf()

    /**
     * 계정 생성 생성자
     */
    constructor(request: MemberDto.CreateRequest): this(
        id = request.id,
        email = request.email,
        password = request.password,
        name = request.name,
        bio = request.bio,
        colour = ColourUtilities.random(),
        privacy = MemberPrivacy()
    ) {
        addAuthority(MemberRole.ROLE_MEMBER)
        if(request.name != null && request.name.isNotBlank())
            lastNameChangedDate = LocalDate.now()
    }

    /**
     * 계정 수정
     */
    fun update(request: MemberDto.UpdateRequest) {
        if(request.name?.isNotBlank() == true && name != request.name)
            lastNameChangedDate = LocalDate.now()

        name = request.name ?: name
        bio = request.bio ?: bio
        birthday = request.birthday ?: birthday

        if(request.privacy != null) {
            privacy.profile = request.privacy.profile
            privacy.birthday = request.privacy.birthday
            privacy.feed = request.privacy.feed
        }
    }

    /**
     * 역할 추가
     *
     * @return role 역할
     */
    fun addAuthority(role: MemberRole) {
        authorities.add(MemberAuthority(this, role))
    }

    /**
     * 역할 설정
     *
     * @param roles 역할 목록
     */
    fun setAuthorities(roles: MutableList<MemberRole>) {
        authorities.clear()

        roles.forEach { role -> addAuthority(role) }
    }

    /**
     * 아바타 수정
     *
     * @param filename 서버에 저장된 파일 명 (`null` 일 경우 삭제)
     */
    fun updateAvatar(filename: String? = null) {
        avatar = filename
    }

    /**
     * 배너 수정
     *
     * @param filename 서버에 저장된 파일 명 (`null` 일 경우 삭제)
     */
    fun updateBanner(filename: String? = null) {
        banner = filename
    }

    /**
     * 인증 정보 추가
     *
     * @param type 인증 유형
     * @param key 인증 키
     * @param expiration 만료 시간 (분 단위)
     * @return 추가된 인증 정보 디테일
     */
    fun addVerification(type: VerificationType, key: String, expiration: Long): MemberVerificationDto.Details {
        //  같은 유형 중복 발급 제어
        if(verifications.any { it.type == type && it.expiredAt > LocalDateTime.now() })
            throw VerificationKeyAlreadyIssuedException()

        val memberVerification = MemberVerification(this, type, key, expiration)
        verifications.add(memberVerification)

        return MemberVerificationDto.Details.fromEntity(memberVerification)
    }
}