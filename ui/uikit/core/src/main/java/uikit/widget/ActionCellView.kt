package uikit.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.setPadding
import uikit.R
import uikit.extensions.dp
import uikit.extensions.getDimensionPixelSize
import uikit.extensions.useAttributes

class ActionCellView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : RowLayout(context, attrs, defStyle) {

    private val iconView: AppCompatImageView
    private val titleView: AppCompatTextView
    private val subtitleView: AppCompatTextView
    private val chevronView: View
    private val actionView: AppCompatImageView

    var title: CharSequence?
        get() = titleView.text
        set(value) {
            titleView.text = value
        }

    var subtitle: CharSequence?
        get() = subtitleView.text
        set(value) {
            subtitleView.text = value
        }

    var iconRes: Int = 0
        set(value) {
            field = value
            if (value == 0) {
                iconView.visibility = GONE
            } else {
                iconView.visibility = VISIBLE
                iconView.setImageResource(value)
            }
        }

    var actionRes: Int = 0
        set(value) {
            field = value
            if (value == 0) {
                actionView.visibility = GONE
                chevronView.visibility = VISIBLE
            } else {
                actionView.visibility = VISIBLE
                actionView.setImageResource(value)
                chevronView.visibility = GONE
            }
        }

    init {
        setPadding(context.getDimensionPixelSize(R.dimen.offsetMedium))
        minimumHeight = 76.dp
        setBackgroundResource(R.drawable.bg_content)
        inflate(context, R.layout.view_action_cell, this)

        iconView = findViewById(R.id.action_cell_icon)
        titleView = findViewById(R.id.action_cell_title)
        subtitleView = findViewById(R.id.action_cell_subtitle)
        chevronView = findViewById(R.id.action_cell_chevron)
        actionView = findViewById(R.id.action_cell_right)

        context.useAttributes(attrs, R.styleable.ActionCellView) {
            iconRes = it.getResourceId(R.styleable.HeaderView_android_icon, 0)
            title = it.getString(R.styleable.ActionCellView_android_title)
            subtitle = it.getString(R.styleable.ActionCellView_android_subtitle)
            actionRes = it.getResourceId(R.styleable.HeaderView_android_action, 0)

            val singleLine = it.getBoolean(R.styleable.ActionCellView_android_singleLine, false)
            if (singleLine) {
                titleView.setSingleLine()
                subtitleView.setSingleLine()
            }
        }
    }
}