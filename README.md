# kotlin-playground

I do not speak Kotlin. I shall speak Kotlin

## Chapter 1: Kotlin: What and why

- Pragmatic, concise, safe, interoperable

## Chapter 2: Kotlin basics

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

## Chapter 3: Defining and calling functions

- The `vararg` keyword can be used to accept any number of parameters
    ```kotlin
    fun myVarargFun(vararg names: String) { ... }
    ```

- The spread operator splits an array into it's component parts, in order to pass it's contents as individual parameters
    ```kotlin
    fun someFun(args: Array<String>) {
      val list = myVarArgFun("args: ", *args)
    }
    ```

- The `infix` keyword can be used to allow for omitting te dot and parentheses when calling the function. The function may only have a single parameter, with no default value. It may not be a `vararg` parameter
    ```kotlin
    infix fun Any.to(other: Any) = Pair(this, other)

    1 to "one"
    // equivalent to
    1.to("one")
    ```

- A destructuring declaration allows for initialising several variables at once. For example;
    ```kotlin
    val (number, name) = 1 to "one"
    ```

- Triple-quoted strings avoid the need to escape characters and can be used to create multiline strings
    ```kotlin
    val myUnescapedString = """"\/4"\'."""
    val myMultilineString =
    """
    ||/
    ||'
    ||\
    """
    ```

- Local functions are functions defined within another function. This is in order to reduce duplication but where the functionality would be of no use anywhere else. Local functions have access to any variables in the scope of the enclosing function. For example;
    ```kotlin
    fun myOuterFun(car: Car) {
        
        fun myLocalFun(strToValidate: String) {
            if (strToValidate.length > 20)
            {
                throw IllegalArgumentException("String $strToValidate is too long")
            }
        }

        myLocalFun(car.make)
        myLocalFun(car.model)
    }
    ```

- Functons and variables may be defined outside of classes for more flexible code structure. They will be present as static methods in classes when referenced in Java

## Chapter 4: Classes, objects and interfaces

- Interfaces may provide default implementations for methods, but these compile to an interface and a class, so a Java class must always provide implementations for all interface methods

- Methods which provide an override implementation must always be explicitly marked as `override` to avoid unintended consequences if the base class implementation changes.

- Kotlin classes are `public` and `final` by default. To allow inheritance, a class and any of it's methods which should be overrideable should be marked as `open`. Override methods are `open` unless marked as `final`. Interfaces are `open` by default

- Final modifier
    ```kotlin
    class someClass() : baseClass {
        final override fun overriddenMethodWhichMayNotBeOverriddenBySubclasses() = 1
    }
    ```

- Abstract modifier
    ```kotlin
    abstract class someClass() {
        fun defaultImplementation() = 1
        open fun funWhichCanBeOverridden() = 
        abstract fun funWhichMustBeOverridden()
    }
    ```

- Visibility modifiers:
    ```kotlin
    public
    private
    protected
    internal
    ```

- The `sealed` modifier allows limiting implementations to the current package/module. This is useful in, for example, `when` expressions - removing the need to provide an else branch. Being `sealed` implicitly describes a class as `abstract` as well.
    ```kotlin
    sealed class Expr
    class Num(val value: Int) : Expr()
    class Sum(val left: Expr, val right: Expr): Expr()
    class Mul(val left: Expr, val right: Expr): Expr()

    fun eval(e: Expr) : Int =
        when (e) {
            is Num -> e.value
            is Sum -> eval(e.left) + eval(e.right)
            // ERROR: `when` expressionmust be exhaustive, add necessary `is Mul` branch or `else` branch instead
        }
    ```

- Kotlin differentiates between primary and secondary constructors. It also uses `init` blocks to initialise a class when using a primary constructor. The `constructor` keyword may be omitted if there are no annotations or visibility modifiers on the primary constructor
    ```kotlin
    // Initialise using only a primary constructor
    class User(val nickname: String)

    // Initialise using init block (verbose)
    class User constructor(_nickname: String) {
        val nickname: String
        
        // Initialiser block
        init {
            nickname = _nickname
        }
    }

    // Initialise at declaration
    class User(_nickname: String) {
        val nickname = _nickname
    }
    ```

