package jp.co.cyberagent.kite.sample

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import jp.co.cyberagent.kite.sample.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val binding = FragmentHomeBinding.bind(view)
    binding.buttonCounterExample.setOnClickListener {
      findNavController().navigate(MainGraph.Action.toCounterExample)
    }
    binding.buttonTimelineExample.setOnClickListener {
      findNavController().navigate(MainGraph.Action.toTimelineExample)
    }
  }
}
