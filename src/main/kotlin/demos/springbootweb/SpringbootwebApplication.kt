package demos.springbootweb

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class SpringbootwebApplication

fun main(args: Array<String>) {
    SpringApplication.run(SpringbootwebApplication::class.java, *args)
}
