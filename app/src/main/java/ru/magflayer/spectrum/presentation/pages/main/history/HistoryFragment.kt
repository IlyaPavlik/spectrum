package ru.magflayer.spectrum.presentation.pages.main.history

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.*
import butterknife.BindView
import moxy.presenter.InjectPresenter
import ru.magflayer.spectrum.R
import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity
import ru.magflayer.spectrum.domain.injection.InjectorManager
import ru.magflayer.spectrum.presentation.common.android.BaseFragment
import ru.magflayer.spectrum.presentation.common.android.BaseRecyclerAdapter
import ru.magflayer.spectrum.presentation.common.android.helper.SwipeToDeleteCallback
import ru.magflayer.spectrum.presentation.common.android.layout.Layout
import ru.magflayer.spectrum.presentation.common.helper.AppHelper
import ru.magflayer.spectrum.presentation.common.helper.DialogHelper

@Layout(R.layout.fragment_history)
class HistoryFragment : BaseFragment(), HistoryView {

    companion object {

        private const val REQUEST_PICK_IMAGE = 1
        private const val REQUEST_WRITE_EXTERNAL_STORAGE = 2

        fun newInstance(): HistoryFragment {
            return HistoryFragment()
        }
    }

    @InjectPresenter
    lateinit var presenter: HistoryPresenter

    @BindView(R.id.history_recycler)
    lateinit var historyRecycler: RecyclerView

    @BindView(R.id.empty)
    lateinit var emptyView: TextView

    private lateinit var adapter: HistoryAdapter

    override fun inject() {
        InjectorManager.appComponent?.inject(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = HistoryAdapter(context)
        historyRecycler.layoutManager = LinearLayoutManager(context)
        historyRecycler.adapter = adapter
        historyRecycler.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        val callback = object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                openAcceptDeleteColor(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(historyRecycler)

        adapter.itemSelectListener = object : BaseRecyclerAdapter.OnItemSelectListener {
            override fun onItemSelect(position: Int) {
                adapter.getItem(position)?.let { presenter.handleColorSelected(it) }
            }
        }

        adapter.itemLongClickListener = object : BaseRecyclerAdapter.OnItemLongClickListener {
            override fun onItemLongClick(position: Int) {
                openAcceptDeleteColor(position)
            }
        }
    }

    override fun showHistory(history: List<ColorPhotoEntity>) {
        val diffResult = DiffUtil.calculateDiff(HistoryDiffCallback(adapter.data, history))

        adapter.data.clear()
        adapter.data.addAll(history)
        diffResult.dispatchUpdatesTo(adapter)

        emptyView.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    override fun openPickPhoto() {
        logger.debug("openPickPhoto")
        if (hasStoragePermission()) {
            requestPermission()
            return
        }

        val title = getString(R.string.select_image_title)
        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
        getIntent.type = "image/*"

        val pickIntent = Intent(Intent.ACTION_PICK)
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")

        val chooserIntent = Intent.createChooser(getIntent, title)
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
                    if (dataUri != null) {
                        val resolver = context?.contentResolver
                        logger.debug("Try to build bitmap from: {}", dataUri)
                        val bitmap = MediaStore.Images.Media.getBitmap(resolver, dataUri)
                        presenter.handleSelectedImage(AppHelper.getPath(context!!, dataUri), bitmap)
                    } else {
                        logger.warn("Data uri is null")
                    }
                } catch (e: Exception) {
                    logger.warn("Cannot load bitmap: ", e)
                }
            }
        } else {
            logger.warn("Result doesn't success: {}", resultCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
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
            val dialog = DialogHelper.buildYesNoDialog(it, title, message, DialogInterface.OnClickListener { _, _ ->
                adapter.getItem(position)?.let { presenter.removeColor(it) }
                emptyView.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
            })
            dialog.setOnCancelListener { adapter.notifyItemChanged(position) }
            dialog.show()
        }
    }

    private fun hasStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(activity!!,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_WRITE_EXTERNAL_STORAGE)
    }
}
