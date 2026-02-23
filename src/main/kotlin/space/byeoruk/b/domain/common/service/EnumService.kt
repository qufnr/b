package space.byeoruk.b.domain.common.service

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import space.byeoruk.b.domain.common.exception.EnumMapperNotFoundException
import space.byeoruk.b.domain.member.model.HistoryLevel
import space.byeoruk.b.domain.member.model.MemberRole
import space.byeoruk.b.domain.member.model.PrivacyStatus
import space.byeoruk.b.global.model.EnumMapper

@Service
class EnumService(private val messageSource: MessageSource) {
    /**
     * 모든 열거형 변수 조회
     *
     * @return Enum 목록
     */
    fun read(): Map<String, List<Map<String, Any?>>> = getReadableEnums()

    /**
     * Enum 이름으로 열거형 변수 조회
     *
     * @param name Enum 이름
     * @return Enum 코드 목록
     */
    fun read(name: String): List<Map<String, Any?>> = getReadableEnums()[name] ?: throw EnumMapperNotFoundException()

    /**
     * 조회 가능한 열거형 변수 반환
     *
     * @return 항목
     */
    private fun getReadableEnums(): Map<String, List<Map<String, Any?>>> =
        mapOf(
            "MemberRole" to toEnumValues(MemberRole.entries),
            "HistoryLevel" to toEnumValues(HistoryLevel.entries),
            "PrivacyStatus" to toEnumValues(PrivacyStatus.entries),
        )

    /**
     * 열거형 변수 값 가져오기
     *
     * @param entries 엔트리
     * @return code, description, localizedDescription
     */
    private fun toEnumValues(entries: List<EnumMapper>): List<Map<String, Any?>> {
        val locale = LocaleContextHolder.getLocale()

        return entries.map {
            mapOf(
                "code" to it.code,
                "description" to it.description,
                "localizedDescription" to messageSource.getMessage(it.locale!!, null, it.locale!!, locale)
            )
        }
    }
}
