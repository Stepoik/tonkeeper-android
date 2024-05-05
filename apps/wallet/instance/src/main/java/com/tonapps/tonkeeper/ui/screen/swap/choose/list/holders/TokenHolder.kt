package com.tonapps.tonkeeper.ui.screen.swap.choose.list.holders

import android.view.ViewGroup
import android.widget.TextView
import com.tonapps.tonkeeper.ui.screen.swap.choose.list.ChooseItem
import com.tonapps.tonkeeperx.R
import com.tonapps.uikit.list.BaseListHolder
import org.w3c.dom.Text
import uikit.widget.FrescoView

class TokenHolder(
    parent: ViewGroup,
    private val onClick: (String) -> Unit
) : BaseListHolder<ChooseItem.Token>(parent, R.layout.view_choose_token) {
    private val imageView = itemView.findViewById<FrescoView>(R.id.token_icon)
    private val symbolView = itemView.findViewById<TextView>(R.id.token_symbol)
    private val displayName = itemView.findViewById<TextView>(R.id.token_display_name)
    private val count = itemView.findViewById<TextView>(R.id.count)

    override fun onBind(item: ChooseItem.Token) {
        itemView.setOnClickListener { onClick(item.contractAddress) }
        imageView.setImageURI(item.iconURL)
        symbolView.text = item.symbol
        displayName.text = item.displayName
        count.text = item.balance
    }
}