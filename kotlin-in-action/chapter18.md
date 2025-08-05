# Chapter 18: Error handling and testing

- Exceptions thrown from within a coroutine can't be caught by simply wrapping the `launch` or `async` block in a `try-catch` statement. For `launch`, the `try-catch` statement can simply be moved within the `launch` block, whilst an `async`'s corresponding `await()` method(s) can be wrapped in `try-catch` statements. Unhandled exceptions within an `async` block are _rethrown_, so should also be handled within the block if possible

- There are two conceptual ways of dividing work between children;
    - Concurrent decomposition of work implies that if one of the children fail, the result can no longer be achieved and there is no reason for the rest to continue
    - When a parent is simply supervising a child, one child can fail, whilst the others may continue

- Coroutines cancel all their children when one fails, unless created with a `SupervisorJob`, then propogate the exception up the hierarchy

- Supervisors define a boundary for error propogation and, as such, are often found at the topmost level in a coroutine hierarchy. They can be defined with the `supervisorScope` builder function
    ```kotlin
    fun main() = runBlocking {
        supervisorScope {
            launch {
                try {
                    while (true) {
                        println("Heartbeat!")
                        delay(500.milliseconds)
                    }
                } catch (e: Exception) {
                    println("Heartbeat terminated: $e")
                    throw e
                }
            }
            launch {
                delay(1.seconds)
                throw UnsupportedOperationException("Ow!")
            }
        }
    }
    ```

- At the top of the hierarchy, the root coroutine, a `CoroutineExceptionHandler` can be supplied within the coroutine context to handle the exception before/instead of passing it to the system-wide exception handler. In Ktor, a `CoroutineExceptionHandler` passes a string representation of the uncaught exception to it's logging provider, for example.

- There is no such thing as an intermediate" `CoroutineExceptionHandler`; handlers registered in non-root coroutine contexts will never been invoked. Exceptions are passed up the hierarchy.

- `CoroutineExceptionHandler`s are only ever invoked when the topmost coroutine in the hierarchy was created using `launch`, since it is the responsibility of the consumer of an `async`'s `Deferred<T>.await()` to handle any exceptions thrown from it

- Instead of wrapping a flow's `collect()` operator in a `try-catch` statement, it can instead be caught by added the intermediate operator `catch()` just before the `collect()` operator is called, since the `catch()` operator will only apply to flows upstream. In this instance, it is wise to utilise the `onEach()` operator to describe the collection logic, rather than with the `collect()` operator, since this will always be downstream of `catch()`

- The `retry()` operator allows new collection attempts to be made on a flow, based on a predicate

- To build a test coroutine, the `runTest` function can be invoked, facilitating the use of virtual time - speeding up test executions which require delays/waits such that these can be run near-instantaneously without losing functionality. `runTest` accepts a timeout for cases such as integration tests which rely on contact with external components
    ```kotlin
    import kotlinx.coroutines.*
    import kotlinx.coroutines.test.*
    import kotlin.test.*
    import kotlin.time.Duration.Companion.seconds

    class PlaygroundTest {
        @Test
        fun testDelay() = runTest {
            val startTime = System.currentTimeMillis()
            delay(20.seconds)
            println(System.currentTimeMillis() - startTime
        }
    }
    ```

- `runTest` has a special test dispatcher & scheduler (`TestCoroutineScheduler`) within it's special scope; `TestScope`.

