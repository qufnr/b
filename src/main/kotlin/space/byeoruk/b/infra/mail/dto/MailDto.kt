package space.byeoruk.b.infra.mail.dto

class MailDto {
    class Content(
        val subject: String,
        val message: String,
        val secondaryMessage: String? = "",
        val actionUrl: String? = "",
        val code: String? = "",
    )
}