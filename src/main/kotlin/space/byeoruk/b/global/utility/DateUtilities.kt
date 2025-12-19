package space.byeoruk.b.global.utility

import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

object DateUtilities {
    /**
     * Date 를 LocalDateTime 으로 변환합니다.
     *
     * @param date java.util.Date 형식 날짜
     * @return java.time.LocalDateTime 날짜
     */
    fun dateToLocalDateTime(date: Date): LocalDateTime {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
    }

    /**
     * LocalDateTime 을 Date 로 변환합니다.
     *
     * @param dateTime java.time.LocalDateTime 형식 날짜
     * @return java.util.Date 날짜
     */
    fun localDateTimeToDate(dateTime: LocalDateTime): Date {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant())
    }
}
