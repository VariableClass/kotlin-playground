# Chapter 4: Classes, objects and interfaces

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

