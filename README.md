# kite

**_This project is currently in development and the API subject to breaking changes without notice._**

A Kotlin DSL library to bind Android UI components to your app state.

## Usage

// TODO

```kotlin
// Define how to create counter state
fun KiteDslScope.createCounterState(): KiteProperty<Int> {
  return state { 0 }
}

// Define how to bind counter state to UI
fun KiteDslScope.bindCounterUi(countState: KiteProperty<Int>) {
  val binding = FragmentCounterBinding.bind(
    requireByType<Fragment>().requireView()
  )
  binding.buttonIncrement.setOnClickListener {
    countState.value++
  }
  binding.buttonDecrement.setOnClickListener {
    countState.value--
  }
  subscribe {
    binding.textCount.text = countState.value.toString()
  }
}

// Connect counter state and UI in Fragment
class CounterFragment : Fragment(R.layout.fragment_counter) {

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) = kiteDsl {
    val counterState = createCounterState()
    bindCounterUi(countState)
  }
}
```
