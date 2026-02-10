package space.byeoruk.b.global.utility

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.web.multipart.MultipartFile
import space.byeoruk.b.global.exception.FileUtilityException

private val log = KotlinLogging.logger {}

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
            throw FileUtilityException("파일 확장자가 올바르지 않습니다. ($extension)")
    }

    /**
     * 파일 크기 검증
     *
     * @param file 멀티파트 파일
     * @param maxFileSize 파일 크기 (MB)
     * @throws FileUtilityException 파일 크기가 초과되면 던짐
     */
    fun validateSize(file: MultipartFile, maxFileSize: Long) {
        val maxFileSizeBytes = maxFileSize * 1024 * 1024

        if(file.size > maxFileSizeBytes) {
            throw FileUtilityException("파일 크기를 초과했습니다. (${file.size} / $maxFileSizeBytes Bytes)")
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
