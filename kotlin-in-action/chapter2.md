# Chapter 2: Kotlin basics

- Most control structures - `if`, `when`, `try/catch` - except loops - `for`, `while` & `do/while` - are expressions, rather than statements
- The default visibility in Kotlin is `public`, so this can be omitted
- Kotlin makes use of properties to generate backing fields & getters/setters without boilerplate
- Enum classes may define properties and functions
- Multiple options can be referenced on a single `when`-branch; equivalent to cascading a `select` case;
    ```kotlin
    when (value) {
        "Option A", "Option B" -> "Hello!",
        "Option C" -> "Goodbye!"
        else -> "Whoops!"
    }
    ```
- Variables are "smart-cast" when checked; they are automatically cast to the type being checked without an extra, explicit step;
    ```kotlin
    fun exampleFun() = when (vehicle) {
        is Car -> vehicle.drive()
        is Boat -> vehicle.sail()
        is Plane -> vehicle.fly()
        else -> throw IllegalArgumentException("Unknown vehicle type")
    }
    ```

- Variables can be defined within the subject of a `when` expression;
    ```kotlin
    when (val car = buildCar()) {
        "Type" -> "The type is ${myValue.type}",
        "Colour" -> "The colour is ${myValue.colour}"
    }
    ```
- `setOf(itemA, itemB)` can be used to create `set`s. Two `set`s are equal if they contain the same items, the order is irrelevant
- The last expression in a block is the result
- When in a nested loop, a label can be attributed to the enclosing loop and referenced when breaking or continuing;
    ```kotlin
    myOuterLabel@ while (outerCondition) {
      while (innerCondition) {
        if (shouldExitInner) break
        if (shouldSkipInner) continue
        if (shouldExitOuter) break@myOuterLabel
        if (shouldSkipOuter) continue@myOuterLabel
      }
    }
    ```
- There is no incrementing `for`-loop in Kotlin, only `for (item in collection)`, though the same functionality can be achieved with ranges;
    ```kotlin
    for (index in 1..100) { ... }
    for (index in 1..<100) { ... }
    for (index in 1..100 step 5) { ... }
    for (index in 100 downTo 1 step 2) { ... }
    ```
- A `map` can be iterated over in Kotlin, extracting both the key and the value as follows;
    ```kotlin
    for ((someKey, someValue) in someCollection) {
        println(someKey)
        println(someValue)
    }
    ```
- The `withIndex()` function can be used to retain an index when iterating over a collection without needing to create and store a separate value;
    ```kotlin
    for (name in names.withIndex()) {
        println("$index: $name")
    }
    ```
- The `in` operator can also be used to check membership in a collection;
    ```kotlin
    val isLetter = c in 'a'..'z' || 'A'..'Z'
    ```

