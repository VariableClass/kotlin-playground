# Chapter 9: Operator overloading and other conventions

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

