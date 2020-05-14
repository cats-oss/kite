package jp.co.cyberagent.kite.sample.counter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import jp.co.cyberagent.kite.KiteDslScope
import jp.co.cyberagent.kite.KiteProperty
import jp.co.cyberagent.kite.kiteDsl
import jp.co.cyberagent.kite.sample.R
import jp.co.cyberagent.kite.sample.databinding.FragmentCounterExampleBinding
import jp.co.cyberagent.kite.sample.databinding.IncludeCounterBinding
import jp.co.cyberagent.kite.state
import jp.co.cyberagent.kite.subscribe

@SuppressLint("SetTextI18n")
class CounterExampleFragment : Fragment(R.layout.fragment_counter_example) {

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) = kiteDsl {
    val binding = FragmentCounterExampleBinding.bind(requireView())

    val count1 = state(0)
    val count2 = state(0)

    subscribe {
      binding.textSum.text = "Sum: ${count1.value + count2.value}"
    }

    bindCounter(binding.counter1, count1)
    bindCounter(binding.counter2, count2)
  }
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
