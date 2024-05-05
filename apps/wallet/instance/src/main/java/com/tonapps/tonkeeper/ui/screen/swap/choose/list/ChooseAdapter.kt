package com.tonapps.tonkeeper.ui.screen.swap.choose.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.tonapps.tonkeeper.ui.screen.swap.choose.list.holders.SkeletonHolder
import com.tonapps.tonkeeper.ui.screen.swap.choose.list.holders.TokenHolder
import com.tonapps.uikit.list.BaseListAdapter
import com.tonapps.uikit.list.BaseListHolder
import com.tonapps.uikit.list.BaseListItem

class ChooseAdapter(
    private val onClick: (String) -> Unit
): BaseListAdapter() {
    override fun createHolder(parent: ViewGroup, viewType: Int): BaseListHolder<out BaseListItem> {
        return when(viewType) {
            ChooseItem.SKELETON -> SkeletonHolder(parent)
            ChooseItem.TOKEN -> TokenHolder(parent, onClick)
            else -> throw IllegalArgumentException("Unknown viewType: $viewType")
        }
    }
}