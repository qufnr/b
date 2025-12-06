package space.byeoruk.b.infra.mail.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import space.byeoruk.b.infra.mail.dto.MailDto
import java.time.LocalDate

@Service
class MailSender(
    private val mailSender: JavaMailSender,
    @Value($$"${spring.application.name}")
    private var appName: String,
) {
    /**
     * 메일 보내기
     *
     * @param to 받는 사람
     * @param content 내용 객체 (MailDto.Content)
     */
    fun send(to: String, content: MailDto.Content) {
        val message = mailSender.createMimeMessage()

        val helper = MimeMessageHelper(message, true, "UTF-8")
        helper.setTo(to)
        helper.setSubject(content.subject)

        val template = loadTemplate()
        val html = renderTemplate(template, mapOf(
            "appNameInitial" to appName[0].toString().uppercase(),
            "appName" to appName,
            "title" to content.subject,
            "message" to content.message,
            "secondaryMessage" to content.secondaryMessage,
            "actionUrl" to content.actionUrl,
            "code" to content.code,
            "codeVisible" to if(content.code.isNullOrBlank()) "none" else "block",
            "actionUrlVisible" to if(content.actionUrl.isNullOrBlank()) "none" else "block",
            "contactMail" to "qufnr@kakao.com", //  TODO :: 임시로 하드코딩
            "year" to LocalDate.now().year.toString(),
        ))

        helper.setText(html, true)

        mailSender.send(message)
    }

    /**
     * resources 폴더 내에 있는 메일 템플릿 가져오기
     *
     * @return 메일 템플릿 HTML 문자열
     */
    private fun loadTemplate(): String {
        val resource = javaClass.getResource("/templates/mail-template.html")
        return resource!!.readText(Charsets.UTF_8)
    }

    /**
     * 메일 템플릿 플레이스홀더 치환
     *
     * @param template 메일 템플릿 HTML 문자열
     * @param values 치환할 키 벨류 맵
     * @return 템플릿 내 플레이스홀더가 값으로 치환된 메일 템플릿 HTML 문자열
     */
    private fun renderTemplate(template: String, values: Map<String, String?>): String {
        var templateContent = template
        for((key, value) in values) {
            templateContent = templateContent.replace("{{${key}}}", value ?: "")
        }

        return templateContent
    }
}