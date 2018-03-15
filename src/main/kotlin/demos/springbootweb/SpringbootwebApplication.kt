package demos.springbootweb

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

// All you need is a class annotated with SpringBootApplication, SpringBoot will do the rest
@SpringBootApplication
class SpringbootwebApplication

// Process entry-point
fun main(args: Array<String>) {
    SpringApplication.run(SpringbootwebApplication::class.java, *args)
}