- When implementing a superclass, the constructor is called at the point of inheritance;
    ```kotlin
    class Vehicle(electric: Boolean)
    class Car(electric: Boolean) : Vehicle(electric)

    class Animal
    class Dog : Animal() // Default constructor invoked, since both Animal and Dog don't define a constructor explicitly
    ```

- A constructor can be made private to prevent instantiation outside of the class itself;
    ```kotlin
    class Secret private constructor(private val agentName: String)
    ```

- In most cases, a single primary constructor with default values is sufficient. In other cases, secondary constructors may be used;
    ```kotlin
    open class Downloader {
        constructor(url: String?) { ... }
        constructor(url: URI?) { ... }
    }
    ```

- Constructors should delegate to the superclass/each other;
    ```kotlin
    open class CoolDownloader : Downloader {
        constructor(url: String?) : this(URI(url)) { ... }
        constructor(url: URI?) : super(url) { ... }
    }
    ```

- Accessing backing fields;
    ```kotlin
    class User(val name: String) {
        var address String = "unspecified"
            set(value: String) {
                println(
                    """
                    Address changed for $name:
                    "$field" -> "$value".
                    """.trimIndent()
                )
                field = value
            }
    }
    ```

- Setting accessor visibility
    ```kotlin
    class User {
        val name: String
            private set
    }
    ```

- `data` classes generate useful versions of the `equals(...)`, `toString()` and `hashCode()` methods. A `copy()` method is also generated which also duplicating the values of the object into a new instance. It is strongly reccommended to make all members of such classes immutable (define with `val`).

- Class delegation; remove boilerplate by delegating the implementation to a member;
    ```kotlin
    class DelegatingCollection<T> : Collection<T> {
        private val innerList = arrayListOf<T>()
        
        override val size: Int get() = innerList.size
        override fun isEmpty: Boolean = innerList.isEmpty()
        override fun contains(element: T): Boolean = innerList.contains(element)
        override fun iterator(): Iterator<T> = innerList.iterator()
        override fun containsAll(elements: Collection<T>): Boolean = innerList.containsAll(elements)
    }

    // becomes
    class DelegatingCollection<T>(innerList: Collection<T> = mutableListOf<T>()) : Collection<T> by innerList
    ```

- The `object` declaration in essence defines a class and creates a singleton instance of that class all in one. This happens at the point of declaration, there is no need to instantiate it elsewhere.
    ```kotlin
    object Payroll {
        val allEmployees = mutableListOf<Person>()

    fun calculateSalary() {
        for (person in allEmployees) {
            ...
        }
    }
    ```

- Nested `object`s;
    ```kotlin
    data class Person(val name: String, val age: Int) {
        object NameComparator : Comparator<Person> {
            override fun compare(p1: Person, p2: Person): Int = p1.name.compareTo(p2.name)
        }

        object AgeComparator : Comparator<Person> {
            override fun compare(p1: Person, p2: Person): Int = p1.age.compareTo(p2.age)
        }
    }

    fun runCompare() {
        val people = listOf(Person("Bob"), Person("Alice"))
        println(people.sortedWith(Person.NameComparator))
    }
    ```

- `companion` `object`s: A single nested object (per class) with access to it's private members. Especially useful for factory methods and static members. If the enclosing class has a private constructor, it is often here that that is used. "The Kotlin Way" to do static methods
    ```kotlin
    class User private constructor(val nickname: String) {
        companion object {
            fun newSubscribingUser(email: String) = User(email.substringBefore('@'))
            fun newSocialUser(accountId: Int) = User(getNameFromSocialNetwork(accountId))
        }
    }

    fun createUsers() {
        val subscribingUser = User.newSubscribingUser("bob@email.com")
        val socialUser = User.newSocialUser(4)
    }
    ```

