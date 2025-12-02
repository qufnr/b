package space.byeoruk.b.global.provider

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import space.byeoruk.b.global.utility.FileUtilities
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.UUID

@Component
class ServerResourceProvider(
    @Value($$"${bserver.resource.path}") val path: String
) {
    /**
     * 멀티파트 파일을 서버 쪽으로 전송
     *
     * @param file 멀티파트 파일
     * @return 서버에 저장된 파일 명
     */
    fun transfer(file: MultipartFile): String {
        return transfer(file, null)
    }

    /**
     * 멀티파트 파일을 서버 쪽으로 전송
     *
     * @param file 멀티파트 파일
     * @param folder 폴더 명 (선택사항)
     * @return 서버에 저장된 파일 명
     */
    fun transfer(file: MultipartFile, folder: String? = null): String {
        var mergedPath =
            if(folder != null && folder.isNotBlank())
                path + (if(folder.startsWith("/")) folder.substring(1) else folder)
            else
                path
        mergedPath += if(mergedPath.endsWith("/")) "" else "/"

        val extension = FileUtilities.getExtension(file)
        val directory = File(mergedPath)
        if(!directory.exists())
            Files.createDirectories(Paths.get(mergedPath))

        val filename = String.format("%s.%s", UUID.randomUUID().toString(), extension)

        val fullPathname = String.format("%s%s", mergedPath, filename)
        file.transferTo(File(fullPathname))

        return filename
    }

    /**
     * 서버에 저장된 파일 삭제
     *
     * @param pathname 서버에 저장된 파일 전체 경로 (파일 명 포함)
     * @return 삭제했다면 true 아니면 false 반환
     */
    fun delete(pathname: String): Boolean {
        val file = File(pathname)

        return file.exists() && file.isFile && file.delete()
    }
}