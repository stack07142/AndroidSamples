package k.t.sample_ble.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.trello.rxlifecycle3.components.support.RxFragment
import k.t.sample_ble.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.central_fragment.*
import kotlinx.android.synthetic.main.listitem_device.*

private const val SCAN_PERIOD: Long = 5000L

class CentralFragment : RxFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.central_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvScanList.apply {
            setHasFixedSize(true)
        }

        btnScan.setOnClickListener {

        }
    }
}

class ScanResultAdapter : ListAdapter<ScanResultAdapter.Item, ScanResultAdapter.ItemViewHolder>(ItemCallback()) {
    private val itemList: MutableList<Item>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listitem_device, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        itemList?.get(position)?.let {
            with(holder) {
                tvDeviceName.text = it.name
                tvDeviceAddress.text = it.address
            }
        }
    }

    override fun submitList(list: MutableList<Item>?) {
        super.submitList(list)
    }

    fun add(item: Item) {
        itemList?.add(item)
    }

    fun clear() {
        itemList?.clear()
    }

    class ItemViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer

    data class Item(val name: String, val address: String)

    class ItemCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean =
            areContentsTheSame(oldItem, newItem)

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean =
            oldItem.name == newItem.name && oldItem.address == newItem.address
    }
}

