package ru.magflayer.spectrum.presentation.common.android.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TableRow
import androidx.cardview.widget.CardView
import ru.magflayer.spectrum.databinding.WidgetColorInfoBinding
import ru.magflayer.spectrum.databinding.WidgetColorInfoItemBinding

class ColorInfoWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1,
) : CardView(context, attrs, defStyleAttr) {

    companion object {
        private const val MAX_COLUMNS = 3
    }

    private val viewBinding = WidgetColorInfoBinding.inflate(
        LayoutInflater.from(context),
        this,
        true,
    )

    fun setTitle(title: CharSequence) {
        viewBinding.colorInfoTitle.text = title
    }

    fun setParams(params: List<String>) = with(viewBinding) {
        colorInfoContainer.removeAllViews()

        var tableRow: TableRow? = null
        for (param in params) {
            if (tableRow == null || tableRow.childCount % MAX_COLUMNS == 0) {
                tableRow = TableRow(context)
                colorInfoContainer.addView(tableRow)
            }
            val itemViewBinding = WidgetColorInfoItemBinding.inflate(
                LayoutInflater.from(context),
                tableRow,
                false,
            )
            itemViewBinding.textView.text = param
            tableRow.addView(itemViewBinding.root)
        }
    }
}
