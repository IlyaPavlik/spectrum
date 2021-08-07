package ru.magflayer.spectrum.presentation.pages.main.history.details

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.magflayer.spectrum.R
import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity
import ru.magflayer.spectrum.domain.injection.InjectorManager
import ru.magflayer.spectrum.presentation.common.android.BaseFragment
import ru.magflayer.spectrum.presentation.common.android.BaseRecyclerAdapter
import ru.magflayer.spectrum.presentation.common.android.BaseViewHolder
import ru.magflayer.spectrum.presentation.common.android.layout.Layout
import ru.magflayer.spectrum.presentation.common.android.widget.ColorInfoWidget
import ru.magflayer.spectrum.presentation.common.android.widget.TextSeekBarView
import ru.magflayer.spectrum.presentation.common.helper.ColorHelper
import java.util.*

@Layout(R.layout.fragment_history_details)
class HistoryDetailsFragment : BaseFragment(), HistoryDetailsView {

    @InjectPresenter
    lateinit var presenter: HistoryDetailsPresenter

    @BindView(R.id.picture)
    lateinit var pictureView: ImageView

    @BindView(R.id.colors)
    lateinit var colorsRecycler: RecyclerView

    @BindView(R.id.title)
    lateinit var titleView: TextView

    @BindView(R.id.red)
    lateinit var redView: TextSeekBarView

    @BindView(R.id.green)
    lateinit var greenView: TextSeekBarView

    @BindView(R.id.blue)
    lateinit var blueView: TextSeekBarView

    @BindView(R.id.cyan)
    lateinit var cyanView: TextSeekBarView

    @BindView(R.id.magenta)
    lateinit var magentaView: TextSeekBarView

    @BindView(R.id.yellow)
    lateinit var yellowView: TextSeekBarView

    @BindView(R.id.key)
    lateinit var keyView: TextSeekBarView

    @BindView(R.id.color_info_ryb)
    lateinit var rybInfo: ColorInfoWidget

    @BindView(R.id.color_info_hsv)
    lateinit var hsvInfo: ColorInfoWidget

    @BindView(R.id.color_info_xyz)
    lateinit var xyzInfo: ColorInfoWidget

    @BindView(R.id.color_info_lab)
    lateinit var labInfo: ColorInfoWidget

    @BindView(R.id.color_info_ncs)
    lateinit var ncsInfo: ColorInfoWidget

    private lateinit var adapter: ColorAdapter

    @ProvidePresenter
    fun providePresenter(): HistoryDetailsPresenter {
        val arguments = arguments
        return HistoryDetailsPresenter(arguments!!.getString(PHOTO_PATH_KEY)!!)
    }

