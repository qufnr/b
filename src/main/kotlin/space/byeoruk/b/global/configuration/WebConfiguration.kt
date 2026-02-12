package space.byeoruk.b.global.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import space.byeoruk.b.global.utility.SystemUtilities

@Configuration
class WebConfiguration(
    @Value($$"${bserver.resource-uri}") val resourceUri: String,

    @Value($$"${bserver.resource.windows}") val path: String,
    @Value($$"${bserver.resource.linux}") val linuxPath: String,
    @Value($$"${bserver.resource.mac}") val macOsPath: String
): WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        val os = SystemUtilities.getOsName()

        val resourcePath = when(os) {
            "windows" -> path
            "macos" -> macOsPath
            else -> linuxPath
        }
        val prefix = if(os == "windows") "file:///" else "file:/"

        registry.addResourceHandler("$resourceUri**")
            .addResourceLocations("$prefix$resourcePath")
    }
}