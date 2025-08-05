# Chapter 17: Flow operators

- There are two types of flow operators; intermediate operators which, much like with collection operators either return a new, modified flow and terminal operators which execute the code in the flow and return a result

- Upstream flows denote flows before an operator and downstream flows are those returned by an operator

- Using the `map` operator simply maps the emittances whilst `transform` operator returns a new flow;
    ```kotlin
    fun map() {
        val names = flow {
            emit("Jo")
            emit("May")
            emit("Sue")
        }
        
        val uppercaseNames = names.map {
            it.uppercase()
        }
        
        runBlocking {
            uppercaseNames.collect { print("$it ") }
        }
    }

    fun map() {
        val names = flow {
            emit("Jo")
            emit("May")
            emit("Sue")
        }

        // `transform` produces a new flow
        val upperAndLowercaseNames = names.transform {
            emit(it.uppercase())
            emit(it.lowercase())
        }
        
        runBlocking {
            upperAndLowercaseNames.collect { print("$it ") }
        }
    }
    ```

- The `take` & `takeWhile` operators can be used to cancel a flow after a certain number of emittances/after a condition is met;
    ```kotlin
    val temps = getTemperatures()
    temps
        .take(5)
        .collect {
            log(it)
        }
    ```

- It is possible to hook into the different phases of a flow with `onStart`, `onEach`, `onCompletion` and `onEmpty`. The order is important, since each operator receives it's input flow from the previous operator's output
     ```kotlin
    val temps = getTemperatures()
    temps
        .take(5)
        .onEach {
            println(it)
        }
        .onCompletion { cause ->
            if (cause != null) {
                println("An error occurred! $cause")
            } else {
                println("Completed!")
            }
        }
        .collect()
    ```

- The `buffer` operator allows decoupling flows, such that if a subsequent flow is slower to process that the feeding flow can emit, a number of emittances can be buffered in an intermediate flow. It can be further customised by providing the `onBufferOverflow` parameter which describes what to do once the buffer is filled; `SUSPEND` - suspend, `DROP_OLDEST` - drop the oldest value in the buffer without suspending or `DROP_LATEST` - drop the latest value being added
    ```kotlin
    fun main() {
        val ids = getAllUserIds()                   // A flow which performs some local database retrieval
        runBlocking {
            ids
                .buffer(3)                          // Creates an intermediate buffer flow to accept IDs as they are retrieved from the `getAllUserIds()` flow
                .map { getProfileFromNetwork(it) }  // since the `getProfileFromNetwork` function s much slower to retrieve it's data
                .collect { log ("Got $it") }
        }
    }
    ```

- The `conflate` operator simply discards the value being emitted if the collector is busy;
    ```kotlin
    fun main = runBlocking {
        val temps = getTemperatures()
        temps
            .onEach { log("Read $it from sensor") }
            .conflate ()
            .collect {
                log("Collected $it")
                delay(1.seconds)
            }
    }
    ```

- The `debounce` operator is useful when needing to wait before processing the next emittance, such as when showing immediate query results to a user who is typing;
    ```kotlin
    fun main() = runBlocking {
        searchQuery
            .debounce(250.milliseconds)
            .collect {
                log("Searching for $it")
            }
    }
    ```

- The `flowOn` operator acts similarly to the `withContext` function in that it adjusts the coroutine context so that execution can switch between dispatchers. Importantly, it only affects the upstream flow, downstream flows are unaffected
    ```kotlin
    fun main() = runBlocking {
        flowOf(1)
            .onEach { log("A") }
            .flowOn(Dispatchers.Default)
            .onEach { log("B") }
            .flowOn(Dispatchers.IO)
            .onEach { log("C")
            .collect()
    }
    ```

- Creating custom intermediate operators is as simple as;
    ```kotlin
    fun Flow<Double>.averageOfLast(n: Int): Flow<Double> =
        flow {
            val number = mutableListOf<Double>()
            collect {
                if (numbers.size >= n) {
                    numbers.removeFirst()
                }
                numbers.add(it)
                emit(numbers.average())
            }
        }
    
    fun main() = runBlocking {
        flowOf(1.0, 2.0, 30.0, 121.0)
            .averageOfLast(3)
            .collect {
                print("$it ")
            }
    }
    ```

- Other terminal operators are available;
    ```kotlin
    fun main() = runBlocking {
        getTemperatures()
            .first()
    }
    ```

