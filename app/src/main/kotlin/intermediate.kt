package com.variableclass.app

import kotlin.properties.Delegates.observable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlinx.datetime.*

fun main() {
    useImportedLibrary()
}

// Extension functions
fun String.bold(): String = "<b>$this</b>"

fun testExtensionFun(): Unit = println("hello".bold())

// Scope functions

// let: Perform null checks and later perform further actions with the returned object
fun sendNotification(recipientAddress: String): String {
    println("Yo $recipientAddress!")
    return "Notification sent!"
}

fun getNextAddress(): String {
    return "sebastian@jetbrains.com"
}

fun exampleLet() {
    val address: String = getNextAddress()

    var confirm = if(address != null) {
        sendNotification(address)
    } else { null }

    confirm = address?.let {
        sendNotification(it)
    }

    sendNotification(address)
}

// apply: Use to initialize objects, like a class instance, at the time of creation rather than later on in your code
// This approach makes your code easier to read and manage.
class Client() {
    var token: String? = null
    fun connect() = println("connected!")
    fun authenticate() = println("authenticated!")
    fun getData(): String = "Mock data"
}

val normalClient = Client()

fun exampleWithoutApply() {

    normalClient.token = "asdf"
    normalClient.connect()
    normalClient.authenticate()
    normalClient.getData()
}

val clientButWithApply = Client().apply {
    token = "asdf"
    connect()
    authenticate()
}

fun invokeClient() {
    clientButWithApply.getData()
}

// run: Use to initialise an object at a specific moment in your code and immediately compute a result
val runClient: Client = Client().apply {
    token = "asdf"
}

fun exampleRun() {
    val result: String = runClient.run {
        connect()
        authenticate()
        getData()
    }
}

// also: Complete an additional action with an object and then return the object to continue using it
fun exampleAlso() {
    val medals: List<String> = listOf("Gold", "Silver", "Bronze")
    val reversedLongUppercaseMedals: List<String> =
        medals
            .map { it.uppercase() }
            .also { println(it) }
            .filter { it.length > 4 }
            .also { println(it) }
            .reversed()
    println(reversedLongUppercaseMedals)
}

// with: Use when needing to call multiple functions on an object. Not an extension function
class Canvas {
    fun rect(x: Int, y: Int, w: Int, h: Int): Unit = println("$x, $y, $w, $h")
    fun circ(x: Int, y: Int, rad: Int): Unit = println("$x, $y, $rad")
    fun text(x: Int, y: Int, str: String): Unit = println("$x, $y, $str")
}

fun exampleWith() {
    val mainMonitorPrimaryBufferBackedCanvas = Canvas()

    mainMonitorPrimaryBufferBackedCanvas.text(10, 10, "Foo")
    mainMonitorPrimaryBufferBackedCanvas.rect(20, 30, 100, 50)
    mainMonitorPrimaryBufferBackedCanvas.circ(40, 60, 25)
    mainMonitorPrimaryBufferBackedCanvas.text(15, 45, "Hello")
    mainMonitorPrimaryBufferBackedCanvas.rect(70, 80, 150, 100)
    mainMonitorPrimaryBufferBackedCanvas.circ(90, 110, 40)
    mainMonitorPrimaryBufferBackedCanvas.text(35, 55, "World")
    mainMonitorPrimaryBufferBackedCanvas.rect(120, 140, 200, 75)
    mainMonitorPrimaryBufferBackedCanvas.circ(160, 180, 55)
    mainMonitorPrimaryBufferBackedCanvas.text(50, 70, "Kotlin")

    with(mainMonitorPrimaryBufferBackedCanvas) {
        text(10, 10, "Foo")
        circ(40, 60, 25)
        text(15, 45, "Hello")
        rect(70, 80, 150, 100)
        circ(90, 110, 40)
        text(35, 55, "World")
        rect(120, 140, 200, 75)
        circ(160, 180, 55)
        text(50, 70, "Kotlin")
    }
}

