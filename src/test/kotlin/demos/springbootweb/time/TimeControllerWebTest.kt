package demos.springbootweb.time

import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.TEXT_PLAIN
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Duration
import java.time.OffsetTime

@AutoConfigureMockMvc
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class TimeControllerWebTest {
    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `Time endpoint returns current time in plain text`() {
        mvc.perform(get("/time"))
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(TEXT_PLAIN))
            .andExpect(content().string(IsTimeOfApproximatelyNow()))
    }

    @Test
    fun `Zones endpoint returns local time for requested zones in json response`() {
        val londonZone = "Europe/London"
        val utcZone = "UTC"

        val request = TimeInZonesRequest(timeZones = listOf(londonZone, utcZone))

        mvc.perform(post("/time/zones")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
            .andExpect(jsonPath("times[0].timeZone", equalTo(londonZone)))
            .andExpect(jsonPath("times[1].timeZone", equalTo(utcZone)))
    }
}

class IsTimeOfApproximatelyNow : BaseMatcher<String>() {
    override fun describeTo(description: Description?) {
        description?.appendText("A time string approximately equal to now")
    }

    override fun matches(item: Any?): Boolean {
        if (item !is String)
            return false

        if (item.isNullOrEmpty())
            return false

        val itemAsTime = OffsetTime.parse(item)
        val differenceToNow = Duration.between(itemAsTime, OffsetTime.now())
        return differenceToNow < Duration.ofMinutes(1)
    }
}