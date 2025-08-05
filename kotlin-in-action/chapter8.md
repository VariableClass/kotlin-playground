# Chapter 8: Basic types, collections and arrays

- `toInt()`, `toLong()`, `toBool()`, etc are available conversion functions, with `toIntOrNull()` alternatives if not wanting to perform checks for `NumberFormatException`s

- `toBool()` does not have a `toBoolOrNull()` equivalent and will instead return `false`. `toBoolStrict()` will however restrict matching to specifically `"true"` or `"false"`

- `Any` - Supertype of all non-nullable types in Kotlin

- `Unit` - A type returned in place of Java's `void`. Allows for using "void" as a type parameter, reducing boilerplate that would be necessary in languages like Java or C#
    ```kotlin
    interface Processor<T> {
        fun process() : T
    }

    class StringProcessor : Processor<String> {
        override fun process() : String = "Hello!"
    }

    class NoResultProcessor : Processor<Unit> {
        override fun process() {
            // do work
        }
    }
    ```

- `Nothing` - Useful in functions that will never return, i.e. those which always throw an exception or contain an infinite loop

- The interface `Collection` defines the readonly operations `size`, `iterator()` and `contains()`, while the extended interface `MutableCollection` defines the mutable operations `add()`, `remove()` and `clear()`

- Simply receiving a variable as `List<T>` doesn't mean that another function elsewhere doesn't have a reference to it as a `MutableList<T>`, so it could be mutated by the other function whilst reading it. The `kotlinx.collections.immutable` library provides immutable collection interfaces and implementation prototypes

- Since Kotlin has no first-class array and it is instead a type, when interacting with Java methods, it is often necessary to transform a `Collection` into an `Array`. For example, when needing to provide an array of `String`s to a method with a `vararg` argument
    ```kotlin
    val strings = listOf("a", "b", "c")
    println("%s/%s/%s".format(*strings.toTypedArray()))
    ```

- In the case of primitive types, the `toTypedArray()` method will always return a boxed array (`Array<Int>`). In cases where the primitive type is required, the following types may be used; `IntArray`, `ByteArray`, `CharArray`, etc.
    ```kotlin
    val fiveZerosByConstructor = IntArray(5)
    val fiveZerosByFactoryFunction = intArrayOf(0, 0, 0, 0, 0)
    val fiveZerosByConstructorWithLambda = IntArray(5) { i -> 0 }
    ```

- There are also helper functions available for collections of boxed primitives to transform them into an unboxed array, such as `List<Int>.toIntArray()`

