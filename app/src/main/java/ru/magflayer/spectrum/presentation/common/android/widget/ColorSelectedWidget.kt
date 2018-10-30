package ru.magflayer.spectrum.presentation.common.android.widget

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.support.v7.graphics.Palette
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import ru.magflayer.spectrum.R
import ru.magflayer.spectrum.presentation.common.android.BaseRecyclerAdapter
import ru.magflayer.spectrum.presentation.common.android.BaseViewHolder
import ru.magflayer.spectrum.presentation.common.helper.ColorHelper
import ru.magflayer.spectrum.presentation.common.helper.DialogHelper

class ColorSelectedWidget @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    @BindView(R.id.color_recycler)
    lateinit var colorRecyclerView: RecyclerView
    @BindView(R.id.color_hex)
    lateinit var hexView: TextView

    @BindView(R.id.red)
    lateinit var redView: TextView
    @BindView(R.id.green)
    lateinit var greenView: TextView
    @BindView(R.id.blue)
    lateinit var blueView: TextView

    @BindView(R.id.hue)
    lateinit var hueView: TextView
    @BindView(R.id.saturation)
    lateinit var saturationView: TextView
    @BindView(R.id.value)
    lateinit var valueView: TextView

    @BindView(R.id.cyan)
    lateinit var cyanView: TextView
    @BindView(R.id.magenta)
    lateinit var magentaView: TextView
    @BindView(R.id.yellow)
    lateinit var yellowView: TextView
    @BindView(R.id.key)
    lateinit var keyView: TextView

    private var colorAdapter = ColorAdapter(context)

    init {
        val view = View.inflate(context, R.layout.widget_history_details, this)
        ButterKnife.bind(this, view)

        colorRecyclerView.layoutManager = LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)

        colorAdapter = ColorAdapter(getContext())
        colorAdapter.itemSelectListener = object : BaseRecyclerAdapter.OnItemSelectListener {
            override fun onItemSelect(position: Int) {
                val swatch = colorAdapter.getItem(position)
                if (swatch != null) {
                    selectColor(swatch.rgb)
                }
            }
        }
        colorRecyclerView.adapter = colorAdapter
    }

    fun showDialog(swatches: List<Palette.Swatch>) {
        colorAdapter.setData(swatches)
        colorAdapter.select(0)

        val title = context.getString(R.string.history_dialog_title)
        DialogHelper.buildViewDialog(context, title, this, DialogInterface.OnClickListener { dialogInterface, _ -> dialogInterface.dismiss() })
                .show()
    }

    @OnClick(R.id.color_hex_container)
    fun onCopyClick() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val label = context.getString(R.string.history_dialog_copy_label)
        val clip = ClipData.newPlainText(label, hexView.text)
        clipboard.primaryClip = clip

        Toast.makeText(context, R.string.history_dialog_copy, Toast.LENGTH_SHORT).show()
    }

    private fun selectColor(color: Int) {
        val context = context
        hexView.text = ColorHelper.dec2Hex(color)

        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)

        redView.text = context.getString(R.string.red_format, red)
        greenView.text = context.getString(R.string.green_format, green)
        blueView.text = context.getString(R.string.blue_format, blue)

        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        val hue = hsv[0].toInt()
        val saturation = (hsv[1] * 100).toInt()
        val value = (hsv[2] * 100).toInt()

        hueView.text = context.getString(R.string.hue_format, hue)
        saturationView.text = context.getString(R.string.saturation_format, saturation)
        valueView.text = context.getString(R.string.value_format, value)

        val cmyk = ColorHelper.rgb2Cmyk(red, green, blue)
        val c = (cmyk[0] * 100).toInt()
        val m = (cmyk[1] * 100).toInt()
        val y = (cmyk[2] * 100).toInt()
        val k = (cmyk[3] * 100).toInt()

        cyanView.text = context.getString(R.string.cyan_format, c)
        magentaView.text = context.getString(R.string.magenta_format, m)
        yellowView.text = context.getString(R.string.yellow_format, y)
        keyView.text = context.getString(R.string.key_color_format, k)
    }

    internal class ColorAdapter(context: Context) : BaseRecyclerAdapter<ColorAdapter.ColorViewHolder, Palette.Swatch>(context) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
            return ColorViewHolder(inflater.inflate(R.layout.item_history_color, parent, false))
        }

        override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
            val swatch = getItem(position)
            if (swatch != null) {
                holder.colorView.setBackgroundColor(swatch.rgb)
            }

            if (position == selectedPosition) {
                holder.colorView.setImageResource(R.drawable.rectangle_color_selected)
            } else {
                holder.colorView.setImageBitmap(null)
            }
        }

        override fun select(position: Int) {
            super.select(position)
            notifyDataSetChanged()
        }

        internal class ColorViewHolder(itemView: View) : BaseViewHolder(itemView) {
            @BindView(R.id.color)
            lateinit var colorView: ImageView
        }
    }
}
