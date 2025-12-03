package space.byeoruk.b.domain.member.provider

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import space.byeoruk.b.domain.member.entity.Member
import space.byeoruk.b.domain.member.exception.MemberBannerNotFoundException
import space.byeoruk.b.domain.member.exception.MemberResourceTransferFailedException
import space.byeoruk.b.global.provider.EntityFileProvider
import space.byeoruk.b.global.provider.ServerResourceProvider

@Component
class MemberBannerProvider(
    @Value($$"${bserver.resource.member.banner.size}")
    private val maximumSize: Long,
    @Value($$"${bserver.resource.member.banner.extensions}")
    private val allowedExtensions: String,

    private val serverResourceProvider: ServerResourceProvider
): EntityFileProvider<Member> {
    private final val directory = "banner/"

    override fun save(entity: Member, file: MultipartFile): String {
        if(file.isEmpty)
            throw MemberBannerNotFoundException()

        super.validate(file, allowedExtensions, maximumSize)

        try {
            val filename = serverResourceProvider.transfer(file, directory)

            delete(entity)

            return filename
        }
        catch(e: Exception) {
            throw MemberResourceTransferFailedException(e)
        }
    }

    override fun delete(entity: Member): Boolean {
        return (entity.avatar != null && entity.avatar!!.isNotBlank()) &&
                serverResourceProvider.delete("${serverResourceProvider.path}${directory}${entity.avatar}")
    }
}