// Lambda expressions (function literals) with receiver (.NET extension methods)
fun functionLiterals() {
    fun StringBuilder.appendText() { append("Hello!") }

    val stringBuilder = StringBuilder()
    stringBuilder.appendText()
    println(stringBuilder.toString())
}

class MenuItem(val name: String)

class Menu(val name: String) {
    val items = mutableListOf<MenuItem>()

    fun item(name: String) {
        items.add(MenuItem(name))
    }
}

fun menu(name: String, init: Menu.() -> Unit): Menu {
    val menu = Menu(name)

    menu.init()
    return menu
}

fun printMenu(menu: Menu) {
    println("Menu: ${menu.name}")
    menu.items.forEach { println(" Item: ${it.name}")}
}

fun functionLiteralsExample() {
    val mainMenu = menu("Main Menu") {
        item("Home")
        item("Settings")
        item("Exit")
    }

    printMenu(mainMenu)
}

fun fetchData(callback: StringBuilder.() -> Unit) {
    val builder = StringBuilder("Data received")
    builder.callback()
}

fun functionLiteralsTest() {
    fetchData {
        append(" - Processed")
        println(this.toString())
        // Data received - Processed
    }
}

// Classes and interfaces
// Abstract classes
abstract class Product(val name: String, var price: Double) {
    abstract val category: String

    fun productInfo(): String {
        return "Product: $name, Category: $category, Price: $price"
    }
}

class Electronic(name: String, price: Double) : Product(name, price) {
    override val category = "Electronic"
}

fun inheritanceTest() {
    val laptop = Electronic("Laptop", 1200.0)
    println(laptop.productInfo())
}

// Interfaces
interface PaymentMethod {
    fun initiatePayment(amount: Double): String
}

class CreditCardPayment(val cardNumber: String, val cardHolderName: String, val expireDate: String) : PaymentMethod {
    override fun initiatePayment(amount: Double): String {
        return "Payment of $$amount initiated using card ending in ${cardNumber.takeLast(4)}."
    }
}

fun paymentMethodTest() {
    val paymentMethod = CreditCardPayment("1234 5678 9012 3456", "John Doe", "12/25")
    println(paymentMethod.initiatePayment(100.0))
}

// Delegation
interface Drawable {
    fun draw()
    fun resize()
    val color: String?
}

class Circle : Drawable {
    override fun draw() {
        TODO("An example implementation")
    }

    override fun resize() {
        TODO("An example implementation")
    }

    override val color = null
}

class RedCircle(circle: Circle) : Drawable by circle {
    override val color = "red"
}

// Object declarations (singleton instance)
object DoAuth {
    fun takeParams(username: String, password: String) {
        println("input Auth parameters = $username:$password")
    }
}

fun singletonTest() {
    // DoAuth created here
    DoAuth.takeParams("user", "password")
}

// Data objects
data object AppConfig {
    var appName: String = "My Application"
    var version: String = "1.0.0"
}

fun dataSingletonTest() {
    println(AppConfig)

    println(AppConfig.appName)
}

// Companion objects (Static methods)
class BigBen {

    // Classes can have only a single companion class
    // It doesn't need a name and will be called Companion if one is not provided
    companion object Bonger {
        fun getBongs(nTimes: Int) {
            repeat(nTimes) { print("BONG ") }
        }
    }
}

fun companionObjectTest() {
    BigBen.getBongs(12)
}

// Open and special classes

open class Vehicle(val make: String, val model: String) {
    open fun displayInfo() {
        println("Vehicle Info: Make - $make, Model - $model")
    }
}

open class Car(make: String, model: String, val numberOfDoors: Int): Vehicle(make, model) {
    override fun displayInfo() {
        println("Car Info: Make - $make, Model - $model, Doors - $numberOfDoors")
    }
}

fun openClassTest() {
    val car = Car("Skoda", "Enyaq", 5)

    car.displayInfo()
}

interface EcoFriendly {
    val emissionLevel: String
}

interface ElectricVehicle {
    val batteryCapacity: Double
}

