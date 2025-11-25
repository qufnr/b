package space.byeoruk.b.global.configuration

import lombok.RequiredArgsConstructor
import org.springframework.data.domain.AuditorAware
import org.springframework.stereotype.Component
import java.util.Optional

@Component
@RequiredArgsConstructor
class AuditorAwareImpl: AuditorAware<Long> {
    /**
     * created_by, modified_by 에 계정 UID 담기
     */
    override fun getCurrentAuditor(): Optional<Long> {
        //  TODO :: 계정 UID 넘기기
        return Optional.ofNullable(0)
    }
}