    override fun inject() {
        InjectorManager.appComponent?.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ColorAdapter(requireContext())
        adapter.itemSelectListener = object : BaseRecyclerAdapter.OnItemSelectListener {
            override fun onItemSelect(position: Int) {
                adapter.getItem(position)?.let { presenter.handleSelectedColor(it) }
            }
        }
        colorsRecycler.adapter = adapter

        val manager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL, false
        )
        colorsRecycler.layoutManager = manager
    }

    override fun showPhoto(entity: ColorPhotoEntity) {
        Glide.with(this)
            .load(entity.filePath)
            .apply(RequestOptions.fitCenterTransform())
            .into(pictureView)

        adapter.setData(entity.rgbColors)
        adapter.select(0)
    }

    override fun showColorName(name: String) {
        titleView.text = name
    }

    override fun showRgb(color: Int) {
        val context = context
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)

        val rgbMaxValue = 255


        if (context != null) redView.setColor(ContextCompat.getColor(context, R.color.red))
        redView.setValue(red)
        redView.setMaxValue(rgbMaxValue)
        redView.setText(red.toString())

        if (context != null) greenView.setColor(ContextCompat.getColor(context, R.color.green))
        greenView.setValue(green)
        greenView.setMaxValue(rgbMaxValue)
        greenView.setText(green.toString())

        if (context != null) blueView.setColor(ContextCompat.getColor(context, R.color.blue))
        blueView.setValue(blue)
        blueView.setMaxValue(rgbMaxValue)
        blueView.setText(blue.toString())
    }

    override fun showRyb(color: Int) {
        val ryb = ColorHelper.dec2Ryb(color)

        rybInfo.setTitle(getString(R.string.history_details_ryb))
        rybInfo.setParams(
            Arrays.asList(
                getString(R.string.ryb_r_format, ryb[0]),
                getString(R.string.ryb_y_format, ryb[1]),
                getString(R.string.ryb_b_format, ryb[2])
            )
        )
    }

    @SuppressLint("DefaultLocale")
    override fun showCmyk(color: Int) {
        val context = context
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)

        val cmyk = ColorHelper.rgb2Cmyk(red, green, blue)
        val cmykMaxValue = 100

        context?.let {
            cyanView.setColor(ContextCompat.getColor(it, R.color.cyan))
        }
        cyanView.setValue((cmyk[0] * 100).toInt())
        cyanView.setMaxValue(cmykMaxValue)
        cyanView.setText(String.format("%.2f", cmyk[0]))

        context?.let {
            magentaView.setColor(ContextCompat.getColor(it, R.color.magenta))
        }
        magentaView.setValue((cmyk[1] * 100).toInt())
        magentaView.setMaxValue(cmykMaxValue)
        magentaView.setText(String.format("%.2f", cmyk[1]))

        context?.let {
            yellowView.setColor(ContextCompat.getColor(it, R.color.yellow))
        }
        yellowView.setValue((cmyk[2] * 100).toInt())
        yellowView.setMaxValue(cmykMaxValue)
        yellowView.setText(String.format("%.2f", cmyk[2]))

        context?.let {
            keyView.setColor(ContextCompat.getColor(it, R.color.key))
        }
        keyView.setValue((cmyk[3] * 100).toInt())
        keyView.setMaxValue(cmykMaxValue)
        keyView.setText(String.format("%.2f", cmyk[3]))
    }

    override fun showHsv(color: Int) {
        val hsv = FloatArray(3)

        Color.colorToHSV(color, hsv)

        val hue = hsv[0].toInt()
        val saturation = (hsv[1] * 100).toInt()
        val value = (hsv[2] * 100).toInt()

        hsvInfo.setTitle(getString(R.string.history_details_hsv))
        hsvInfo.setParams(
            Arrays.asList(
                getString(R.string.hue_long_format, hue),
                getString(R.string.saturation_long_format, saturation),
                getString(R.string.value_long_format, value)
            )
        )
    }

    override fun showXyz(color: Int) {
        val xyz = ColorHelper.dec2Xyz(color)

        xyzInfo.setTitle(getString(R.string.history_details_xyz))
        xyzInfo.setParams(
            Arrays.asList(
                getString(R.string.x_format, xyz[0]),
                getString(R.string.y_format, xyz[1]),
                getString(R.string.z_format, xyz[2])
            )
        )
    }

    override fun showLab(color: Int) {
        val lab = ColorHelper.dec2Lab(color)

        labInfo.setTitle(getString(R.string.history_details_lab))
        labInfo.setParams(
            Arrays.asList(
                getString(R.string.l_format, lab[0]),
                getString(R.string.a_format, lab[1]),
                getString(R.string.b_format, lab[2])
            )
        )
    }

    override fun showNcs(color: Int, ncsName: String) {
        ncsInfo.setTitle(getString(R.string.history_details_ncs))
        ncsInfo.setParams(listOf(ncsName))
    }

    internal class ColorAdapter(context: Context) :
        BaseRecyclerAdapter<ColorAdapter.ColorViewHolder, Int>(context) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
            return ColorViewHolder(inflater.inflate(R.layout.item_history_color, parent, false))
        }

        override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {

            getItem(position)?.let { holder.colorView.setBackgroundColor(it) }

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

    companion object {

        private val PHOTO_PATH_KEY = "PHOTO_PATH_KEY"

        fun newInstance(filePath: String): HistoryDetailsFragment {

            val args = Bundle()
            args.putString(PHOTO_PATH_KEY, filePath)

            val fragment = HistoryDetailsFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
