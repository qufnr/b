package space.byeoruk.b.global.utility

object SystemUtilities {
    /**
     * 서버 OS 반환
     *
     * @return OS 이름 (windows, macos, linux)
     */
    fun getOsName(): String {
        val os = System.getProperty("os.name").lowercase()

        if(os.contains("win"))
            return "windows"

        if(os.contains("mac"))
            return "macos"

        return "linux"
    }
}