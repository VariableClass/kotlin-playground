# Chapter 15: Structured concurrency

- Coroutine scopes establish parent-child relationships between coroutines; `launch` & `async` extend the `CoroutineScope` interface and therefore automatically become children of the parent scope

- The `coroutineScope` function enables creating a new coroutine scope;
    ```kotlin
    import kotlinx.coroutines.x
    import kotlin.random.Random
    import kotlin.time.Duration.Companion.milliseconds

    suspend fun generateValue() : Int {
        delay(500.milliseconds)
        return Random.nextInt(0, 10)
    }

    suspend fun computeSum() {
        log("Computing a sum...")
        val sum = coroutineScope {
            val a = async { generateValue() }
            val b = async { generateValue() }
            a.await() + b.await()
        }
        log("Sum is $sum")
    }

    fun main() = runBlocking {
        computeSum()
    }
    ```

- A `Job` - created as part of a coroutine - will automatically cancel coroutines in the same scope upon an unhandled exception and propogate the exception further

- Another alternative is using the `CoroutineScope` constructor function. By default this will be created with a `Job`, but specifically passing in a `SupervisorJob` will allow for manually defining the behaviour of the coroutine;
    ```kotlin
    class ComponentWithScope(dispatcher: CoroutineDispatcher = Dispatchers.Default) {
        private val scope = CoroutineScope(dispatcher + SupervisorJob())

        fun start() {
            log("Starting")
            scope.launch {
                while(true) {
                    delay(500.milliseconds)
                    log("Component working!")
                }
            }
            
            scope.launch {
                log("Doing a one-off task...")
                delay(500.milliseconds)
                log("Task done!")
            }
        }

        fun stop() {
            log("Stopping")
            scope.cancel()
        }
    }

    fun main() {
        val c = ComponentWithScope()
        c.start()
        Thread.sleep(2000)
        c.stop()
    }
    ```

- `GlobalScope` is a globally available scope, but lacks the benefits of structured concurrency that defining scopes provides and is generally not advised for the majority of use cases

- A `Job` can be cancelled with the `cancel()` function;
    ```kotlin
    fun main() {
        runBlocking {
            var launchedJob = launch {
                log("I'm launched!")
                delay(1000.milliseconds)
                log("I'm done!")
            }
            
            var asyncDeferred = async {
                log("I'm async!")
                delay(1000.milliseconds)
                log("I'm done!")
            }

            delay(200.milliseconds)
            launchedJob.cancel()
            async.cancel()
        }
    }
    ```

- The `withTimeout` and `withTimeoutOrNull` functions allow for computing a value while setting a maximum time limit;
    ```kotlin
    import kotlinx.coroutines.*
    import kotlin.time.Duration.Companion.seconds
    import kotlin.time.Duration.Companion.milliseconds

    suspend fun calculateSomething(): Int {
        delay(3.seconds)
        return 2 + 2
    }

    fun main() = runBlocking {
        val quickResult = withTimeoutOrNull(500.milliseconds) {
            calculateSomething()
        }
        println(quickResult)    // null
        
        val slowResult = withTimeoutOrNull(5.seconds) {
            calculateSomething()
        }
        println(slowResult)     // 4
    }
    ```

- Cancelling a coroutine cascades through it's children. A `CancellationException` is thrown from suspension points - places in the code where a coroutine is suspended (a `delay` or waiting on an IO call, for example). This should not be accidentally swallowed by some over-eager `try`-`catch` logic

- Calls to other cancellable functions introduces suspension points, but these can and should be introduced with checking the `isActive` property, the `ensureActive()` and the `yield()` functions.

    - Checking `isActive` allows for graceful termination of the currently running coroutine, closing resources and other related clean-up;
        ```kotlin
        ```

    - Invoking `ensureActive()` checks the `isActive` property and immediately throws a `CancellationException` if it is false
        ```kotlin
        val myJob = launch {
            repeat(5) {
                doCpuHeavyWork()
                ensureActive()
            }
        }
        ```

    - Invoking `yield()` creates a suspension point, allowing other coroutines to work in the meantime
        ```kotlin
        import kotlinx.coroutines.*

        fun doCpuHeavyWork(): Int {
            var counter = 0
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() < startTime + 500) {
                counter++
                yield()
            }
            return counter
        }
    
        fun main() {
            runBlocking {
                launch {
                    repeat(3) {
                        doCpuHeavyWork()
                    }
                }
                launch {
                    repeat(3) {
                        doCpuHeavyWork()
                    }
                }
            }
        }
        ```

- Always keep open resources in mind when, also in coroutines. Using a `try-finally` statement, or simply a `use { }` block, if the resource implements the `AutoCloseable` interface are both recommended approaches

- In Ktor, a `PipelineContext` (which extends `CoroutineScope`) provides the request-level scope, cancelling any work if the client cancels the request. In order to run work independent of the request scope, `Application` - which also extends `CoroutineScope` and has the same lifetime as the Ktor application - should be chosen as the parent scope;
    ```kotlin
    routing {
        get("/requestData") {
            launch {
                println("I'm doing some background work!")
                delay(200.milliseconds)
                println("I'm done!")
            }
        }
        get("/backgroundTask") {
            call.application.launch {
                println("I'm doing some background work!")
                delay(5000.milliseconds)
                println("I'm done!")
            }
        }
    }
    ```

