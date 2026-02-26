package space.byeoruk.b.socket.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import space.byeoruk.b.socket.handler.StompHandler

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfiguration(
    private val stompHandler: StompHandler,

    @Value($$"${bserver.app-url}")
    private val appUrl: String,
    @Value($$"${bserver.app-url-local}")
    private val appUrlLocal: String
): WebSocketMessageBrokerConfigurer {
    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        //  queue - 1:1 송신
        //  topic - 1:N 브로드케스팅
        registry.enableSimpleBroker("/queue", "/topic")
        registry.setApplicationDestinationPrefixes("/app")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws")
            .setAllowedOrigins(appUrl, appUrlLocal)
            .withSockJS()
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(stompHandler)
    }
}