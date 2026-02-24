package space.byeoruk.b.domain.member.details

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import space.byeoruk.b.domain.member.dto.MemberDto
import space.byeoruk.b.domain.member.model.MemberRole

class MemberDetails(
    private val uid: Long,
    private val id: String,
    private val password: String,
    private val isNonLocked: Boolean,
    private val isEnabled: Boolean,
): UserDetails {
    private var authorities: List<MemberRole> = emptyList()

    constructor(member: MemberDto.Details, password: String): this(
        uid = member.uid,
        id = member.id,
        password = password,
        isNonLocked = !member.isLocked,
        isEnabled = member.isEnabled,
    ) {
        this.authorities = member.authorities
    }

    fun getIdentifier(): Long {
        return uid
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authorities.map { authority -> SimpleGrantedAuthority(authority.name) }
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return id
    }

    override fun isAccountNonLocked(): Boolean {
        return isNonLocked
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return isEnabled
    }
}