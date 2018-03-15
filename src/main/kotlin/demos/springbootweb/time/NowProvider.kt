package demos.springbootweb.time

import java.time.Instant

interface NowProvider {
    fun now(): Instant
}