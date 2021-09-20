package ru.magflayer.spectrum.presentation.pages.main.history

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.*
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ActivityComponent
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.magflayer.spectrum.R
import ru.magflayer.spectrum.databinding.FragmentHistoryBinding
import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity
import ru.magflayer.spectrum.presentation.common.android.BaseFragment
import ru.magflayer.spectrum.presentation.common.android.BaseRecyclerAdapter
import ru.magflayer.spectrum.presentation.common.android.helper.SwipeToDeleteCallback
import ru.magflayer.spectrum.presentation.common.helper.DialogHelper

class HistoryFragment : BaseFragment(R.layout.fragment_history), HistoryView {

    companion object {

        private const val REQUEST_PICK_IMAGE = 1
        private const val REQUEST_WRITE_EXTERNAL_STORAGE = 2

        fun newInstance(): HistoryFragment {
            return HistoryFragment()
        }
    }

    private val viewBinding by viewBinding(FragmentHistoryBinding::bind)

    @InjectPresenter
    lateinit var presenter: HistoryPresenter

    private lateinit var historyAdapter: HistoryAdapter

    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface HistoryEntryPoint {
        fun historyPresenter(): HistoryPresenter
    }

    @ProvidePresenter
    fun providePresenter(): HistoryPresenter {
        return EntryPointAccessors.fromActivity(
            requireActivity(),
            HistoryEntryPoint::class.java
        ).historyPresenter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        historyAdapter = HistoryAdapter(requireContext())

        viewBinding.historyRecycler.apply {
            this.layoutManager = LinearLayoutManager(context)
            this.adapter = historyAdapter
            this.addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        val callback = object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                openAcceptDeleteColor(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(viewBinding.historyRecycler)

        historyAdapter.itemSelectListener = object : BaseRecyclerAdapter.OnItemSelectListener {
            override fun onItemSelect(position: Int) {
                historyAdapter.getItem(position)?.let { presenter.handleColorSelected(it) }
            }
        }

        historyAdapter.itemLongClickListener =
            object : BaseRecyclerAdapter.OnItemLongClickListener {
                override fun onItemLongClick(position: Int) {
                    openAcceptDeleteColor(position)
                }
            }
    }

    override fun showHistory(history: List<ColorPhotoEntity>) {
        val diffResult = DiffUtil.calculateDiff(HistoryDiffCallback(historyAdapter.data, history))

        historyAdapter.data.clear()
        historyAdapter.data.addAll(history)
        diffResult.dispatchUpdatesTo(historyAdapter)

        viewBinding.empty.visibility =
            if (historyAdapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    override fun openPickPhoto() {
        logger.debug("openPickPhoto")
        if (hasStoragePermission()) {
            requestPermission()
            return
        }

        val title = getString(R.string.select_image_title)
        val getContentIntent = Intent(Intent.ACTION_GET_CONTENT)
        getContentIntent.type = "image/*"

        val pickIntent = Intent(Intent.ACTION_PICK)
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")

        val chooserIntent = Intent.createChooser(getContentIntent, title)
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

        startActivityForResult(chooserIntent, REQUEST_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (context == null) {
            logger.warn("Context is null")
            return
        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_PICK_IMAGE) {
                val dataUri = data?.data
                try {
                    dataUri?.let { uri ->
                        logger.debug("Try to build bitmap from: {}", uri)
                        val inputStream = requireContext().contentResolver.openInputStream(uri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        presenter.handleSelectedImage(uri, bitmap)
                    } ?: logger.warn("Data uri is null")
                } catch (e: Exception) {
                    logger.warn("Cannot load bitmap: ", e)
                }
            }
        } else {
            logger.warn("Result doesn't success: {}", resultCode)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openPickPhoto()
                }
            }
        }
    }

    private fun openAcceptDeleteColor(position: Int) {
        val title = getString(R.string.history_remove_title)
        val message = getString(R.string.history_remove_description)

        context?.let { it ->
            val dialog = DialogHelper.buildYesNoDialog(
                it,
                title,
                message
            ) { _, _ ->
                historyAdapter.getItem(position)?.let { presenter.removeColor(it) }
                viewBinding.empty.visibility =
                    if (historyAdapter.itemCount == 0) View.VISIBLE else View.GONE
            }
            dialog.setOnCancelListener { historyAdapter.notifyItemChanged(position) }
            dialog.show()
        }
    }

    private fun hasStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        requestPermissions(
            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_WRITE_EXTERNAL_STORAGE
        )
    }
}