class ElectricCar(
    make: String,
    model: String,
    numberOfDoors: Int,
    val capacity: Double,
    val emission: String
) : Car(make, model, numberOfDoors), EcoFriendly, ElectricVehicle {
    override val batteryCapacity: Double = capacity
    override val emissionLevel: String = emission
}

// Sealed classes

sealed class Mammal(val name: String)

class Cat(val catName: String) : Mammal(catName)
class Human(val humanName: String, val job: String) : Mammal(humanName)

fun greetMammal(mammal: Mammal): String {
   return when (mammal) {
       is Human -> "Hello ${mammal.name}; You're working as a ${mammal.job}"
       is Cat -> "Hello ${mammal.name}"
   }
}

fun sealedClassTest() {
    println(greetMammal(Cat("Snowy")))
    println(greetMammal(Human("Alice", "Zookeeper")))
}

// Enum classes
enum class State {
    IDLE, RUNNING, FINISHED
}

fun enumTest() {
    val state = State.RUNNING
    val message = when (state) {
        State.IDLE -> "It's idle"
        State.RUNNING -> "It's running"
        State.FINISHED -> "It's finished"
    }

    println(message)
}

enum class Color(val rgb: Int) {
    RED(0xFF0000),
    GREEN(0x00FF00),
    BLUE(0x0000FF),
    YELLOW(0xFFFF00);

    fun containsRed() = (this.rgb and 0xFF0000 != 0)
}

// Inline value classes
// Must have single property initialised in header
@JvmInline
value class Email(val address: String)

fun sendEmail (email: Email) {
    println("Sending email to ${email.address}")
}

// Properties
class Contact(val id: Int, var email: String) {
    val category: String = ""
}

// EQUIVALENT TO
class VerboseContact(val id: Int, var email: String) {
    var category: String = ""
        get() = field
        set(value) {
            field = value
        }
}

// Extension properties
data class Person(val firstName: String, val lastName: String)

val Person.fullName: String
    get() = "$firstName $lastName"

fun extensionPropertyTest() {
    val person = Person("John", "Doe")
    println(person.fullName)
}

// Delegated properties
class CachedStringDelegate {
    var cachedValue: String? = null

    operator fun getValue(thisRef: DelegateExampleUser, property: Any?): String {
        if (cachedValue == null) {
            cachedValue = "${thisRef.firstName} ${thisRef.lastName}"
            println("Computed and cached: $cachedValue")
        } else {
            println("Accessed from cache: $cachedValue")
        }

        return cachedValue ?: "Unknown"
    }
}

class DelegateExampleUser(val firstName: String, val lastName: String) {
    val displayName: String by CachedStringDelegate()
}

fun delegatedPropertyTest() {
    val user = DelegateExampleUser("John", "Doe")

    println(user.displayName)
    println(user.displayName)
}

// Standard delegates

// Lazy properties
class Database {
    fun connect() {
        println("Connecting to the database...")
    }

    fun query(sql: String) : List<String> {
        return listOf("Data1", "Data2", "Data3")
    }
}

val databaseConnection: Database by lazy {
    val db = Database()
    db.connect()
    db
}

fun fetchData() {
    val data = databaseConnection.query("SELECT * FROM data")
    println("Data: $data")
}

fun lazyPropertyTest() {
    fetchData()
    fetchData()
}

// Observable properties

class Thermostat {
    var temperature: Double by observable(20.0) { _, old, new ->
        if (new > 25) {
            println("Warning: Temperature is too high! ($old -> $new°C)")
        } else {
            println("Temperature updated: $old°C -> $new°C")
        }
    }
}

fun observablePropertyTest() {
    val thermostat = Thermostat()
    thermostat.temperature = 22.5
    thermostat.temperature = 27.0
}

// Null safety

// is & !is operators
fun printObjectType(obj: Any) {
    when (obj) {
        is Int -> println("It's an integer with value $obj")
        !is Double -> println("It's NOT a double")
        else -> println("Unknown type")
    }
}

fun isNotIsTest() {
    val myInt = 42
    val myDouble = 3.14
    val myList = listOf(1, 2, 3)

    printObjectType(myInt)
    printObjectType(myDouble)
    printObjectType(myList)
}

