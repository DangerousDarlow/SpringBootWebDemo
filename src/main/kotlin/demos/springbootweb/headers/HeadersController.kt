package demos.springbootweb.headers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController("headers")
class HeadersController {

    // SpringBoot dependency injection is fantastic. And it isn’t just for construction.
    // Add a parameter to a framework function and SpringBoot will do it’s best to populate
    // it for you. Most of the time it gives you what you want. Here it’s the request ..
    // a Java object that’s not part of Spring or Kotlin.
    @GetMapping
    fun getHeaders(servletRequest: HttpServletRequest): Map<String, String> {
        val headers = mutableMapOf<String, String>()

        servletRequest.headerNames.toList().forEach { name ->
            servletRequest.getHeaders(name).toList().forEach { value ->
                headers[name] = value
            }
        }

        return headers
    }
}