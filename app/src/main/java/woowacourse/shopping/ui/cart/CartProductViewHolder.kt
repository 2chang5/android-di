package woowacourse.shopping.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartProductBinding
import woowacourse.shopping.model.CartProduct

class CartProductViewHolder(
    private val binding: ItemCartProductBinding,
    private val dateFormatter: DateFormatter,
    onClickDelete: (position: Int) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.ivCartProductDelete.setOnClickListener {
            val position = adapterPosition
            onClickDelete(position)
        }
    }

    fun bind(cartProduct: CartProduct) {
        binding.item = cartProduct
        binding.tvCartProductCreatedAt.text = dateFormatter.formatDate(cartProduct.createdAt)
    }

    companion object {
        fun from(
            parent: ViewGroup,
            dateFormatter: DateFormatter,
            onClickDelete: (position: Int) -> Unit,
        ): CartProductViewHolder {
            val binding = ItemCartProductBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return CartProductViewHolder(binding, dateFormatter, onClickDelete)
        }
    }
}
