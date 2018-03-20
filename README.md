# Spring Boot Web Demo

This project has been created to demonstrate using Spring Boot to create an application with a web API. The starting point was the spring initializr website https://start.spring.io where the following options were selected
 * Gradle Project
 * Kotlin
 * Spring Boot version: 2
 * Dependency: Web
 
![initializr](https://github.com/DangerousDarlow/SpringBootWebDemo/blob/master/screenshots/initializr.png)

## Build & Run

Build the demonstration application using powershell

```powershell
.\gradlew build
```

Run the application using

```powershell
java -jar .\build\libs\SpringBootWebDemo-0.0.1-SNAPSHOT.jar
```

The application has started when you see the following

```powershell
Started SpringbootwebApplicationKt in 3.521 seconds (JVM running for 4.001)
```

## Controller

The annotation `@RestController` is applied to a class to map web requests to it's handler methods. The annotation `@GetMapping` is applied to a method to map a GET request to that handler.

```kotlin
@RestController
@RequestMapping("time")
class TimeController(val nowProvider: NowProvider) {

    @GetMapping
    fun getTime() = ZonedDateTime
            .ofInstant(nowProvider.now(), ZoneId.of("US/Central"))
            .format(DateTimeFormatter.ISO_DATE_TIME)!!
}
```

Run the application noting the port number in the output `Tomcat started on port(s): 8080 (http)`.

The application Postman can be used to test the API. It can be downloaded from https://www.getpostman.com/. Start postman and send a GET request to `localhost:8080/time`. US/Central time will be returned as a string.

![get time](https://github.com/DangerousDarlow/SpringBootWebDemo/blob/master/screenshots/postman-get-time.png)

## Dependency Injection

Dependency injection is really straight forward using Spring. Annotating a class with @Component makes the class a bean. When the Spring application context loads components are registered and made available for injection.

```kotlin
@Component
class RealNowProvider : NowProvider {
    override fun now() = Instant.now()!!
}
```

To inject into a constructor add a parameter of the class / interface type and Spring will do the rest.

```kotlin
class TimeController(val nowProvider: NowProvider) {
```

Dependency injection isn't just for constructors. Properties annotated with @Autowired will be injected.

```kotlin
class TimeControllerWebTest {
    @Autowired
    lateinit var mvc: MockMvc
```

## Testing the controller

The controller can be unit tested but this would not verify the HTTP request / response wiring and marshalling that Spring Boot does. The solution is to annotate a class with @SpringBootTest which starts the application and runs tests against it.

```kotlin
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
```

The test uses mock mvc to test the web api. The content returned is verified by a custom matcher derived from Hamcrest BaseMatcher.

```kotlin
class IsTimeOfApproximatelyNow : BaseMatcher<String>() {
    override fun matches(item: Any?): Boolean {
        stuff
    }
}
```

The Mockito mocking framework makes isolating classes from dependencies straight forward. Dependencies to be mocked are annotated with @Mock and the class they are to be injected into with @InjectMocks.

```kotlin
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
```

## Marshalling

Requests and responses of arbitrary complexity can be used by marshalling. JSON encoding is commonly used because it's human readable and straight forward. A data class can be used and must be able to be instantiated with no arguments which in Kotlin is easiest achieved by defaulting all values.

```kotlin
data class TimeInZonesRequest(val timeZones: List<String> = emptyList())
```

A handler for the post request is added using the annotation `@PostMapping`. The request body is mapped to a handler parameter using the annotation `@RequestBody`. That's all there is to it. Marshalling will convert between JSON and data class instances.

```kotlin
@PostMapping("/zones")
fun postTimeInZones(@RequestBody() request: TimeInZonesRequest): TimeInZonesResponse {
    val times = mutableListOf<LocalTimeInZone>()
    return TimeInZonesResponse(times)
}
```

Testing with a Spring Boot test is similar to before except the body must be specified. An `Autowired` ObjectMapper can be used to serialise a data class instance to JSON.

```kotlin
mvc.perform(post("/time/zones")
    .contentType(APPLICATION_JSON)
    .content(objectMapper.writeValueAsString(request)))
    .andExpect(status().isOk)
    .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
    .andExpect(jsonPath("times[0].timeZone", equalTo(londonZone)))
    .andExpect(jsonPath("times[1].timeZone", equalTo(utcZone)))
```