package com.core.base.usecases

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator


abstract class OneItemSelectionAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected open lateinit var toggle: (RecyclerView.ViewHolder, Boolean) -> Unit


    //Prevent blink on notifyItemChanged
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = toggle(holder, position, toggle)

    var selected: Int = 0
    var prevSelected: Int = 0

    fun selectOption(item: Int) {
        prevSelected = selected
        selected = item

        notifyItemChanged(prevSelected)
        notifyItemChanged(selected)
    }

    fun toggle(
        holder: RecyclerView.ViewHolder,
        pos: Int, toggle: (holder: RecyclerView.ViewHolder, selected: Boolean) -> Unit
    ) {
        when {
            selected == pos -> toggle.invoke(holder, true)
            prevSelected == pos -> toggle.invoke(holder, false)
            else -> toggle.invoke(holder, false)
        }
    }
}