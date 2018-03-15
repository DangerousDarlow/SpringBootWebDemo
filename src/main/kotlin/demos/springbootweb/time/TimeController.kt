package demos.springbootweb.time

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.OffsetTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("time")
class TimeController {

    @GetMapping
    fun getTime(): String {
        return OffsetTime.now().format(DateTimeFormatter.ISO_OFFSET_TIME)
    }

    @PostMapping("/zones")
    fun postTimeInZones(@RequestBody() request: TimeInZonesRequest): TimeInZonesResponse {
        val times = mutableListOf<LocalTimeInZone>()

        request.timeZones.forEach {
            val zone = ZoneId.of(it)
            times.add(LocalTimeInZone(
                timeZone = zone.id,
                localTime = ZonedDateTime
                    .ofInstant(Instant.now(), zone)
                    .toLocalTime()
                    .format(DateTimeFormatter.ISO_LOCAL_TIME)))
        }

        return TimeInZonesResponse(times)
    }
}