# Chapter 11: Generics

- Defining a generic type argument for a function is done as follows;
    ```kotlin
    fun <T: SomeInterface> List<T>.slice(indices: IntRange) : List<T> { ... }
    ```

- Multiple type constraints can be specified as follows;
    ```kotlin
    fun<T> ensureTrailingPeriod(seq: T)
            where T : CharSequence, T : Appendable {
        if (!seq.endsWith('.')) { seq.append('.') }
    }

- Types are assumed to be nullable unless otherwise specified. If the only constraint is that a type may not be null, `Any` may simply be used as the type argument

- Due to erasure of type parameters at runtime, in order to check the type of a generic, the star projection syntax can be used. Obviously, the type parameters cannot be checked;
    ```kotlin
    if (value is List<*>) { ... }
    ```

- `inline` functions can utilise `reified` type parameters;
    ```kotlin
    inline fun <reified T> isA(value: Any) = value is T
    ```

- A covariant class is a generic class for which `MyGeneric<A>` is a subtype of `MyGeneric<B>` if `A` is a subtype of `B`. The subtyping is preserved. As long as the type argument(s) are only used in the out positions of member functions in the type, they can be marked with the `out` prefix to facilitate type covariance;
    ```kotlin
    abstract class Animal(val name: String)
    
    class Cat(val name: String) : Animal(name)

    class Herd<out T: Animal> {
    
        fun roundUp() : List<T> { ... }
    }

    // This is allowed since the type parameter on Herd is marked with the `out` prefix
    fun herdCats(herd: Herd<Cats>) { ... }
    ```

- A contravariant class is a generic class for which `MyGeneric<A>` is a subtype of `MyGeneric<B>` if `B` is a subtype of `A`. The subtyping is reversed. As long as the type argument(s) are only used in the in positions of member functions in the type, they can be marked with the `in` prefix to facilitate type contravariance;
    ```kotlin
    
    class Tiger(val name: String) : Cat(name)

    class Pack<in T: Tiger> {
        fun adopt(tiger: T) { ... }
    }

    fun herdCats(cats Pack<Cat>) { ... }
    ```

- Use-site variance allows for specifying the restriction at the point of use. Adding out here limits the methods which may be used in `source` to only those where `T` is in the out position. This is what's known as a projected type, and is a shorter way than building up boilerplate type arguments in the type itself
    ```kotlin
    fun <T> copyData(source: MutableList<out T>, destination: MutableList<T>){
        for (item in source) {
            destination.add(item)
        }
    }
    ```

- The star projection syntax is useful when needing to deal with the generic where the type arguments aren't important;
    ```kotlin
    fun printFirst(list: List<*>) {
        if (list.isNotEmpty()) {
            println(list.first())
        }
    }
    ```

- `typealias` is useful for aliasing function types, confusing generics or long imports

