package com.example.smartmedicinebox.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.smartmedicinebox.databinding.HistoryListItemBinding
import com.example.smartmedicinebox.databinding.ListAdminItemBinding
import com.example.smartmedicinebox.entity.History

class HistoryListViewAdapter(
    private val context: Context,
    private var listData: MutableList<Any>
) : BaseAdapter() {
    override fun getCount(): Int {
        return listData.size
    }

    override fun getItem(i: Int): Any {
        return listData[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, p0: View?, viewGroup: ViewGroup): View {
        var view = p0
        val holder: ViewHolder
        if (view == null) {
            val binding = HistoryListItemBinding.inflate(
                LayoutInflater.from(
                    context
                ), viewGroup, false
            )
            view = binding.root
            holder = ViewHolder(binding)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }


        initView(holder.binding, i)


        return view
    }

    private fun initView(binding: HistoryListItemBinding, index: Int) {
        val history = listData[index] as History
        binding.name.text = history.name
        binding.boxId.text = "ID: ${history.bid}"
        binding.time.text = history.createDateTime
    }

    class ViewHolder(var binding: HistoryListItemBinding)
}