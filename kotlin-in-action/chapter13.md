# Chapter 13: DSL construction

- Nesting of lambdas or chained method calls are two typical ways to express a DSL in Kotlin, especially when combined with `infix` calls;
    ```kotlin
    // Standard way to register dependencies
    project.dependencies.add("testImplementation", kotlin("test"))
    project.dependencies.add("implementation", kotlin("org.jetbrains.exposed:exposed-core:0.40.1"))
    project.dependencies.add("implementation", kotlin("org.jetbrains.exposed:exposed-dao:0.40.1"))

    // With nested lambdas
    dependencies {
        testImplementation(kotlin("test"))
        implementation("org.jetbrains.exposed:exposed-core:0.40.1")
        implementation("org.jetbrains.exposed:exposed-dao:0.40.1")
    }

    // Standard JUnit API of writing a test
    assertTrue(str.startsWith("kot"))

    // Chained infix method call
    str should startWith("kot")
    ```

- HTML example
    ```kotlin
    fun createTable() = createHTML().table {
        val numbers = mapOf(1 to "one", 2 to "two")
        for((num, string) in numbers) {
            tr {
                td { +"$num" }
                td { +string }
            }
        }
    }
    ```

- One ingredient to the magic is defining a lambda _with receiver_ as a parameter, rather than a plain lambda;
    ```kotlin
    // Regular lambda as parameter
    fun buildString(buildAction: (StringBuilder) -> Unit) : String {
        val sb = StringBuilder()
        builderAction(sb)
        return sb.toString()
    }

    fun useRegularBuildStringFunc() {
        val s = buildString {
            it.append("Hello, ")
    it.append("World!")
        }
    }

    fun buildStringWithLambdaAndReceiver(buildAction: StringBuilder.() -> Unit) : String {
        val sb = StringBuilder()
        sb.buildAction()
        return sb.toString()
    }

    fun useLambdaReceiverBuildStringFunc() {
        val s = buildString {
            append("Hello, ")
            append("World!")
        }
    }
    ```

- Keep a lookout for function parameters that are lambdas; `fun someFun(lambda: (SomeType) -> SomeResult)`, rather than lambdas with receivers; `fun someFun(lambda: SomeType.() -> SomeResult)`

- Objects can be callable as functions, by defining an `invoke()` `operator` method which, by convention, is accessible through calling the object itself with function-like syntax
    ```kotlin
    class Greeter(val greeting: String) {
        operator fun invoke(name: String) {
            println("$greeting, $name!")
        }
    }

    fun main() {
        val norwegianGreeter = Greeter("Hei")
        norwegianGreeter("Johan")   // Prints "Hei, Johan!"
    }
    ```

- This is what allows another magic ingredient - single word invocations of some DSL item, passing in the lambda with receiver body;
    ```kotlin
    var html = createHtml().body {
        table {                         // Here, `table` is in actual fact calling the `invoke()` method on TABLE (a class named liked this since it is never supposed to be accessed "the usual way", passing in a lambda with receiver of table
            tr {                        // The same is true for `tr`
                td { "Some item" }      // And `td`
            }
        }
    }
    ```

- Member extensions are extension functions defined within a class, constraining their applicability scope. For example, here, the properties of a column can't be specified outside the context of a table
    ```kotlin
    class Table {
        fun Column<Int>.autoIncrement() : Column<Int> { ... }
    }
    ```

- Delegated properties also feature often in DSLs

