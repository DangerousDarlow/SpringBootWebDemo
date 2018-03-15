package demos.springbootweb.time

import org.springframework.stereotype.Component
import java.time.Instant

@Component
class RealNowProvider : NowProvider {
    override fun now() = Instant.now()!!
}