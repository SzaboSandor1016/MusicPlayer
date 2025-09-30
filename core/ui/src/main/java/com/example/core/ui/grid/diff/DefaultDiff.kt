package com.example.core.ui.grid.diff

import androidx.recyclerview.widget.DiffUtil

abstract class DefaultDiff<T>: DiffUtil.Callback() {

    private val oldList: MutableList<T> = ArrayList()
    private val newList: MutableList<T> = ArrayList()

    fun setList(oldList: List<T>, newList: List<T>) {

        this.oldList.clear()
        this.oldList.addAll(oldList)

        this.newList.clear()
        this.newList.addAll(newList)
    }

    fun getOldItem(position: Int) = oldList.getOrNull(position)

    fun getNewItem(position: Int) = newList.getOrNull(position)

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }
}