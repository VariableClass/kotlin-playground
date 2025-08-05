# Chapter 6: Working with collections and sequences

- `filter`/`filterNot` returns a subset of the collection based on the predicate

- `map` transforms all items in the collection

- `filterIndexed` & `mapIndexed` work the same as `filter` & `map`, but also provide the index of collection items as an input parameter into the lambda

- `filterKeys`/`filterValues` & `mapKeys`/`mapValues` can be used on maps

- `reduce` passes in an accumulator to the lambda, allowing for aggregation type tasks

- `fold` is very similar to `reduce`, just that it is possible to choose an arbitrary start value

- `runningReduce` & `runningFold` return a list of all the intermittent values up to and including the final result

- Collection predicates; `all`, `any`, `none`, `count` (more efficient that `.size`), `find`/`firstOrNull`

- `partition` returns a pair of lists, split on the predicate. Saves needing to run two `filter` operations, for example.

- `groupBy` returns a map with the filter values as keys and subsets of the original collection as values

- `associate` returns key-value `Pair`s;
    ```kotlin
    ...
    val people = listOf(Person("Joe", 29), Person("Mary", 31))
    val nameToAge = people.associate { it.name to it.age }
    ...
    ```

- `associateWith` instead uses the elements of the collection as keys and uses the lambda to generate the values

- `associateBy` uses the lambda to generate the keys and uses the elements of the collection as the values

- `replaceAll` replaces the collection items with the result of the lambda function. Not functional since the original collection is mutated.

- `fill` is a shorthand way of doing a `replaceAll` with a single value (not lambda). Also not functional

- `ifEmpty` can return a default value in the case the collection is empty. `ifBlank` is an equivalent for blank Strings

- `chunked` breaks the collection up into distinct sizes. A lambda can also be passed in to transform the output;
    ```kotlin
    ...
    val temperatures = listOf(27.7, 29.8, 22.0, 35.5, 19.1)
    println(temperatures.chunked(2)) // [[27.7, 29.8], [22.0, 35.5], [19.1]]
    println(temperatures.chunked(2) { it.sum() }) // [57.5, 57.5, 19.1]
    ...
    ```
- `windowed` allows for creating a sliding window of a given size;
    ```kotlin
    println(temperatures.windowed(3))    // [[27.7, 29.8, 22.0], [29.8, 22.0, 35.5], [22.0, 35.5, 19.1]]
    println(temperatures.windowed(3) { it.sum() / it.count() })  // [26.5, 29.09999999999999998, 25.533333333333333]
    ```

- `zip` creates a list of pairs from two separate lists, ignoring items without a counterpart;
    ```kotlin
    ...
    val names = listOf("Joe", "Mary", "Jamie")
    val ages = listOf(22, 31, 31, 44, 0)
    println(names.zip(ages)) // [(Joe, 22), (Mary, 31), (Jamie, 31)]

    // as infix
    println(names zip ages) // [(Joe, 22), (Mary, 31), (Jamie, 31)]
    ...
    ```

- `flatMap` first `map`s each element, then flattens the resulting lists into a single list. Could also be combined with, for example, `toSet()` to remove duplicates from the resulting list

- `flatten` turns a `List<List<T>>` into a `List<T>`

- `asSequence()` does not create intermediate lists to hold values in a set of chained functions;
    ```kotlin
    // Without `asSequence()`, two collections are created; one to hold the values from the `map` function and one for the final result out of `filter`
    people.map(Person::name).filter { it.startsWith("A") }

    // With `asSequence()`, no intermediate collections are created
    people
        .asSequence()
        .map(Person::name)
        .filter { it.startsWith("A") }
        .toList()
    ```

- Intermediate operations are lazy and return another sequence, whilst terminal operations return a result. Sequences also prevent extraneous processing of irrelevant elements. All computations are performed on each item before going forward, contrary to operating on a collection where each computation is performed on the entire collection before going forward.
    ```kotlin
    ...
    println(
        listOf(1, 2, 3, 4)
            .asSequence()
            .map { it * it }
            .find { it > 3 }
    )
    ...
    ```

- `generateSequence` can be used to do exactly that;
    ```kotlin
    ...
    val naturalNumbers = generateSequence(0) { it + 1 }
    val numbersTo100 = naturalNumbers.takeWhil { it <= 100 }
    println(numbersTo100.sum()
    ...
    ```

- They can be used in clever ways;
    ```kotlin
    fun File.isInHiddenDir() = generateSequence(this) { it.parentFile }.any { it.isHidden}

    fun main() {
        val file = File("/home/someUser/.config/app/config.txt")
        println(file.isInHiddenDir()) // true
    }
    ```

