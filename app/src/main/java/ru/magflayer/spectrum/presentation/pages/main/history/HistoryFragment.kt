package ru.magflayer.spectrum.presentation.pages.main.history

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
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
        fun newInstance(): HistoryFragment {
            return HistoryFragment()
        }
    }

    private val viewBinding by viewBinding(FragmentHistoryBinding::bind)

    @InjectPresenter
    lateinit var presenter: HistoryPresenter

    private lateinit var historyAdapter: HistoryAdapter

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let { safeUri ->
                logger.debug("Try to build bitmap from: {}", safeUri)
                val inputStream = requireContext().contentResolver.openInputStream(safeUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                presenter.handleSelectedImage(safeUri, bitmap)
            } ?: logger.warn("Data uri is null")
        }

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
                historyAdapter.getItem(position)?.let { openHistoryDetailsScreen(it.filePath) }
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
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    override fun openHistoryDetailsScreen(filePath: String) {
        val action = HistoryFragmentDirections.nextAction(filePath)
        findNavController().navigate(action)
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
}
