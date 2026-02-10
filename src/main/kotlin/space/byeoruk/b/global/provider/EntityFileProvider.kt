package space.byeoruk.b.global.provider

import org.springframework.web.multipart.MultipartFile
import space.byeoruk.b.global.exception.FileUtilityException
import space.byeoruk.b.global.utility.FileUtilities

interface EntityFileProvider<T> {
    fun save(entity: T, file: MultipartFile): String
    fun delete(entity: T): Boolean

    /**
     * 파일 유효성 검증
     *
     * @param file 멀티파트 파일
     * @param extensions 허용 확장자 문자열 콤마(,)로 구분
     * @param size 파일 최대 허용 크기
     * @throws FileUtilityException 유효성 검증 실패 시 던짐
     */
    fun validate(file: MultipartFile, extensions: String, size: Long) {
        FileUtilities.validateExtension(file, extensions.lowercase().split(","))
        FileUtilities.validateSize(file, size)
    }
}