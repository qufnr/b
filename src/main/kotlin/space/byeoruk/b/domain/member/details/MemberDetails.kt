package space.byeoruk.b.domain.member.details

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import space.byeoruk.b.domain.member.entity.Member
import space.byeoruk.b.domain.member.model.MemberRole
import java.util.stream.Collectors

class MemberDetails(
    private val uid: Long,
    private val id: String,
    private val password: String,
): UserDetails {
    private var authorities: Array<MemberRole> = emptyArray()

    constructor(member: Member): this(
        uid = member.uid!!,
        id = member.id,
        password = member.password,
    ) {
        this.authorities = member.authorities.map { authority -> authority.authority }.toTypedArray()
    }

    fun getIdentity(): Long {
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
        return true //  TODO :: 계정 잠금 여부 만들기
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}