- `object` expressions: Define anonymous `object`s ad hoc. These are not singleton instances
    ```kotlin
    interface MouseListener {
        fun onEnter()
        fun onClick()
    }

    fun main() {
        var clickCount = 0

        Button(object: MouseListener {
            override fun onEnter() { ... }
            override fun onClick() { clickCount++ }
        })
    }
    ```

- Inline classes
    ```kotlin
    fun addExpense(expense: Int) {
        // Save the expense in pence
    }

    // If attempting to save the expense in Yen, we would normally need to create a class to differentiate
    class GbpPence(val amount: Int)

    fun addExpense(expense: GbpPence) {
        // Save the expense in pence
    }

    // These GbpPence objects may be created often, before immediately being used and discarded - a heavy performance cost
    
    // Enter: inline classes - type safety without redundant object instantiations for types with a single property, initialised in the primary constructor. They don't participate in class hierarchies, but can implement interfaces, define methods and computed properties
    @JvmInline // This annotation will be uneccessary following the completion of Project Valhalla (rewriting the JVM)
    value class GbpPence(val amount: Int)
    ```

## Chapter 5: Programming with lambdas

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

## Chapter 6: Working with collections and sequences

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

## Chapter 7: Working with nullable values

- Much like smart-casting, performing a null check will allow further use of variable as though it were non-null

- Safe-call operator '`?.`' - `nullableObject?.method()`

- Null-coalescing operator '`?:`' - `nullableObject?.method() ?: "default value"`

- Safe-casting operator '`as?`', often combined with the null-coalescing operator;
    ```kotlin
    val otherPerson = other as? Person ?: return false
    ```

- Non-null assertion operator '`!!`' - `val newString = someString!!`, throw an exception if the value is null, otherwise carry on. Not for use in most situations

- `let` - A library function that will execute a lambda only if the receiver is not null;
    ```kotlin
    fun sendEmail(val recipient: String) { ... }

    fun main() {
        val email: String? = getEmailAddress()
        
        if (email != null) sendEmail(email)
        
        // becomes
        email?.let { sendEmail(it) }
    }
    ```

- `lateinit` - Initialise a property after construction without having to make it nullable. If it is accessed prior to initialisation, a `UninitializedPropertyAccessException` is returned
    ```kotlin
    class MyService {
        fun performAction(): String = "Action Done!"
    }

    class MyTest {
        private lateinit var myService: MyService

    @BeforeAll fun setUp() {
        myService = MyService()
    }

    @Test fun testAction() {
        assertEquals("Action Done!", myService.performAction())
    }
    ```

- `isNullOrEmpty()` & `isNullOrBlank()` are available extension functions for `String?`

- Generic types can be null. Non-null requires specifying a type;
    ```kotlin
    fun <T> printHashCode(t: T) {
        // T may be null, so a safe call operator is required
        println(t?.hashCode())
    }

    fun <T: Any> printHashCode(t: T) {
        // Now T can never be null, so a safe call operator is no longer necessary
        println(t.hashCode())
    }
    ```

- Types from Java libraries should be checked for nullability on a case-by-case basis, since the Kotlin compiler has no way of guaranteeing non-nullability and checking everything would be overly cumbersome

## Chapter 8: Basic types, collections and arrays

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

## Chapter 9: Operator overloading and other conventions

