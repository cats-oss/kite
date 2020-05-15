package jp.co.cyberagent.kite.sample.timeline.epoxymodel

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import androidx.core.widget.ImageViewCompat
import com.airbnb.epoxy.EpoxyModel
import jp.co.cyberagent.kite.Invoker2
import jp.co.cyberagent.kite.sample.R
import jp.co.cyberagent.kite.sample.databinding.ModelItemBinding

data class ContentModel(
  val id: String,
  val content: String,
  val isFavorite: Boolean,
  val onClick: Invoker2<String, Boolean>
) : EpoxyModel<View>() {

  override fun bind(view: View) {
    val binding = ModelItemBinding.bind(view)
    binding.textContent.text = content
    val (src, color) = if (isFavorite) {
      R.drawable.ic_favorite to ColorStateList.valueOf(Color.RED)
    } else {
      R.drawable.ic_favorite_border to ColorStateList.valueOf(Color.GRAY)
    }
    binding.imageFavorite.setImageResource(src)
    ImageViewCompat.setImageTintList(binding.imageFavorite, color)
    binding.root.setOnClickListener {
      onClick.invoke(id, !isFavorite)
    }
  }

  override fun getDefaultLayout(): Int = R.layout.model_item
}
