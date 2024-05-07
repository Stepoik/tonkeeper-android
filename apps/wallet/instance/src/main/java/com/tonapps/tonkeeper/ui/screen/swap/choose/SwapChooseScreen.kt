package com.tonapps.tonkeeper.ui.screen.swap.choose

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tonapps.tonkeeper.ui.screen.swap.choose.list.ChooseAdapter
import com.tonapps.tonkeeper.ui.screen.swap.choose.list.ChooseItemDecoration
import com.tonapps.tonkeeper.ui.screen.swap.choose.list.ChooseSwapArgs
import com.tonapps.tonkeeper.ui.screen.swap.choose.models.SuggestedItemVO
import com.tonapps.tonkeeper.ui.component.choosebutton.ChooseButtonView
import com.tonapps.tonkeeperx.R
import com.tonapps.uikit.list.BaseListItem
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import uikit.base.BaseFragment
import uikit.extensions.applyNavBottomPadding
import uikit.extensions.collectFlow
import uikit.widget.RowLayout
import uikit.widget.SearchInput

class SwapChooseScreen : BaseFragment(R.layout.fragment_swap_choose_screen),
    BaseFragment.BottomSheet {

    private val args: ChooseSwapArgs by lazy { ChooseSwapArgs(requireArguments()) }
    private val viewModel: SwapChooseViewModel by viewModel { parametersOf(args.chooseType) }

    private lateinit var tokenRecyclerView: RecyclerView
    private lateinit var closeIconButton: View
    private lateinit var searchInput: SearchInput
    private lateinit var suggestedRow: RowLayout
    private lateinit var suggestedText: View
    private lateinit var rootView: View
    private lateinit var closeButton: View

    private val adapter = ChooseAdapter { contractAddress ->
        viewModel.onSelect(contractAddress)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootView = view.findViewById(R.id.swap_choose_screen)
        rootView.applyNavBottomPadding()

        tokenRecyclerView = view.findViewById(R.id.tokens)
        tokenRecyclerView.adapter = adapter
        tokenRecyclerView.isNestedScrollingEnabled = true
        tokenRecyclerView.addItemDecoration(ChooseItemDecoration())

        closeIconButton = view.findViewById(R.id.action_close)
        closeIconButton.setOnClickListener { onClose() }

        searchInput = view.findViewById(R.id.search_input)
        searchInput.doOnTextChanged = { query ->
            query?.let {
                viewModel.search(it.toString())
            }
        }

        closeButton = view.findViewById(R.id.close_button)
        closeButton.setOnClickListener { onClose() }

        suggestedRow = view.findViewById(R.id.suggested_row)
        suggestedText = view.findViewById(R.id.suggested_text)

        collectFlow(viewModel.tokens, ::loadTokens)
        collectFlow(viewModel.suggestedTokens, ::setSuggestedTokens)
        collectFlow(viewModel.close, ::onClose)
    }

    private fun loadTokens(tokens: List<BaseListItem>) {
        adapter.submitList(tokens)
    }

    private fun onClose(close: Boolean = true) {
        if (close) {
            finish()
        }
    }

    private fun setSuggestedTokens(tokens: List<SuggestedItemVO>) {
        if (tokens.isEmpty()) {
            suggestedRow.visibility = View.GONE
            suggestedText.visibility = View.GONE
            return
        }
        suggestedRow.visibility = View.VISIBLE
        suggestedText.visibility = View.VISIBLE
        for (token in tokens) {
            val chooseButton = ChooseButtonView(requireContext())
            chooseButton.setOnClickListener { viewModel.onSelect(token.token) }
            chooseButton.setParameters(token.toChooseButtonParameters())
            suggestedRow.addView(chooseButton)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(chooseType: Int): SwapChooseScreen {
            val fragment = SwapChooseScreen()
            fragment.arguments = ChooseSwapArgs(chooseType = chooseType).toBundle()
            return fragment
        }
    }
}