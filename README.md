# kite

[![Android API](https://img.shields.io/badge/API-14%2B-blue.svg?label=API&maxAge=300)](https://www.android.com/history/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

**_This project is currently in development and the API subject to breaking changes without notice._**

A Kotlin DSL to bind Android UI components to your app state.

## Download

// TODO

## Usage

// TODO

```kotlin
// Define state
fun KiteDslScope.createCounterState(): KiteProperty<Int> {
  return state { 0 }
}

// Define how to bind UI to state
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

// Connect state and UI
class CounterFragment : Fragment(R.layout.fragment_counter) {

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) = kiteDsl {
    val counterState = createCounterState()
    bindCounterUi(countState)
  }
}
```

## Credits

This library is inspired by [Vue](https://github.com/vuejs/vue), and [React](https://github.com/facebook/react).
