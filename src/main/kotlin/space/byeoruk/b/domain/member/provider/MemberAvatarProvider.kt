package space.byeoruk.b.domain.member.provider

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import space.byeoruk.b.domain.member.entity.Member
import space.byeoruk.b.domain.member.exception.MemberAvatarNotFoundException
import space.byeoruk.b.domain.member.exception.MemberResourceTransferFailedException
import space.byeoruk.b.global.provider.EntityFileProvider
import space.byeoruk.b.global.provider.ServerResourceProvider

@Component
class MemberAvatarProvider(
    @Value($$"${bserver.resource.member.avatar.size}")
    private val maximumSize: Long,
    @Value($$"${bserver.resource.member.avatar.extensions}")
    private val allowedExtensions: String,

    private val serverResourceProvider: ServerResourceProvider
): EntityFileProvider<Member> {
    //  서버 리소스 디렉터리 내 상위 디렉터리 명
    private final val directory = "avatar/"

    /**
     * 계정 아바타 서버에 저장
     *
     * @param entity 사용자 Entity
     * @param file 멀티파트 파일
     * @return 저장된 파일 명
     * @throws space.byeoruk.b.global.exception.FileUtilityException 파일 검증 실패 시 던짐
     * @throws MemberResourceTransferFailedException 파일 저장 실패 시 던짐
     */
    override fun save(entity: Member, file: MultipartFile): String {
        if(file.isEmpty)
            throw MemberAvatarNotFoundException()

        super.validate(file, allowedExtensions, maximumSize)

        try {
            val filename = serverResourceProvider.transfer(file, directory)

            //  기존 아바타 파일 있으면 삭제!!
            delete(entity)

            return filename
        }
        catch(e: Exception) {
            throw MemberResourceTransferFailedException(e);
        }
    }

    /**
     * 계정 아바타 서버에서 삭제
     *
     * @param entity 사용자 Entity
     * @return 삭제되었다면 `true` 아니면 `false` 반환
     */
    override fun delete(entity: Member): Boolean {
        return (entity.avatar != null && entity.avatar!!.isNotBlank()) &&
                serverResourceProvider.delete("${serverResourceProvider.path}${directory}${entity.avatar}")
    }
}