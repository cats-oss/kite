# kite

[![Android API](https://img.shields.io/badge/API-14%2B-blue.svg?label=API&maxAge=300)](https://www.android.com/history/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

**_Deperated._**

A Kotlin DSL to bind Android UI components to your app state.

## Download

// TODO

## Usage

### Start Kite DSL

You can create a `KiteDslScope` from either Activity or Fragment via extension function `kiteDsl`:

In `Actvity`:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  kiteDsl {
    // Write Kite DSL here
  }
}
```

In `Fragment`:

```kotlin
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
  kiteDsl {
    // Write Kite DSL here
  }
}
```

### Create State

In the `KiteDslScope`, you can create state via extension function `state`:

```kotlin
kiteDsl {
  // Create a state with initial value
  val count = state { 0 }
  // Assign a new value
  count.value = 3
  // Read the value
  println(count.value)
}
```

When you create `kiteDsl` inside `Activity/Fragment`, all declared states will be saved into an Android `ViewModel`.
So states can survive after `Activity/Fragment` recreation.

### Subscribe to State Change

You can create a action that will rerun when its dependent states changed via extension function `subscribe`:

```kotlin
val count = state { 0 }

// When click the button, textView.text will change.
subscribe {
  textView.text = count.value.toString()
}

button.setOnClickListener {
  count.value++
}
```

You can subscribe to more than one state:

```kotlin
val count1 = state { 0 }
val count2 = state { 0 }

// Whether count1 or count2 changed, the textView.text will change.
subscribe {
  textView.text = (count1.value + count2.value).toString()
}
```

The action will only rerun when its referenced state changed:

```kotlin
val count1 = state { 0 }
val count2 = state { 0 }

// Will only rerun when count1 changed
subscribe {
  textView1.text = count1.value.toString()
}

// Will only rerun when count2 changed
subscribe {
  textView2.text = count2.value.toString()
}
```

### Use Coroutine

`KiteDslScope` implemented `CoroutineScope`, so you can use coroutine inside `KiteDslScope`:

```kotlin
val count = state { 0 }

button.setOnClickListener {
  launch {
    delay(1000)
    count1.value++
  }
}
```

### Set/Get Contextual Value

You can set some contextual value inside `KiteDslScope` and then get them later:

```kotlin
fun KiteDslScope.setCount() {
  val count = state { 0 }
  set("count", count)
}

fun KiteDslScope.getCount() {
  setCount()
  val count = get<KiteMutableState<Int>>("count") // nullable
  val count = require<KiteMutableState<Int>>("count") // non null
}
```

### DI Support

You can construct a `KiteScopeModelFactory` and add any dependencies needed into it via `addService`:

```kotlin
@Provide
fun provideKiteScopeModelFactory(repository: Repository): KiteScopeModelFactory {
  return KiteScopeModelFactory().apply {
    addService(repository)
  }
}
```

Then initialize `kiteDsl` with injected `KiteScopeModelFactory`.
Now all services added into `KiteScopeModelFactory` will set into `KiteDslScope` as contextual value:

```kotlin
@Inject
lateinit var scopeModelFactory: KiteScopeModelFactory

kiteDsl(scopeModelFactory = scopeModelFactory) {
  val repository = requireByType<Repository>()
}
```

### Test

It's better not to directly write Kite DSL inside `Activity/Fragment`.
Instead, separate your business logic and UI binding into separate extension functions of `KiteDslScope`:

```kotlin
data class CounterUseCase(
  val count: KiteState<Int>,
  val increment: () -> Unit,
  val decrement: () -> Unit
)

fun KiteDslScope.counterUseCase(): CounterUseCase {
  val count = state { 0 }
  return CounterUseCase(
    count = count,
    increment = { count.value++ },
    decrement = { count.value-- }
  )
}

fun KiteDslScope.bindCounter(counterUseCase: CounterUseCase) {
  val binding = requireByType<FragmentCounterBinding>()
  subscribe {
    binding.textView.text = counterUseCase.count.toString()
  }

  binding.incrementButton.setOnClickListener {
    counterUseCase.increment.invoke()
  }

  binding.decrementButton.setOnClickListener {
    counterUseCase.decrement.invoke()
  }
}
```

Now you can test business logic via `runTestKiteDsl`:

```kotlin
@Test
fun testCounter() = runTestKiteDsl {
  val counter = counterUseCase()
  assert(counter.count.value == 0)
  counter.increment.invoke()
  assert(counter.count.value == 1)
}
```

Test UI with `TestKiteActivity` or `TestKiteFragment`:

```kotlin
@Test
fun testDisplayCount() = runTestKiteDsl {
  val factory = TestKiteFragment.makeFactory(
    R.layout.fragment_counter,
    TestKiteFragment.Config {
      setByType(FragmentCounterBinding.bind(it.requireView()))
      val count = state { 3 }
      val counter = CounterUseCase(
        count = count,
        increment = {},
        decrement = {}
      )
      bindCounter(counter)
    }
  )
  launchFragmentInContainer<TestKiteFragment>(factory = factory)
    .moveToState(Lifecycle.State.RESUMED)
  Espresso.onView(ViewMatchers.withText("3"))
    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
}
```

## Credits

This library is inspired by [Vue](https://github.com/vuejs/vue), and [React](https://github.com/facebook/react).
