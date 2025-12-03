package space.byeoruk.b.security.exception

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerExceptionResolver

@Component
class AuthenticationEntryPointImpl: AuthenticationEntryPoint {
    private var resolver: HandlerExceptionResolver

    constructor(@Qualifier("handlerExceptionResolver") resolver: HandlerExceptionResolver) {
        this.resolver = resolver
    }

    override fun commence(request: HttpServletRequest, response: HttpServletResponse, authException: AuthenticationException) {
        resolver.resolveException(request, response, null, request.getAttribute("exception") as Exception)
    }
}