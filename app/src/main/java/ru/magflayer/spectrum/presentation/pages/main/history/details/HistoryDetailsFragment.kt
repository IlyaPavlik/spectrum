package ru.magflayer.spectrum.presentation.pages.main.history.details

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ActivityComponent
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.magflayer.spectrum.R
import ru.magflayer.spectrum.databinding.FragmentHistoryDetailsBinding
import ru.magflayer.spectrum.databinding.ItemHistoryColorBinding
import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity
import ru.magflayer.spectrum.presentation.common.android.BaseFragment
import ru.magflayer.spectrum.presentation.common.android.BaseRecyclerAdapter
import ru.magflayer.spectrum.presentation.common.android.BaseViewHolder
import ru.magflayer.spectrum.presentation.common.helper.ColorHelper

class HistoryDetailsFragment : BaseFragment(R.layout.fragment_history_details), HistoryDetailsView {

    companion object {

        private const val PHOTO_PATH_KEY = "PHOTO_PATH_KEY"

        fun newInstance(filePath: String): HistoryDetailsFragment {
            val args = Bundle()
            args.putString(PHOTO_PATH_KEY, filePath)

            val fragment = HistoryDetailsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val viewBinding by viewBinding(FragmentHistoryDetailsBinding::bind)

    @InjectPresenter
    lateinit var presenter: HistoryDetailsPresenter

    private lateinit var colorAdapter: ColorAdapter

    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface HistoryDetailsEntryPoint {
        fun historyDetailsPresenter(): HistoryDetailsPresenter
    }

    @ProvidePresenter
    fun providePresenter(): HistoryDetailsPresenter {
        return EntryPointAccessors.fromActivity(
            requireActivity(),
            HistoryDetailsEntryPoint::class.java
        ).historyDetailsPresenter().apply {
            val safeArgs: HistoryDetailsFragmentArgs by navArgs()
            filePath = safeArgs.filePath
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        colorAdapter = ColorAdapter(requireContext())
        colorAdapter.itemSelectListener = object : BaseRecyclerAdapter.OnItemSelectListener {
            override fun onItemSelect(position: Int) {
                colorAdapter.getItem(position)?.let { presenter.handleSelectedColor(it) }
            }
        }

        viewBinding.colorsRecycler.apply {
            adapter = colorAdapter
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }
    }

    override fun showPhoto(entity: ColorPhotoEntity) {
        Glide.with(this)
            .load(entity.filePath)
            .apply(RequestOptions.fitCenterTransform())
            .into(viewBinding.picture)

        colorAdapter.setData(entity.rgbColors)
        colorAdapter.select(0)
    }

    override fun showColorName(name: String) {
        viewBinding.title.text = name
    }

    override fun showRgb(color: Int) {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        val rgbMaxValue = 255

        viewBinding.red.apply {
            setColor(ContextCompat.getColor(requireContext(), R.color.red))
            setValue(red)
            setMaxValue(rgbMaxValue)
            setText(red.toString())
        }

        viewBinding.green.apply {
            setColor(ContextCompat.getColor(requireContext(), R.color.green))
            setValue(green)
            setMaxValue(rgbMaxValue)
            setText(green.toString())
        }

        viewBinding.blue.apply {
            setColor(ContextCompat.getColor(requireContext(), R.color.blue))
            setValue(blue)
            setMaxValue(rgbMaxValue)
            setText(blue.toString())
        }
    }

    override fun showRyb(color: Int) {
        val ryb = ColorHelper.dec2Ryb(color)

        viewBinding.colorInfoRyb.apply {
            setTitle(getString(R.string.history_details_ryb))
            setParams(
                listOf(
                    getString(R.string.ryb_r_format, ryb[0]),
                    getString(R.string.ryb_y_format, ryb[1]),
                    getString(R.string.ryb_b_format, ryb[2])
                )
            )
        }
    }

    @SuppressLint("DefaultLocale")
    override fun showCmyk(color: Int) {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)

        val cmyk = ColorHelper.rgb2Cmyk(red, green, blue)
        val cmykMaxValue = 100

        viewBinding.cyan.apply {
            setColor(ContextCompat.getColor(requireContext(), R.color.cyan))
            setValue((cmyk[0] * 100).toInt())
            setMaxValue(cmykMaxValue)
            setText(String.format("%.2f", cmyk[0]))
        }

        viewBinding.magenta.apply {
            setColor(ContextCompat.getColor(requireContext(), R.color.magenta))
            setValue((cmyk[1] * 100).toInt())
            setMaxValue(cmykMaxValue)
            setText(String.format("%.2f", cmyk[1]))
        }

        viewBinding.yellow.apply {
            setColor(ContextCompat.getColor(requireContext(), R.color.yellow))
            setValue((cmyk[2] * 100).toInt())
            setMaxValue(cmykMaxValue)
            setText(String.format("%.2f", cmyk[2]))
        }

        viewBinding.key.apply {
            setColor(ContextCompat.getColor(requireContext(), R.color.key))
            setValue((cmyk[3] * 100).toInt())
            setMaxValue(cmykMaxValue)
            setText(String.format("%.2f", cmyk[3]))
        }
    }

    override fun showHsv(color: Int) {
        val hsv = FloatArray(3)

        Color.colorToHSV(color, hsv)

        val hue = hsv[0].toInt()
        val saturation = (hsv[1] * 100).toInt()
        val value = (hsv[2] * 100).toInt()

        viewBinding.colorInfoHsv.apply {
            setTitle(getString(R.string.history_details_hsv))
            setParams(
                listOf(
                    getString(R.string.hue_long_format, hue),
                    getString(R.string.saturation_long_format, saturation),
                    getString(R.string.value_long_format, value)
                )
            )
        }
    }

    override fun showXyz(color: Int) {
        val xyz = ColorHelper.dec2Xyz(color)

        viewBinding.colorInfoXyz.apply {
            setTitle(getString(R.string.history_details_xyz))
            setParams(
                listOf(
                    getString(R.string.x_format, xyz[0]),
                    getString(R.string.y_format, xyz[1]),
                    getString(R.string.z_format, xyz[2])
                )
            )
        }
    }

    override fun showLab(color: Int) {
        val lab = ColorHelper.dec2Lab(color)

        viewBinding.colorInfoLab.apply {
            setTitle(getString(R.string.history_details_lab))
            setParams(
                listOf(
                    getString(R.string.l_format, lab[0]),
                    getString(R.string.a_format, lab[1]),
                    getString(R.string.b_format, lab[2])
                )
            )
        }
    }

    override fun showNcs(color: Int, ncsName: String) {
        viewBinding.colorInfoNcs.apply {
            setTitle(getString(R.string.history_details_ncs))
            setParams(listOf(ncsName))
        }
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

            private val viewBinding by viewBinding(ItemHistoryColorBinding::bind)

            val colorView: ImageView
                get() = viewBinding.color
        }
    }
}
