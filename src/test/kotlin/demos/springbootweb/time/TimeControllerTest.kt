package demos.springbootweb.time

import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDateTime
import java.time.ZoneOffset

@RunWith(MockitoJUnitRunner::class)
class TimeControllerTest {

    @Mock
    lateinit var nowProvider: NowProvider

    @InjectMocks
    lateinit var controller: TimeController

    @Test
    fun `getTime returns current US central time`() {
        whenever(nowProvider.now()).thenReturn(
                LocalDateTime.parse("2018-03-15T22:45").toInstant(ZoneOffset.UTC)
        )

        assertThat(controller.getTime()).isEqualTo("2018-03-15T17:45:00-05:00[US/Central]")

        verify(nowProvider, times(1)).now()
    }
}