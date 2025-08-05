# Chapter 5: Programming with lambdas

- Functional programming considers functions as first-class citizens; they should be treated as values. A lambda could therefore be passed as an argument instead of a redundant class instance

    ```kotlin
    // This example uses an anonymous object
    button.setOnClickListener(object: OnClickListener {
        override fun onClick(v: View) {
            println("I was clicked!")
        }
    }

    // becomes
    button.setOnClickListener({ println("I was clicked!") })
    ```

- Synactic convention allows for a lambda expression to be provided after the parentheses if it is the last argument in a function call. These parentheses can then be dropped entirely if the expression is the only argument

    ```kotlin
    // The above example may now be simplified
    button.setOnClickListener {
        println("I was clicked!")
    }
    ```

- A typical lambda may define name(s) for any variables the lambda will use. When dealing with, for example, a collection and the current collection item is the only variable of interest, the `it` keyword can instead be used
    ```kotlin
    ...
    val cars = listOf(Car("Honda", 2004), Car("Audi", 2017), Car("Volkswagen", 2017), Car("Skoda", 2023))

    fun getTheOldestCar(cars: ArrayList<Car>) = cars.minByOrNull { it.productionYear }
    ...
    ```

- When a lambda simply defers to a function or property, this can simply be expressed with the even simpler following syntax
    ```kotlin
    fun getTheOldestCar(cars: ArrayList<Car>) = cars.minByOrNull(Car::productionYear)
    ```

- Examples of the same function call from most to least verbose;
    ```kotlin
    people.maxByOrNull({p: Person -> p.age })
    people.maxByOrNull() { p: Person -> p.age }
    people.maxByOrNull { p: Person -> p.age }
    people.maxByOrNull { p -> p.age }
    people.maxByOrNull { it.age }
    people.maxByOrNull(Person::age)
    ```

- The `run` function accepts a lambda to be executed and is useful when defining functions in places;
    ```kotlin
    fun main() {
        val myFavouriteNumber = run {
            println("Do work")
            println("Do more work")
            42
        }
    }
    ```

- A top level reference can also be passed, there is simply no class name to specify;
    ```kotlin
    fun salute() = println("Salute!")

    fun main() { run(::salute) }
    ```

- It is also possible to pass a reference to a constructor;
    ```kotlin
    data class Person(val name: String, val age: Int)

    fun main() {
        val createPerson = ::Person
        val p = createPerson("Alice", 29)
    }
    ```

- A reference to a method on a specific object can be passed using the object's name in place of the class name;
    ```kotlin

    fun main() {
        var jim = Person("Jim", 31)

        var personLambda = Person::age
        var jimLambda = jim::age
        
        println(personLambda(jim))
        println(jimLambda())
    }
    ```

- When invoking Java methods which require a functional interface (or "Single Abstract Method"/SAM interface) - an interface with a single function - Kotlin allows for passing a lambda instead;
    ```kotlin
    fun main() {
        val button = Button()
        button.setOnClickListener { view -> ... }
    }
    ```

    The Java code for the `setOnClickListener(...)` function is as follows;
    
    ```java
    public void setOnClickListener(OnClickListener listener) { ... }
    
    public interface OnClickListener {
        void onClick(View v);
    }
    ```

- SAM constructors allow for creating lambda "wrappers" for functional interfaces when needing to refer to an instance of such an interface directly;
    ```kotlin
    fun createExampleRunnable(): Runnable {
        // SAM constructor using the name of the underlying interface and accepts a lambda as an argument
        return Runnable { println("Done!") }
    }

    fun main() {
        var myRunnable: Runnable = createExampleRunnable()
        myRunnable.run()
    }
    ```

- Functional interfaces are can be defined in Kotlin as follows;
    ```kotlin
    fun interface IntCondition {
        fun check(i: Int)
        fun checkString(s: String) = check(s.toInt())
        fun checkChar(c: Char) = check(c.digitToInt())
    }

    fun main() {
        val isOddChecker = IntCondition { it % 2 != 0 }
        println(isOddChecker.check(1))
        println(isOddChecker.checkString("2"))
        println(isOddChecker.checkChar('3'))
    }
    ```

- As before, instances of functional interfaces can simply be replaced with a lambda;
    ```kotlin
    fun checkCondition(i: Int, condition: IntCondition): Boolean = condition.check(i)

    fun main() {
        checkCondition(1) { it % 2 != 0 }
    }
    ```

- The below type is an example of the same usage, but instead using a functional type, where '(Int)' is the parameter list and 'Boolean' is the return type. Functional types are useful in simpler scenarios, where functional interfaces are useful when in more complex scenarios

    ```kotlin
    fun main() {
        val isOddChecker: (Int) -> Boolean = { it % 2 != 0 }
        checkCondition(1, isOddChecker)
    }
    ```

- Perform multiple operations on the same object with the lambda function `with`
    ```kotlin
    fun alphabet(): String {
        val result = StringBuilder()
        for (letter in 'A'..'Z') {
            result.append(letter)
        }
        result.append("\nNow I know the alphabet!")
        return result.toString()
    }

    // becomes
    fun alphabet() = with(StringBuilder()) {
        for (letter in 'A'..'Z') {
            append(letter)
        }

        append("\nNow I know the alphabet!")

        // If, for example, the enclosing class ALSO defined a `toString()` method and
        // that was what we wanted to use instead of the `StringBuilder.toString()` method,
        // we could specify which `this` we want using the following syntax; `this@EnclosingClass.toString()`
        toString()
    }
    ```

- `apply` returns the receiver object, in contrast to `with`, where the return value is determined within the lambda
    ```kotlin
    fun alphabet() = StringBuilder().apply {
        for (letter in 'A'..'Z') {
            append(letter)
        }
        append("\nNow I know the alphabet!")
    }.toString()

    // Using the buildString standard library, which accepts a lambda with receiver of type StringBuilder and calls `toString()`
    fun alphabet() = buildString {
        for (letter in 'A'..'Z') {
            append(letter)
        }
        append("\nNow I know the alphabet!")
    }
    ```

- The buildString is one example, but there are also collection builder functions, which help create readonly List, Set or Map, while allowing the collection to be treated as mutable during construction
    ```kotlin
    val fibonacci = buildList {
        addAll(listOf(1, 1, 2)
        add(3)
        add(index = 0, element = 3)
    }
    ```

- `also` takes a receiver object, performs an action and returns the receiver object, much like apply. The difference is that also accesses the receiver object as an argument
    ```kotlin
    fun main() {
        val fruits = listOf("Apple", "Banana", "Cherry")
        val uppercaseFruits = mutableListOf<String>()
        val reversedLongFruits = fruits
            .map { it.uppercase() }
            .also { uppercaseFruits.addAll(it) }
            .filter { it.length > 5 }
            .also { println(it) }
            reversed()
        println(uppercaseFruits)
        println(reversedLongFruits)
    }

    // The above code would give the following output
    // [BANANA, CHERRY]
    // [APPLE, BANANA, CHERRY]
    // [CHERRY, BANANA]
    ```

