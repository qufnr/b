package space.byeoruk.b.infra.mail.configuration

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

private val log = KotlinLogging.logger {}

@Configuration
class MailSenderConfiguration(
    @Value($$"${spring.mail.host}")
    private val mailHost: String,
    @Value($$"${spring.mail.port}")
    private val mailPort: Int,
    @Value($$"${spring.mail.username}")
    private val mailUsername: String,
    @Value($$"${spring.mail.password}")
    private val mailPassword: String,
    @Value($$"${spring.mail.properties.mail.smtp.auth}")
    private val mailSmtpAuth: Boolean,
    @Value($$"${spring.mail.properties.mail.smtp.timeout}")
    private val mailSmtpTimeout: Int,
    @Value($$"${spring.mail.properties.mail.smtp.starttls.enable}")
    private val mailSmtpStartTls: Boolean,
) {
    @Bean
    fun javaMailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl().apply {
            host = mailHost
            port = mailPort
            username = mailUsername
            password = mailPassword
        }

        val props = mailSender.javaMailProperties
        props["mail.transport.protocol"] = "smtp"
        props["mail.smtp.auth"] = mailSmtpAuth
        props["mail.smtp.starttls.enable"] = mailSmtpStartTls
        props["mail.smtp.timeout"] = mailSmtpTimeout

        log.info { "호스트: ${mailSender.host}" }
        log.info { "포트: ${mailSender.port}" }
        log.info { "계정 이름: ${mailSender.username}" }
        log.info { "비밀번호: ${mailSender.password}" }
        props.forEach { prop -> log.info { "${prop.key}: ${prop.value}" } }

        return mailSender
    }
}