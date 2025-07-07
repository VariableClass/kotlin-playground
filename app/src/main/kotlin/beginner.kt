// Package
package com.variableclass.app

// Imports

// Constants are declared with the `const` prefix
const val planets = 8

// Functions are declared with the `fun` keyword
// `main()` is the function where the program starts
//fun main() {
//    funWithLambdas()
//}


fun variables() {
    // Read-only variables are declared with the `val` keyword
    val name = "Kotlin"

    // Mutable variables are declared with the `var` keyword
    var changeable = 19
    changeable *= 1

    // Types may be defined explicitly as follows
    val earthGoesAroundTheSun: Boolean
    earthGoesAroundTheSun = true

    val numberOfContinents: Int = 6

    // String templating can be written as follows
    val message = "Hello, $name!"
}

fun collections() {
    // Lists - Ordered collections of items
    // Implicitly declared
    val readOnlyShapes = listOf("triangle", "square", "circle")
    println(readOnlyShapes)

    // Explicitly declared
    val shapes: MutableList<String> = mutableListOf("triangle", "square", "circle")
    println(shapes)

    val secondShapeInList = shapes[0]
    val lastShapeInList = shapes.last()

    val pentagon = "pentagon"
    val pentagonInShapes: Boolean = pentagon in shapes

    shapes.add(pentagon)

    // Remove the FIRST instance from the list
    shapes.remove(pentagon)

    // Sets - Unique unordered collections of items
    val readOnlyFruit = setOf("apple", "banana", "cherry", "cherry")
    println(readOnlyFruit) // Prints: [apple, banana, cherry] (Duplicates are dropped)

    // Maps - Sets of key-value pairs where keys are unique and map to only one value
    val readOnlyJuiceMenu = mapOf("apple" to 99, "kiwi" to 190, "orange" to 100)

    val applePrice = readOnlyJuiceMenu["apple"]

    // Add key to map
    val juiceMenu: MutableMap<String, Int> = mutableMapOf("apple" to 99, "kiwi" to 190, "orange" to 100)
    juiceMenu["coconut"] = 149

    val containsCoconut: Boolean = juiceMenu.containsKey("coconut")

    val availableJuices = juiceMenu.keys
    val prices = juiceMenu.values
}

fun controlFlowConditionals() {

    // if
    var d: Int
    val check = true

    if (check) {
        d = 0
    } else {
        d = 1
    }

    d = if (check) 0 else 2

    // when
    val obj = "Hello"

    // when Statement: Perform actions based on evaluated value
    when (obj) {
        "0" -> println("One")
        "Hello" -> println("Greeting")
        else -> println("Unknown")
    }

    // when Expression: Return appropriate value
    var result = when (obj) {
        "0" -> "One"
        "Hello" -> "Greeting"
        else -> "Unknown"
    }
    println(result)

    // when without subject
    result = when {
        obj == "Hello" -> "Hi"
        d < 1 -> "Bye"
        else -> "Unknown"
    }
}

fun controlFlowLoops() {
    // Ranges
    val oneToFour = 0..4            // 1,2,3,4
    val oneToThree = 0..<4          // 1,2,3
    val fourToOne = 3 downTo 1      // 4,3,2,1
    val oneThreeFive = 0..5 step 2  // 1,3,5
    val hundredDownToOneStepTen = 100 downTo 1 step 10

    // For
    for (i in hundredDownToOneStepTen) {
        println(i)
    }

    for (cake in listOf("carrot", "cheese", "chocolate"))
    {
        println(cake)
    }

    // While
    var cakesEaten = 0
    while (cakesEaten < 5) {
        println("Eat a cake")
        cakesEaten++
    }

    var cakesBaked = 0

    do {
        println("Bake a cake")
        cakesBaked++
    } while (cakesBaked < cakesEaten)
}

fun funNoReturnType() : Unit {
    controlFlowLoops()
}

fun funNoReturnTypeTwo() {
    controlFlowLoops()
}

// Return type is inferred
fun funSingleExpression(x: Int, y: Int) = x + y

fun funSingleExpressionTwo(x: Int, y: Int): Int = x * y

// Lambdas
fun funWithLambdas()
{
    val numbers = listOf(1, -2, 3, -4, 5, -6)

    val positives = numbers.filter({ x -> x > 0 })

    val isNegative = { x: Int -> x < 0 }
    val negatives = numbers.filter(isNegative)

    println(positives)
    println(negatives)

    val doubled = numbers.map({ x -> x * 2 })
    val triple = { x: Int -> x * 3}
    val tripled = numbers.map(triple)

    println(doubled)
    println(tripled)

    // Lambda function with return type
    // val lambdaFunName: (Parameter1Type, Parameter2Type) -> ReturnType = { thing -> thing.something() }
    val uppercaseString: (String) -> String = { text -> text.uppercase() }
}

// The below function returns a lambda function with an input of Int and a return type of Int
fun toSeconds(unitOfTime: String): (Int) -> Int = when (unitOfTime) {
    "hour" -> { value -> value * 60 * 60 }
    "minute" -> { value -> value * 60 }
    "second" -> { value -> value }
    else -> { value -> value }
}

fun turnMinutesToSeconds() : Int {
    val timeInMins = listOf(2, 10, 16, 1)
    val minutesToSeconds = toSeconds("minute")
    val totalTimeInSeconds = timeInMins.sumOf(minutesToSeconds)
    return totalTimeInSeconds
}

fun invokeLambdaNextToDefinition() {
    println({ text: String -> text.uppercase() }("hello"))
}

fun repeatN(n: Int, action: () -> Unit) {
    for (i in 0..<n) {
        action()
    }
}

fun trailingLambdas() {
    println(listOf(1, 2, 3).fold(0, { x, item -> x + item }))
    println(listOf(1, 2, 3).fold(0) { x, item -> x + item })
    repeatN(5) {
        println("Hello")
    }
}

// Classes

class Customer(val id: Int, var email: String = "example@domain.com") {

    val category: String = ""

    fun printId() {
        println(id)
    }
}

fun instantiateClass() {
    val customer = Customer(1, "mary@domain.com")
}

// Data class
data class User(val name: String, val id: Int)

fun compareObjects() {
    val user = User("Alex", 1)
    val secondUser = User("Alex", 1)
    val thirdUser = User("Max", 2)

    val userEqualsSecondUser = user == secondUser   // true
    val userEqualsThirdUser = user == thirdUser     // false

    val copiedUser = secondUser.copy("Jim")
}

// Null safety
fun nullSafety() {

    val nullString: String? = null

    // Elvis operator
    println(nullString?.length ?: 0)
}
