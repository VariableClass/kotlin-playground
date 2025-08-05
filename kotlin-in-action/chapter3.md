# Chapter 3: Defining and calling functions

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

