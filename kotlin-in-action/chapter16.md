# Chapter 16: Flows

- Flows enable sequential values to be returned as they become available. They can be created with the `flow` builder function and their values collected using the `collect` extension function
    ```kotlin
    import kotlinx.coroutines.delay
    import kotlinx.coroutines.runBlocking
    import kotlinx.coroutines.flow.*
    import kotlinx.time.Duration.Companion.milliseconds

    fun createValue(): Flow<Int> {
        return flow {
            emit(1)
            delay(1000.milliseconds)
            emit(2)
            delay(1000.milliseconds)
            emit(3)
            delay(1000.milliseconds)
        }
    }

    fun main() = runBlocking {
        val myFlowOfValues = createValues()
        myFlowOfValues.collect {
            log(it)     // Each value will be logged as it is returned
        }
    }
    ```

- Flows come in two flavours; 'Cold' flows which only emit items when they are consumed by an individual collector and 'Hot' flows which produce items independently of any consumers, in a broadcast fashion

- Cold flows can be defined in a non-suspended function since they are inert until a terminal operator is invoked on them, such as `collect()`

- Should a flow produce infinite values, it's enclosing coroutine can simply be cancelled as any other coroutine;
    ```kotlin
    fun incrementingCounterFlow(): Flow<Int> {
        return flow {
            val counter = 0
            while (true) {
                emit(count++)
                delay(200.milliseconds)
            }
        }
    }

    fun main() {
        runBlocking {
            val collector = launch {
                incrementingCounterFlow.collect {
                    println(it)
                }
            }
            delay(5.seconds)
            collector.cancel()
        }
    }
    ```

- The `channelFlow` builder function can be used to create cold flows which can emit from other coroutines. The below example with normal flow (limited to running in a single coroutine) would run sequentially and take 5 seconds. Here everything runs in parallel and the execution takes only 500 milliseconds. The coroutines use the `send` function to return values back to the channel flow
    ```kotlin
    val randomNumbers = channelFlow {
        repeat(10) {
            launch {
                send(getRandomNumber())
            }
        }
    }

    fun main() = runBlocking {
        randomNumbers.collect {
            log(it)
        }
    }
    ```

- Use normal flows, unless launching new coroutines from inside the flow, in which case channel flows should be used

- Hot flows come in two flavours out of the box; 'Shared flows' and 'State flows';
    - Shared flows broadcast values to subscribers, independently of whether any subscribers are actually listening
    ```kotlin
    import kotlinx.coroutines.*
    import kotlinx.coroutines.flow.*
    import kotlin.random.*
    import kotlin.time.Duration.Companion.milliseconds

    class RadioStation {
        private val _messageFlow = MutableSharedFlow<Int>(replay = 5)   // The `replay` argument here defines the size of a cache to be able to provide new subscribers with the 5 most recent emittances
        val messageFlow : SharedFlow<Int> = _messageFlow.asSharedFlow()
        
        fun beginBroadcasting(scope: CoroutineScope) {
            scope.launch {
                while (true) {
                    delay(500.milliseconds)
                    val number = Random.nextInt(0..10)
                    log("Emitting $number!")
                    _messageFlow.emit(number)
                }
            }
        }
    }

    fun main() {
        val radioStation = RadioStation()
        radioStation.beginBroadcasting()
        delay(600.milliseconds)
        radioStation.messageFlow.collect {
            log("Collecting $it")
        }
    }
    ```
    - State flows
    ```kotlin
    import kotlinx.coroutines.flow.*
    import kotlinx.coroutines.*

    class ViewCounter {
        private val _counter = MutableStateFlow(0)  // Here the type `MutableStateFlow<Int>` is derived from the initial value `0`
        val counter = _counter.asStateFlow()
        
        fun increment() {
            _counter.update { it + 1 } // The `update` function updates the value in a threadsafe way
        }
    }

    fun main() {
        val vc = ViewCounter()
        vc.increment()
        println(vc.counter.value)
    }
    ```

- State flows perform equality-based conflation; they only emit if the value changes

- The `shareIn` function turns a cold flow into a hot flow that can be shared between multiple consumers;
    ```kotlin
    fun querySensor(): Int = Random.nextInt(-10..30)

    fun getTemperatures(): Flow<Int> {
        return flow {
            while (true) {
                emit(querySensor())
                delay(500.milliseconds)
            }
        }
    }

    fun main() {
        val temps = getTemperatures()
        runBlocking {
            // Since the `sharedIn` turns the cold flow into a hot one, it will begin executing and therefore needs to be supplied with a coroutine scope
            // The second, `started` parameter determines when the flow should start executing;
            // Eagerly: Immediately
            // Lazily: When the first subscriber appears
            // WhileSubscribed: When the first subscriber appears, then cancels collection when the last subscriber disappears
            val sharedTemps = temps.sharedIn(this, SharingStarted.Lazily)
            launch {
                sharedTemps.collect()
                log ("$it Celsius")
            }
            launch {
                sharedTemps.collect()
                log ("${celsureToFahrenheit(it)} Fahrenheit"
            }
        }
    }
    ```

- The `stateIn` functions turns a cold flow into a hot flow that tracks state and can be accessed by multiple consumers;
    ```kotlin
    fun main() {
        val temps = getTemperatures()
        runBlocking {
            val tempState = temps.stateIn(this)
            // The `value` property is used to access the StateFlow value
            println(tempState.value)    // 18
            delay(800.milliseconds)
            println(tempState.value)    // -1
        }
    }
    ```

- It can be useful to try to reframe problems to make use of the simpler state flows. These have the added benefit that if a `MutableStateFlow` was created of, for example, type `List<Int>`, the entire history of updates could be retained in that list; useful for new subscribers

