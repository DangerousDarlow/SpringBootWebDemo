package demos.springbootweb.time

import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Duration
import java.time.OffsetTime

@AutoConfigureMockMvc
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class TimeControllerWebTest {
    @Autowired
    lateinit var mvc: MockMvc

    @Test
    fun `Time endpoint returns current time in plain text`() {
        mvc.perform(get("/time"))
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
            .andExpect(content().string(IsTimeOfApproximatelyNow()))
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