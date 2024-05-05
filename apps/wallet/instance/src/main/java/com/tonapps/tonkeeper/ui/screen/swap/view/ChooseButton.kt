package com.tonapps.tonkeeper.ui.screen.swap.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import com.tonapps.tonkeeperx.R
import com.tonapps.wallet.localization.Localization
import uikit.widget.FrescoView
import uikit.widget.RowLayout
import kotlin.math.roundToInt


class ChooseButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : RowLayout(context, attrs, defStyle) {
    private var chooseTextView: AppCompatTextView
    private var iconView: FrescoView
    private val emptyText = Localization.choose

    init {
        inflate(context, R.layout.view_choose_button, this)
        chooseTextView = findViewById(R.id.choose_text)
        iconView = findViewById(R.id.token_icon)
    }

    fun setParameters(parameters: ChooseButtonParameters) {
        val chooseTextViewParams = chooseTextView.layoutParams as MarginLayoutParams
        chooseTextViewParams.marginStart = 8.toPx()
        chooseTextViewParams.marginEnd = 16.toPx()
        iconView.visibility = VISIBLE
        iconView.setImageURI(parameters.iconURL)
        chooseTextView.text = parameters.text
    }

    fun clear() {
        chooseTextView.setText(emptyText)
        iconView.visibility = GONE
        val chooseTextViewParams = chooseTextView.layoutParams as MarginLayoutParams
        chooseTextViewParams.marginStart = 14.toPx()
        chooseTextViewParams.marginEnd = 14.toPx()
    }

    private fun Int.toPx(): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        resources.displayMetrics
    ).roundToInt()
}