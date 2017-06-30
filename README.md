# Spring Boot Web Demo

This project has been created to demonstrate using spring boot to create an application with a REST API. The starting point was the spring initializr website https://start.spring.io where the following options were selected
 * Gradle Project
 * Kotlin
 * Spring Boot version: 2
 * Dependency: Web
 
![initializr](https://github.com/DangerousDarlow/SpringBootWebDemo/blob/master/screenshots/initializr.png)

## Controller

The annotation `@RestController` is applied to a class to map web requests to it's handler methods. The annotation `@GetMapping` is applied to a method to map an HTTP GET request to that handler.

```kotlin
@RestController
@RequestMapping("time")
class TimeController {
    @GetMapping
    fun getTime(): String {
        return OffsetDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)
    }
}
```

Run the application noting the port number in the output `Tomcat started on port(s): 8080 (http)`.

The application Postman can be used to test the API. It can be downloaded from https://www.getpostman.com/. Start postman and send a GET request to `localhost:8080/time`. Local time will be returned as a string.

![get time](https://github.com/DangerousDarlow/SpringBootWebDemo/blob/master/screenshots/postman-get-time.png)

## Testing the controller

The controller can be unit tested but this would not verify the HTTP request / response wiring and marshalling that spring boot does. The solution is to use a spring boot test which starts the application and runs tests against it.

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

The test uses mock mvc to test the web api. The content returned is verified by a custom matcher derived from hamcrest BaseMatcher.

```kotlin
class IsTimeOfApproximatelyNow : BaseMatcher<String>() {
    override fun matches(item: Any?): Boolean {
        stuff
    }
}
```
