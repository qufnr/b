package space.byeoruk.b.global.utility

object StringUtilities {
    /**
     * 문자열 마스킹 처리
     *
     * @return 마스킹된 문자열
     */
    fun String.mask(): String {
        if(this.isBlank())
            return this

        if(this.length <= 4)
            return this.first() + "*".repeat(this.length - 1)

        val prefix = this.take(2)
        val suffix = this.takeLast(2)
        val asterisks = "*".repeat(this.length - 4)

        return "$prefix$asterisks$suffix"
    }

    /**
     * 이메일 문자열 마스킹 처리
     *
     * @return 마스킹된 이메일 문자열
     */
    fun String.maskEmail(): String {
        if(this.isBlank())
            return this

        val parts = this.split("@")
        val domain = parts.getOrNull(1) ?: return this
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
