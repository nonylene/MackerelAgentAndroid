package net.nonylene.mackerelagent

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.nonylene.mackerelagent.databinding.LogRecyclerItemBinding
import net.nonylene.mackerelagent.viewmodel.LogRecyclerItemViewModel

class LogRecyclerAdapter : RecyclerView.Adapter<LogRecyclerAdapter.ViewHolder>() {

    var logs: List<AgentLog> = listOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.log_recycler_item, parent, false))
    }

    override fun getItemCount() = logs.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.model.setRealmLog(logs[position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding: LogRecyclerItemBinding

        init {
            binding = DataBindingUtil.bind(itemView)
            binding.model = LogRecyclerItemViewModel()
        }
    }
}