// as & as? operators
fun asAsNullableTest() {
    val a: String? = null

    // Unsafe cast
    val b = a as String
    print(b)

    // Safe cast (returns null on failure)
    val c = a as? String?
    print(c)
}

fun calculateTotalStringLength(items: List<Any>): Int {
    var totalLength = 0

    for (item in items) {
        totalLength += if (item is String) {
            item.length
        } else {
            0
        }
    }

    return totalLength
}

fun calculateTotalStringLengthAbridged(items: List<Any>) = items.sumOf { (it as? String)?.length ?: 0 }

// Null values and collections
fun usefulNullsafeFunctions() {
    val emails: List<String?> = listOf("alice@example.com", null, "bob@example.com")
    val validEmails = emails.filterNotNull()
    println(validEmails)

    val serverConfig = mapOf(
        "appConfig.json" to "App Configuration",
        "dbConfig.json" to "Database Configuration"
    )

    val requestedFile = "appConfig.json"
    val configFiles = listOfNotNull(serverConfig[requestedFile])

    println(configFiles)

    // The below functions are meant to be used with collections that do not contain null values
    val temperatures = listOf(15, 18, 21, 21, 19, 17, 16)
    val maxTemperature = temperatures.maxOrNull()
    println("Highest temp is ${maxTemperature ?: "No data"}")

    val minTemperature = temperatures.minOrNull()
    println("Lowest temp is ${minTemperature ?: "No data"}")

    val singleHotDay = temperatures.singleOrNull { it == 30 }
    println("Single hot day with 30 degrees: ${singleHotDay ?: "No data"}")

    data class NullLambdaUser(val name: String?, val age: Int?)

    val users = listOf(
        NullLambdaUser(null, 25),
        NullLambdaUser("Alice", null),
        NullLambdaUser("Bob", 30),
    )

    val firstNonNullName = users.firstNotNullOfOrNull { it.name }
    println(firstNonNullName)

    val itemPrices = listOf(20, 30, 40, 50, 60)

    val totalPrice = itemPrices.reduceOrNull { runningTotal, price -> runningTotal + price }
    println("Total price: ${totalPrice ?: "No items"}")
}

// Early returns and the Elvis operator
data class EarlyReturnUser(
    val id: Int,
    val name: String,
    // List of friend user IDs
    val friends: List<Int>
)

// Function to get the number of friends for a user
fun getNumberOfFriends(users: Map<Int, EarlyReturnUser>, userId: Int): Int {
    // Retrieves the user or return -1 if not found
    val user = users[userId] ?: return -1
    // Returns the number of friends
    return user.friends.size
}

fun earlyReturnTest() {
    // Creates some sample users
    val user1 = EarlyReturnUser(1, "Alice", listOf(2, 3))
    val user2 = EarlyReturnUser(2, "Bob", listOf(1))
    val user3 = EarlyReturnUser(3, "Charlie", listOf(1))

    // Creates a map of users
    val users = mapOf(1 to user1, 2 to user2, 3 to user3)

    println(getNumberOfFriends(users, 1))
    // 2
    println(getNumberOfFriends(users, 2))
    // 1
    println(getNumberOfFriends(users, 4))
    // -1
}

// Libraries and APIs

// Importing
// kotlin.time.* will import everything within the package

// Wildcard imports don't work for companion objects. These must be explicitly declared
// kotlin.time.Duration
// kotlin.time.Duration.Companion.hours
// kotlin.time.Duration.Companion.minutes

fun importTest() {
    val thirtyMinutes: Duration = 30.minutes
    val halfAnHour: Duration = 0.5.hours
    println(thirtyMinutes == halfAnHour)
}

fun useImportedLibrary() {
    val now = Clock.System.now()
    println ("Current time UTC: $now")

    val zone = TimeZone.of("Europe/Oslo")
    val localDateTime = now.toLocalDateTime(zone)
    println("Local date time in Oslo: $localDateTime")
}
