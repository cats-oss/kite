package jp.co.cyberagent.kite.sample.counter

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import jp.co.cyberagent.kite.core.KiteDslScope
import jp.co.cyberagent.kite.core.KiteMutableState
import jp.co.cyberagent.kite.core.Tuple2
import jp.co.cyberagent.kite.core.requireByType
import jp.co.cyberagent.kite.core.state
import jp.co.cyberagent.kite.core.subscribe
import jp.co.cyberagent.kite.runtime.kiteDsl
import jp.co.cyberagent.kite.sample.R
import jp.co.cyberagent.kite.sample.databinding.FragmentCounterBinding
import jp.co.cyberagent.kite.sample.databinding.IncludeCounterBinding

class CounterFragment : Fragment(R.layout.fragment_counter) {

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) = kiteDsl {
    val (counter1, counter2) = createCounterState()

    bindCounterExampleFragmentUi(counter1, counter2)
  }
}

fun KiteDslScope.createCounterState(): Tuple2<KiteMutableState<Int>, KiteMutableState<Int>> {
  return state { 0 } to state { 0 }
}

fun KiteDslScope.bindCounterExampleFragmentUi(
  counter1: KiteMutableState<Int>,
  counter2: KiteMutableState<Int>
) {
  val binding = FragmentCounterBinding.bind(
    kiteContext.requireByType<Fragment>().requireView()
  )

  subscribe {
    binding.textSum.text = "Sum: ${counter1.value + counter2.value}"
  }

  bindCounter(binding.counter1, counter1)
  bindCounter(binding.counter2, counter2)
}

fun KiteDslScope.bindCounter(
  binding: IncludeCounterBinding,
  countState: KiteMutableState<Int>
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
