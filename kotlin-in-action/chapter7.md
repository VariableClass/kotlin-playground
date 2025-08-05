# Chapter 7: Working with nullable values

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

