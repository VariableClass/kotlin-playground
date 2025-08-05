# Chapter 14: Coroutines

- The `suspend` prefix runs a function on coroutines, suspending it's execution til it is no longer waiting

- Coroutines can be spun up with a number of builder functions;
    - `runBlocking()` bridges the gap between blocking and suspending functions
        ```kotlin
        import kotlinx.coroutines.*
        import kotline.time.Duration.Companion.milliseconds

        suspend fun doSomethingSlowly() {
            delay(500.milliseconds)
            println("I'm done")
        }

        fun main() = runBlocking {
            doSomethingSlowly()
        }
        ```
    - `launch()` starts new coroutines which don't return values
        ```kotlin
        fun main() = runBlocking {
            launch {
                // Do work
            }
        }
        ```
    - `async()` is used for computing values in an asynchronous manner
        ```kotlin
        suspend fun slowlyAddNumbers(a: Int, b: Int): Int {
            log ("Waiting a bit before calculating $a + $b)
            delay(100.milliseconds * a)
            return a + b
        }

        fun main() = runBlocking {
            val myDeferred = async { slowlyAddNumbers(2, 2) }
            val myOtherDeferred = async { slowlyAddNumbers(4, 4) }

            val firstDeferredResult = myDeferred.await()
            val secondDeferredResult = myOtherDeferred.await()
        }
        ```

- The `-Dkotlinx.coroutines.debug` JVM option shows additional information about running coroutines

- `Dispatcher`s determine where coroutines will run;
    - `Dispatchers.Default` is the default option, unless anything else is specified. This `Dispatcher` has an available thread pool equal to the number of CPU cores
    - `Dispatchers.Main` has a single thread used for UI tasks, whether this be desktop, mobile or web
    - `Dispatchers.IO` is used for inherently blocking code and has an available thread pool of 64 or total CPU cores; whichever is larger
    - `Dispatchers.Unconfined` allows coroutines to run without any kind of thread confinement and should only be used in specific cases
    - `limitedParallelism(n)` allows for setting custom constraints on parallelism for a dispatcher, but is also reserved for specialty cases

- A `Dispatcher` can be specified as part of the coroutine builder function;
    ```kotlin
    fun main() {
        runBlocking {
            log("Doing some work")
            launch(Dispatchers.Default) {
                log("Doing some background work")
            }
        }
    }
    ```

- The `withContext()` function allows switching to another `Dispatcher`; for example running some IO tasks, before switching back to the UI thread to show the results
    ```kotlin
    launch(Dispatchers.Default) {
        val result = performBackgroundOperation()
        withContext(Dispatchers.Main) {
            updateUI(result)
        }
    }
    ```

- A `Mutex` lock allows for locking an object in a threadsafe manner;
    ```kotlin
    fun main() = runBlocking {
        val mutex = Mutex()
        var x = 0
        repeat(10_000) {
            launch(Dispatchers.Default) {
                mutex.withLock {
                    x++
                }
            }
        }
        delay(1.seconds)
        println(x)
    }
    ```

- There are also threadsafe data structures such as `AtomicInteger` & `ConcurrentHashMap`

- `CoroutineContext`s provide context around the current coroutine, such as it's `Dispatcher` or `CoroutineName`. It is available through the compiler instrinsic `coroutineContext` property - a property not defined in code, by handled by the compiler

- Extra arguments can be overriden when creating a coroutine;
    ```kotlin
    fun main() {
        runBlocking(Dispatchers.IO + CoroutineName("myRoutine") {
            // Do some work
        }
    }
    ```

