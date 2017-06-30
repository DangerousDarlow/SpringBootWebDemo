package demos.springbootweb.time

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("time")
class TimeController {
    @GetMapping
    fun getTime(): String {
        return OffsetTime.now().format(DateTimeFormatter.ISO_OFFSET_TIME)
    }
}