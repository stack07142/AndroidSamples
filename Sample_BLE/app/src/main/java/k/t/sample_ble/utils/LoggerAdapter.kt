package k.t.sample_ble.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import k.t.sample_ble.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.listitem_log.*

class LoggerAdapter : ListAdapter<String, LoggerAdapter.LogItemViewHolder>(LogItemCallback()) {
    private var rvLogger: RecyclerView? = null
    private var logList = mutableListOf<String>()
    private val maxCnt = 15

    fun clear() {
        logList.clear()
        submitList(logList)
        notifyDataSetChanged()
    }

    fun log(msg: String) {
        if (logList.size > maxCnt) {
            logList.removeAt(0)
        }
        logList.add(msg)
        submitList(logList)
        rvLogger?.scrollToPosition(itemCount - 1)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.rvLogger = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listitem_log, parent, false)
        return LogItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogItemViewHolder, position: Int) {
        getItem(position)?.let { logStr ->
            with(holder) {
                tvLogItem.text = logStr
            }
        }
    }

    class LogItemCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem
    }

    class LogItemViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer

    interface EventListener {
        fun log(msg: String)
    }
}