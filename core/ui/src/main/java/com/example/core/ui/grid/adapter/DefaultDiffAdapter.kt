package com.example.core.ui.grid.adapter

import androidx.annotation.CallSuper
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.core.ui.grid.diff.DefaultDiff

abstract class DefaultDiffAdapter<T, D : DefaultDiff<T>, VH : RecyclerView.ViewHolder> :
    RecyclerView.Adapter<VH>() {

    private var diffResult: DiffUtil.DiffResult? = null

    protected val list: MutableList<T> = ArrayList()

    abstract val diff: D

    @CallSuper
    open fun setList(list: List<T>): DefaultDiffAdapter<T, D, VH> = apply {

        diff.setList(this.list, list)

        diffResult = DiffUtil.calculateDiff(diff)
    }

    fun notifyList(list: List<T>) {
        setList(list)
        diffResult?.dispatchUpdatesTo(this)
    }
    fun getItemList(): MutableList<T> {

        return this.list
    }

    override fun getItemCount(): Int {
        return this.list.size
    }
}