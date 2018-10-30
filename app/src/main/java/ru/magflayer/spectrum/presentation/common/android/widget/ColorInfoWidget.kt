package ru.magflayer.spectrum.presentation.common.android.widget

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView

import butterknife.BindView
import butterknife.ButterKnife
import ru.magflayer.spectrum.R

class ColorInfoWidget @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = -1) : CardView(context, attrs, defStyleAttr) {

    companion object {
        private const val MAX_COLUMNS = 3
    }

    @BindView(R.id.color_info_title)
    lateinit var titleView: TextView
    @BindView(R.id.color_info_container)
    lateinit var paramContainer: ViewGroup

    private var inflater = LayoutInflater.from(context)

    init {
        val view = View.inflate(context, R.layout.widget_color_info, this)
        ButterKnife.bind(this, view)
    }

    fun setTitle(title: CharSequence) {
        titleView.text = title
    }

    fun setParams(params: List<String>) {
        paramContainer.removeAllViews()

        var tableRow: TableRow? = null
        for (param in params) {
            if (tableRow == null || tableRow.childCount % MAX_COLUMNS == 0) {
                tableRow = TableRow(context)
                paramContainer.addView(tableRow)
            }
            val paramTextView = inflater?.inflate(R.layout.widget_color_info_item, tableRow, false) as TextView
            paramTextView.text = param
            tableRow.addView(paramTextView)
        }
    }

}
