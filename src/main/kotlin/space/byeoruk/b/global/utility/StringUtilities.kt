package space.byeoruk.b.global.utility

class StringUtilities {
    companion object {
        /**
         * 문자열 마스킹 처리
         *
         * @param value 문자열
         * @return 마스킹된 문자열
         */
        fun mask(value: String): String {
            if(value.isBlank())
                return value

            if(value.length <= 4)
                return value.first() + "*".repeat(value.length - 1)

            val prefix = value.take(2)
            val suffix = value.takeLast(2)
            val asterisks = "*".repeat(value.length - 4)

            return "$prefix$asterisks$suffix"
        }

        /**
         * 이메일 문자열 마스킹 처리
         *
         * @param email 이메일 문자열
         * @return 마스킹된 이메일 문자열
         */
        fun maskEmail(email: String): String {
            if(email.isBlank())
                return email

            val parts = email.split("@")
            val domain = parts.getOrNull(1) ?: return email
            val local = parts[0]

            val visibleLocal = local.take(3)
            val asterisks = "*".repeat(local.length - visibleLocal.length)

            return "$visibleLocal$asterisks@$domain"
        }

        /**
         * 무작위 문자열을 특정 길이 만큼 생성하고 반환
         *
         * @param length 문자열 길이
         * @return 무작위 문자열
         */
        fun random(length: Int): String {
            val chars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
            return buildString(length) {
                repeat(length) {
                    append(chars.random())
                }
            }
        }
    }
}