- Conventions provide a way to utilise shorthand operator syntax to invoke methods like `plus()`. Define the function with the `operator` prefix`
    ```kotlin
    data class Point(val x: Int, val y: Int) {
        operator fun plus(other: Point) = Point(x + other.x, y + other.y)
    }

    fun main() {
        val p1 = Point(2, 5)
        val p2 = Point(4, 1)
        
        // Use the operator to invoke the `plus()` method on Point
        println(p1 + p2)
    }
    ```

- The overloadable binary arithmetic operators include;
    - `+` (`plus()`)
    - `-` (`minus()`)
    - `*` (`times()`)
    - `/` (`div()`)
    - `%` (`mod()`)

- Kotlin does not support commutativity, so an operator function for scaling a `Point` would need an equivalent function for `Double`;
    ```kotlin
    operator fun Point.times(scale: Double) = Point(x * scale.toInt(), y * scale.toInt())

    operator fun Double.times(point: Point) = point * this
    ```

- Unary operators can also be overloaded;
    ```kotlin
    operator fun Point.unaryMinus() = Point(-x, -y)
    ```

- The overloadable unary operators include:
    - `+a` (`unaryPlus()`)
    - `-a` (`unaryMinus()`)
    - `!a` (`not()`)
    - `++a`, `a++` (`inc()`)
    - `--a`, `a--` (`dec()`)

- The overloadable comparison operators include:
    - `==` (`equals()`) (`!=` will simply invoke a call to `equals()` and negate it). `equals()` will always been written as an `override` function, without the `operator` prefix, since it is always inherited from the `Any` type
    - `>`, `>=`, `<` and `<=` will all use the `compareTo()` function 

- Collection conventions can also be overloaded
    ```kotlin
    ...
    operator fun Point.get(index: Int) = when (index) {
        0 -> x
        1 -> y
        else -> throw IndexOutOfBoundsException("Invalid coordinate $index")
    }

    operator fun MutablePoint.set(index: Int, value: Int) {
        when (index) {
            0 -> x = value
            1 -> y = value
            else -> throw IndexOutOfBoundsException("Invalid coordinate $index")
        }
    }
    ...

    data class Rectangle(val upperLeft: Point, val lowerRight: Point)

    operator fun Rectangle.contains(p: Point) = p.x in upperLeft.x..<lowerRight.x && p.y in upperLeft.y..<lowerRight.y

    fun main() {
        val point = MutablePoint(1, 2)
        val xCoordinate = point[0]
        val newYCoordinate = 5
        point[1] = newYCoordinate

        val rect = Rectangle(Point(0, 0), Point(5,10))
        val pointInRectangle = point in rect
    }
    ```

- The overloadable collection conventions are;
    - `thing[index]` (`get(index)`)
    - `thing[index] = newValue` (`set(index, newValue)`)
    - `item in collection` (`collection.contains(item)`)

- Range conventions can also be overloaded;
    ```kotlin
    operator fun <T: Comparable<T>> T.rangeTo(that: T): ClosedRange<T>
    operator fun <T: Comparable<T>> T.rangeUntil(that: T): OpenRange<T>
    ```
- The overloadable range conventions are;
    - `thing..otherThing` (`thing.rangeTo(otherThing)`)
    - `thing..<otherThing` (`thing.rangeUntil(otherThing)`)

- The iterator convention can also be overloaded;
    ```kotlin
    operator fun ClosedRange<LocalDate>.iterator(): Iterator<LocalDate> =
        object : Iterator<LocalDate> {
            var current = start
            override fun hasNext() = current <= endInclusive
            override fun next(): LocalDate {
                val thisDate = current
                current = current.plusDays(1)
                return thisDate
            }
    
    fun main() {
        val newYear = LocalDate.ofYearDay(2042, 1)
        val daysOff = newYear.minusDays(1)..newYear
        for (dayOff in daysOff) { println(dayOff) }
    }
    ```

- Methods to enable destructuring declarations are automatically generated for `data` classes based on the position within the primary constructor, but can be manually added for non-data classes as follows;
    ```kotlin
    class Point(val x: Int, val y: Int) {
        operator fun component1() = x
        operator fun component2() = y
    }

    fun main() {
        val (x, y) = Point(1, 2)
        println(x) // 1
        println(y) // 2
    }

    data class NameComponents(val name: String, val extension: String)

    fun splitFilename(fullName: String): NameComponents {
        val result = fullName.split('.', limit = 2)
        return NameComponents(result[0], result[1])
    }
    
    // The above function can be simplified since component functions are also generated for (the first five) collection members
    fun splitFileNameImproved(fullName: String): NameComponents {
        val (name, extension) = fullName.split('.', limit = 2)
        return NameComponents(name, extension)
    }

    fun main2() {
        val (name, extension) = splitFilename("exampleFile.kt")
        println(name) // exampleFile
        println(extension) // kt
    }
    ```

- It is possible to ignore components when destructuring;
    ```kotlin
    data class Person(val firstName: String, val lastName: String, val age: Int)

    fun main() {
        val (firstName, _, age) = Person("Jim", "Jimsson", 100)
        println("$firstName is $age years old")
    }
    ```

- It is worth remembering that destructuring in Kotlin is, currently, only positional. Name-based destructuring may be added in future

- Delegated properties - delegating a task to a helper object. Implementing, for example, lazy e-mail retrieval without a delegate might look as follows;
    ```kotlin
    class Email { ... }

    fun loadEmails(person: Person): List<Email> {
        println("Load emails for ${person.name}")
        return listOf(Email(), Email(), Email())
    }
    
    class Person(val name: String) {
        // In this example, we make use of a backing property
        private var _emails: List<Email>? = null
        
        val emails: List<Email>
            get() {
                if (_emails == null) {
                    _emails = loadEmails(this)
                }
                return _emails!!
            }
    }

    fun main() {
        val p = Person("Alice")
        p.emails 
    }
    ```

- Using a delegated property, it would instead look as follows;
    ```kotlin
    class Person(val name: String) {
        // lazy is a standard library function
        // by instructs the emails property to be returned by way of the lazy library funtion/loadEmails function
        val emails by lazy { loadEmails(this) }
    }
    ```

- By convention, a delegate type must define an operator functions `getValue(...)`, as well as an operator function `setValue(...)` for mutable properties and, optionally, the `provideDelegate()` operator function which can describe specific logic for building a delegate instance of the type

- The `Delegates.observable` function allows for adding an observer of property changes;
    
    ```kotlin
    open class Observable {
        val observers = mutableListOf<Observer>()
    }

    class Person(val name: String, age: Int, salary: Int) : Observable() {
        private val onChange = { property: KProperty<*>, oldValue: Any?, newValue: Any? -> notifyObservers(property.name, oldValue, newValue) }
        
        var age by Delegates.observable(age, onChange)
        var salary by Delegates.observable(age, onChange)
    }
    ```

## Chapter 10: Higher-order functions: Lambdas as parameters and return values

- Function types define the inputs and outputs of the lambda, for example; `(Int, String) -> Boolean` or `() -> Unit`

- Here's an example higher-order function that defines a lambda function argument, with a function type;
    ```kotlin
    fun higherOrderFunPrintLambdaOutput(num1: Int, num2: Int, operationToPerformOnTwoNumbers: (Int, Int) -> Int) {
        println("Output: ${operationToPerformOnTwoNumbers(num1, num2)}")
    }

    fun main() {
        higherOrderFunPrintLambdaOutput(2, 3) { x, y -> x + y }
    }
    ```

- And a self-implemented version of the `filter` function (but for `String` only);
    ```kotlin
    fun String.filter(predicate: (Char) -> Boolean) = buildString {
        this@filter.forEach {
            if (predicate(it)) append(it)
        }
    }
    ```

- Function types are normal interfaces under the hood, a lambda is an instance of a class generated by the compiler which implements the interface.
    ```kotlin
    // The interfaces available for use simply correspond to how many arguments the lambda accepts

    // 0 input arguments
    interface Function0<out R> {
        operator fun invoke(): R
    }

    // 1 input argument
    interface Function1<P1, out R> {
        operator fun invoke(p1: P1): R
    }

    interface Function2<P1, P2, out R> {
        operator fun invoke(p1: P1, p2: P2): R
    }
    
    // So an equivalent way of writing the filter function would be;
    fun String.filter(predicate: Function1<Char, Boolean>) = ...
    ```

- Function types can be used as the return type of a function as well;
    ```kotlin
    enum class Delivery { STANDARD, EXPEDITED }

    class Order(val itemCount: Int)

    fun getShippingCostCalculator(delivery: Delivery): (Order) -> Double {
        return when (delivery) {
            is Delivery.STANDARD -> { order -> 1.2 * order.itemCount }
            is Delivery.EXPEDITED -> { order -> 6 + 2.1 * order.itemCount }
        }
    }

    fun main() {
        val calculator = getShippingCostCalculator(Delivery.EXPEDITED)
        println("Shipping costs: ${calculator(Order(3))}")
    }
    ```

- Inlining functions with the `inline` prefix with replace the reference with the actual code snippet at compile time, instead of generating classes and objects during runtime. All lambda parameters are also inlined, unless specifically marked with the `noninline` prefix.

- The `withLock()` extension function can be used to access objects in a threadsafe way
    ```kotlin
    ...
    val l: Lock = ReentrantLock()
    l.withLock {
        // Do some action on the resource
    }
    ...
    ```

- `use()` and `useLines()` can be used to access closeable resources
    ```kotlin
    fun readFirstLineFromFileUse (fileName: String): String {
        BufferedReader(FileReader(filename)).use { br -> return br.readLine() }
    }

    fun readFirstLineFromFileUseLines(filename: String): String {
        Path(fileName).useLines {
            return it.first()
        }
    }
    ```

- Non-local return: Invoking return within a lambda will return from the wider context, as long as the function has been inlined

- Just as with `this@label`, it's possible to `return@label`, as below;
    ```kotlin
    fun lookForAliceNamedLabelReturn(people: List<Person>) {
        people.forEach myLabel@{
            if (it.name != "Alice") return@myLabel  // This return will return to the start of the lambda function, then the forEach lambda will continue to the next iteration
            println("Found Alice!")
        }
    }

    fun lookForAliceLabelReturn(people: List<Person>) {
        people.forEach {
            it (it.name != "Alice") return@forEach  // This return will return to the forEach
            println("Found Alice!")
        }
    }
    ```

- Anonymous functions are another way to inline code snippets, an example is given below;
    ```kotlin
    fun lookForAliceLambdaFun(people: List<Person>) {
        people.forEach {
            if (it.name == "Alice") return  // This return will return out of the enclosing function
        }
    }

    fun lookForAliceAnonFun(people: List<Person>) {
        people.forEach(fun(person) {
            if (person.name == "Alice") return // This return will return out of the anonymous function
        })
    }
    ```

## Chapter 11: Generics

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

## Chapter 12: Annotations and reflection

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

## Chapter 13: DSL construction

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

## Chapter 14: Coroutines

- The `suspend` prefix runs a function on coroutines, suspending it's execution til it is no longer waiting

- Coroutines can be spun up with a number of builder functions;
    - `runBlocking()` bridges the gap between blocking and suspending functions
        ```kotlin
        import kotlinx.coroutines.*
        import kotline.time.Duration.Companion.milliseconds

        suspend fun doSomethingSlowly() {
            delay(500.milliseconds)
            println("I'm done")
        }

        fun main() = runBlocking {
            doSomethingSlowly()
        }
        ```
    - `launch()` starts new coroutines which don't return values
        ```kotlin
        fun main() = runBlocking {
            launch {
                // Do work
            }
        }
        ```
    - `async()` is used for computing values in an asynchronous manner
        ```kotlin
        suspend fun slowlyAddNumbers(a: Int, b: Int): Int {
            log ("Waiting a bit before calculating $a + $b)
            delay(100.milliseconds * a)
            return a + b
        }

        fun main() = runBlocking {
            val myDeferred = async { slowlyAddNumbers(2, 2) }
            val myOtherDeferred = async { slowlyAddNumbers(4, 4) }

            val firstDeferredResult = myDeferred.await()
            val secondDeferredResult = myOtherDeferred.await()
        }
        ```

- The `-Dkotlinx.coroutines.debug` JVM option shows additional information about running coroutines

- `Dispatcher`s determine where coroutines will run;
    - `Dispatchers.Default` is the default option, unless anything else is specified. This `Dispatcher` has an available thread pool equal to the number of CPU cores
    - `Dispatchers.Main` has a single thread used for UI tasks, whether this be desktop, mobile or web
    - `Dispatchers.IO` is used for inherently blocking code and has an available thread pool of 64 or total CPU cores; whichever is larger
    - `Dispatchers.Unconfined` allows coroutines to run without any kind of thread confinement and should only be used in specific cases
    - `limitedParallelism(n)` allows for setting custom constraints on parallelism for a dispatcher, but is also reserved for specialty cases

- A `Dispatcher` can be specified as part of the coroutine builder function;
    ```kotlin
    fun main() {
        runBlocking {
            log("Doing some work")
            launch(Dispatchers.Default) {
                log("Doing some background work")
            }
        }
    }
    ```

- The `withContext()` function allows switching to another `Dispatcher`; for example running some IO tasks, before switching back to the UI thread to show the results
    ```kotlin
    launch(Dispatchers.Default) {
        val result = performBackgroundOperation()
        withContext(Dispatchers.Main) {
            updateUI(result)
        }
    }
    ```

- A `Mutex` lock allows for locking an object in a threadsafe manner;
    ```kotlin
    fun main() = runBlocking {
        val mutex = Mutex()
        var x = 0
        repeat(10_000) {
            launch(Dispatchers.Default) {
                mutex.withLock {
                    x++
                }
            }
        }
        delay(1.seconds)
        println(x)
    }
    ```

- There are also threadsafe data structures such as `AtomicInteger` & `ConcurrentHashMap`

- `CoroutineContext`s provide context around the current coroutine, such as it's `Dispatcher` or `CoroutineName`. It is available through the compiler instrinsic `coroutineContext` property - a property not defined in code, by handled by the compiler

- Extra arguments can be overriden when creating a coroutine;
    ```kotlin
    fun main() {
        runBlocking(Dispatchers.IO + CoroutineName("myRoutine") {
            // Do some work
        }
    }
    ```

## Chapter 15: Structured concurrency

- Coroutine scopes establish parent-child relationships between coroutines; `launch` & `async` extend the `CoroutineScope` interface and therefore automatically become children of the parent scope

- The `coroutineScope` function enables creating a new coroutine scope;
    ```kotlin
    import kotlinx.coroutines.x
    import kotlin.random.Random
    import kotlin.time.Duration.Companion.milliseconds

    suspend fun generateValue() : Int {
        delay(500.milliseconds)
        return Random.nextInt(0, 10)
    }

    suspend fun computeSum() {
        log("Computing a sum...")
        val sum = coroutineScope {
            val a = async { generateValue() }
            val b = async { generateValue() }
            a.await() + b.await()
        }
        log("Sum is $sum")
    }

    fun main() = runBlocking {
        computeSum()
    }
    ```

- A `Job` - created as part of a coroutine - will automatically cancel coroutines in the same scope upon an unhandled exception and propogate the exception further

- Another alternative is using the `CoroutineScope` constructor function. By default this will be created with a `Job`, but specifically passing in a `SupervisorJob` will allow for manually defining the behaviour of the coroutine;
    ```kotlin
    class ComponentWithScope(dispatcher: CoroutineDispatcher = Dispatchers.Default) {
        private val scope = CoroutineScope(dispatcher + SupervisorJob())

        fun start() {
            log("Starting")
            scope.launch {
                while(true) {
                    delay(500.milliseconds)
                    log("Component working!")
                }
            }
            
            scope.launch {
                log("Doing a one-off task...")
                delay(500.milliseconds)
                log("Task done!")
            }
        }

        fun stop() {
            log("Stopping")
            scope.cancel()
        }
    }

    fun main() {
        val c = ComponentWithScope()
        c.start()
        Thread.sleep(2000)
        c.stop()
    }
    ```

- `GlobalScope` is a globally available scope, but lacks the benefits of structured concurrency that defining scopes provides and is generally not advised for the majority of use cases

- A `Job` can be cancelled with the `cancel()` function;
    ```kotlin
    fun main() {
        runBlocking {
            var launchedJob = launch {
                log("I'm launched!")
                delay(1000.milliseconds)
                log("I'm done!")
            }
            
            var asyncDeferred = async {
                log("I'm async!")
                delay(1000.milliseconds)
                log("I'm done!")
            }

            delay(200.milliseconds)
            launchedJob.cancel()
            async.cancel()
        }
    }
    ```

- The `withTimeout` and `withTimeoutOrNull` functions allow for computing a value while setting a maximum time limit;
    ```kotlin
    import kotlinx.coroutines.*
    import kotlin.time.Duration.Companion.seconds
    import kotlin.time.Duration.Companion.milliseconds

    suspend fun calculateSomething(): Int {
        delay(3.seconds)
        return 2 + 2
    }

    fun main() = runBlocking {
        val quickResult = withTimeoutOrNull(500.milliseconds) {
            calculateSomething()
        }
        println(quickResult)    // null
        
        val slowResult = withTimeoutOrNull(5.seconds) {
            calculateSomething()
        }
        println(slowResult)     // 4
    }
    ```

- Cancelling a coroutine cascades through it's children. A `CancellationException` is thrown from suspension points - places in the code where a coroutine is suspended (a `delay` or waiting on an IO call, for example). This should not be accidentally swallowed by some over-eager `try`-`catch` logic

- Calls to other cancellable functions introduces suspension points, but these can and should be introduced with checking the `isActive` property, the `ensureActive()` and the `yield()` functions.

    - Checking `isActive` allows for graceful termination of the currently running coroutine, closing resources and other related clean-up;
        ```kotlin
        ```

    - Invoking `ensureActive()` checks the `isActive` property and immediately throws a `CancellationException` if it is false
        ```kotlin
        val myJob = launch {
            repeat(5) {
                doCpuHeavyWork()
                ensureActive()
            }
        }
        ```

    - Invoking `yield()` creates a suspension point, allowing other coroutines to work in the meantime
        ```kotlin
        import kotlinx.coroutines.*

        fun doCpuHeavyWork(): Int {
            var counter = 0
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() < startTime + 500) {
                counter++
                yield()
            }
            return counter
        }
    
        fun main() {
            runBlocking {
                launch {
                    repeat(3) {
                        doCpuHeavyWork()
                    }
                }
                launch {
                    repeat(3) {
                        doCpuHeavyWork()
                    }
                }
            }
        }
        ```

- Always keep open resources in mind when, also in coroutines. Using a `try-finally` statement, or simply a `use { }` block, if the resource implements the `AutoCloseable` interface are both recommended approaches

- In Ktor, a `PipelineContext` (which extends `CoroutineScope`) provides the request-level scope, cancelling any work if the client cancels the request. In order to run work independent of the request scope, `Application` - which also extends `CoroutineScope` and has the same lifetime as the Ktor application - should be chosen as the parent scope;
    ```kotlin
    routing {
        get("/requestData") {
            launch {
                println("I'm doing some background work!")
                delay(200.milliseconds)
                println("I'm done!")
            }
        }
        get("/backgroundTask") {
            call.application.launch {
                println("I'm doing some background work!")
                delay(5000.milliseconds)
                println("I'm done!")
            }
        }
    }
    ```

## Chapter 16: Flows

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

## Chapter 17: Flow operators

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

## Chapter 18: Error handling and testing

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

## Glossary

- Idiomatic: Writing code "the Kotlin way"
- Ternary: Composed of three items. An example ternary operation in C# looks like: `var result = condition ? a : b;`. In Kotlin, `if` functions the same way; `val result = if (condition) a else b`
- Multiparadigm: OOP & functional
- Vacuous truth: `all` returns true when there are no items in the array
- Expression: Has a value
- Statement: Top-level element without it's own value
- Block body: With curly braces
- Expression body: Like an expression - no curly braces
- Statically typed: Types are resolved at compile-time, not runtime, like in dynamically typed languages like JavaScript and Python
- Type inference: Inferring the type of an expression
- String interpolation: Syntax which allows for referring to expressions within a string value
- Commutativity: The ability to swap the left and right sides of an operator
- Destructuring declarations: Initialising several variables at once from a single value
- Reified type arguments: Type arguments accessible at runtime due to being inlined
- Idempotent: Will give the same output, given the same inputs
- Immutable: Cannot be mutated
- Polymorphism
