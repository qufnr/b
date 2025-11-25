package space.byeoruk.b

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class ProjectBApplication

fun main(args: Array<String>) {
    runApplication<ProjectBApplication>(*args)
}
