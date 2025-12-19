package space.byeoruk.b.global.utility

import org.springframework.web.multipart.MultipartFile
import space.byeoruk.b.global.exception.FileUtilityException

object FileUtilities {
    /**
     * 파일 확장자 검증
     *
     * @param file 멀티파트 파일
     * @param extensions 허용 확장자 목록
     * @throws FileUtilityException 파일 명을 읽지 못했거나, 파일 확장자가 허용 확장자에 없으면 던짐
     */
    fun validateExtension(file: MultipartFile, extensions: List<String>) {
        val extension = getExtension(file)
        if(!extensions.contains(extension))
            throw FileUtilityException("파일 확장자가 올바르지 않습니다.")
    }

    /**
     * 파일 크기 검증
     *
     * @param file 멀티파트 파일
     * @param size 파일 크기
     * @throws FileUtilityException 파일 크기가 초과되면 던짐
     */
    fun validateSize(file: MultipartFile, size: Long) {
        if(file.size > size) {
            val fileSize = file.size / 1000 / 1000
            val maximumFileSize = size / 1000 / 1000
            throw FileUtilityException("파일 크기를 초과했습니다. ($fileSize / $maximumFileSize MB)")
        }
    }

    /**
     * 파일 확장자 반환
     *
     * @param file 멀티파트 파일
     * @return 파일 확장자
     * @throws FileUtilityException 파일 명을 읽지 못하면 던짐
     */
    fun getExtension(file: MultipartFile): String {
        val filename = file.originalFilename
        if(filename != null && filename.isNotBlank())
            return filename.lowercase().substring(filename.lastIndexOf(".") + 1)
        throw FileUtilityException("파일 명을 읽는 데 실패했습니다.")
    }
}
