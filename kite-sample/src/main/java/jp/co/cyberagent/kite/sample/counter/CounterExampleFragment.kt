package jp.co.cyberagent.kite.sample.counter

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import jp.co.cyberagent.kite.KiteDslScope
import jp.co.cyberagent.kite.KiteProperty
import jp.co.cyberagent.kite.Tuple2
import jp.co.cyberagent.kite.getContextualValue
import jp.co.cyberagent.kite.kiteDsl
import jp.co.cyberagent.kite.sample.R
import jp.co.cyberagent.kite.sample.databinding.FragmentCounterExampleBinding
import jp.co.cyberagent.kite.sample.databinding.IncludeCounterBinding
import jp.co.cyberagent.kite.state
import jp.co.cyberagent.kite.subscribe

class CounterExampleFragment : Fragment(R.layout.fragment_counter_example) {

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) = kiteDsl {
    val (counter1, counter2) = createCounterState()

    bindCounterExampleFragmentUi(counter1, counter2)
  }
}

fun KiteDslScope.createCounterState(): Tuple2<KiteProperty<Int>, KiteProperty<Int>> {
  return state { 0 } to state { 0 }
}

fun KiteDslScope.bindCounterExampleFragmentUi(
  counter1: KiteProperty<Int>,
  counter2: KiteProperty<Int>
) {
  val binding = FragmentCounterExampleBinding.bind(
    getContextualValue<Fragment>().requireView()
  )

  subscribe {
    binding.textSum.text = "Sum: ${counter1.value + counter2.value}"
  }

  bindCounter(binding.counter1, counter1)
  bindCounter(binding.counter2, counter2)
}

fun KiteDslScope.bindCounter(
  binding: IncludeCounterBinding,
  countState: KiteProperty<Int>
) {
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
