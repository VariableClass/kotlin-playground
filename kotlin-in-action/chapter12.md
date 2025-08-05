# Chapter 12: Annotations and reflection

- An annotation is used to associate additional metadata with a declaration. Common annotations include `@Deprecated`, `@Test` & `@Suppress`. They equivalent to, for example a C# decorator attribute.
    ```kotlin
    @Deprecated("Use removeAt(index) instead.", ReplaceWith("removeAt(index)"))
    fun remove(index: Int) { ... }

    import kotlin.test.*
    
    class MyTest {

        @Test
        fun TestTrue() {
            assert(1 + 1 == 2)
        }
    }
    ```

- Since a single declaration in Kotlin often produces multiple Java declarations, it can be useful to specify to which of these an annotation applies. In the below example, the name for the Java getters and setters otherwise implicitly generated from the `certificate` property are set using the `JvmName` annotation, including a `get:` and `set:` prefix to correctly annotate the respective Java methods
    ```kotlin
    class CertManager {
        @get:JvmName("obtainCertificate")
        @set:JvmName("putCertificate")
        var certificate: String = "---BEGIN..."
    }
    ```

- The available use-site targets are as follows;
    - `property`: Property
    - `field`: Field generated for the property
    - `get`: Property getter
    - `set`: Property setter
    - `receiver`: Receiver parameter of an extension function or property
    - `param`: Constructor parameter
    - `setparam`: Property setter parameter
    - `delegate`: Field storing the delegate instance for a delegated property
    - `file`: Class containing top-level functions and properties declared in the file

- Defining an annotation looks as follows;
    ```kotlin
    annotation class JsonName(val name: String)
    ```

- Annotations may also be annotated, these are called meta-annotations;
    ```kotlin
    @Target(AnnotationTarget.PROPERTY)
    annotation class JsonExclude
    ```

- Defining a meta-annotation is done by annotating the annotation class with the `@Target` annotation, targeting `AnnotationTarget.ANNOTATION_CLASS`;
    ```kotlin
    @Target(AnnotationTarget.ANNOTATION_CLASS)
    annotation class BindingAnnotation

    @BindingAnnotation
    annotation class Bind
    ```

- A class can be passed as an argument into an annotation, as it is in `@DeserializeInterface` to specify the type to use when deserialising an interface property;
    ```kotlin
    interface Company {
        val name: String
    }

    data class CompanyImplementation(override val name: String) : Company

    data class Person(
        val name: String
        @DeserializeInterface(CompanyImplementation::class) val company: Company
    )
    ```

- Defining a class argument for an annotation is achieved as below. Here the `out` prefix denotes the use of any class that extends `Any`, not simply `Any` itself
    ```kotlin
    annotation class DeserializeInterface(val targetClass: KClass<out Any>)
    ```

- To get an instance of a class for reflection, it is possible to either use the class name itself, followed by `::class`; `MyClass::class` or an object of that class, followed by `::class`;
    ```kotlin
    class MyClass

    fun main() {
        val myInstance = MyClass()
        val reflectionRef = myInstance::class
    }
    ```

- The `KCallable` interface provides a way of invoking functions & properties, where the `call()` method accepts an arbitrary number of arguments, but will throw an exception if the provided number of arguments do not match the method signature. `invoke()` can instead be used for `KFunction`s and `get()` for `KProperty`s
    ```kotlin
    interface KCallable<out R> {
        fun call(vararg args: Any?): R
    }

    fun foo(x: Int) = println(x)

    fun bar(x: Int, y: Int) = x + y

    fun main() {
        val kFunction = ::foo
        kFunction.call(42)

        val kFunction2: KFunction2<Int, Int, Int> = ::bar

        // Since bar() is a KFunction, the more typesafe `invoke()` can be used instead
        kFunction.invoke(42, 24)

        // For properties, the `get()` method can instead be used
        var counter = 0
        val kProperty = ::counter
        kProperty.setter.call(21)
        kProperty.get()
    }
    ```

- Both classes and objects are handled with the `KClass` type, though the `objectInstance` property will have a value if dealing with an `object`. Otherwise `createInstance()` can be invoked to instantiate the class

- The `callBy()` method allows invoking a constructor or function with default arguments

- The type of a `KClass` can be evaluated with the typed `typeOf<T>()` method

