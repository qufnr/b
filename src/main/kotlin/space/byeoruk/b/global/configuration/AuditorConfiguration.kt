package space.byeoruk.b.global.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.context.SecurityContextHolder
import space.byeoruk.b.domain.member.details.MemberDetails
import java.util.Optional

@Configuration
class AuditorConfiguration {
    /**
     * Auditing 설정
     * Security Context 에서 인증된 계정 정보 찾고, 그 계정의 UID 를 `@CreatedBy`, `@LastModifiedBy` 어노테이션을 선언한 Entity 컬럼에 대입
     */
    @Bean
    fun auditorAware(): AuditorAware<Long> {
        val authentication = SecurityContextHolder.getContext().authentication

        if(authentication != null &&
            authentication.isAuthenticated &&
            !authentication.name.equals("anonymousUser")) {
            val memberDetails = authentication.principal as MemberDetails
            return AuditorAware { Optional.of<Long>(memberDetails.getIdentifier()) }
        }

        return AuditorAware { Optional.of<Long>(0L) }
    }
}