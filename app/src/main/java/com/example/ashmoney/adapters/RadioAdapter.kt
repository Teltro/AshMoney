package com.example.ashmoney.adapters

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ashmoney.holder.RadioHolder
import com.example.ashmoney.models.ui.RecyclerViewUIModel

abstract class RadioAdapter<Item : RecyclerViewUIModel, ViewHolder : RadioHolder<Item>>(
    private val selectFirstItemIfNotExist: Boolean = false,
    private val selectionChangeListener: ((selectedItem: Item?) -> Unit)? = null
) : ListAdapter<Item, ViewHolder>(RecyclerViewUIModelDiffCallback<Item>()) {

    protected lateinit var recyclerView: RecyclerView

    private var selectedPosition: Int? = null
        set(value) {
            if (this::recyclerView.isInitialized) {

                selectedViewHolder?.let { viewHolder ->
                    field?.let { oldSelectedIndex ->
                        // mb it will be faster?
                        //if (viewHolder.item == currentList[oldSelectedIndex])
                        if (currentList.indexOf(viewHolder.item) == oldSelectedIndex)
                            viewHolder.selected = false
                    }
                }

                value?.let { newSelectedIndex ->
                    val viewHolder =
                        recyclerView.findViewHolderForAdapterPosition(newSelectedIndex) as? ViewHolder
                    viewHolder?.selected = true
                    selectedViewHolder = viewHolder
                } ?: run {
                    selectedViewHolder = null
                }

            }

            field = value
        }

    protected var selectedViewHolder: ViewHolder? = null

    //private var _selectedItem: Item? = null

    var selectedItem: Item? = null
        set(value) {
            changeSelectedPosition(value)
            /*currentList.indexOf(value).let { index ->
                val selectedPosition = if (index >= 0)
                    index
                else
                    null

                *//*val _selectedItem = if (selectedPosition != null)
                    currentList.getOrNull(selectedPosition)
                else
                    null

                selectionChangeListener?.invoke(_selectedItem)*//*


                this.selectedPosition = selectedPosition
            }*/
            selectionChangeListener?.invoke(value)

            field = value
        }



    private fun changeSelectedPosition(item: Item?) {
        selectedPosition = item?.let {
            //val index = currentList.indexOf(it)
            val index = currentList.indexOfFirst { listItem -> listItem.same(it) }
            if (index >= 0) index else null
        }
    }

    init {
        //_selectedItem = firstLoadSelectedItem
        //selectedItem = firstLoadSelectedItem
    }


    /*fun setSelectedItemIfExist(item: Item) {
        currentList.indexOf(item).let { index ->
            val selectedPosition = if (index >= 0)
                index
            else if (selectFirstItemIfNotExist && currentList.isNotEmpty())
                0
            else
                null

            _selectedItem = if (selectedPosition != null)
                currentList.getOrNull(selectedPosition)
            else
                null

            selectionChangeListener?.invoke(_selectedItem)

            _selectedPosition = selectedPosition
        }
    }*/

    //fun getSelectedItem(): Item? = _selectedItem

    override fun onCurrentListChanged(
        previousList: MutableList<Item>,
        currentList: MutableList<Item>
    ) {
        super.onCurrentListChanged(previousList, currentList)

        changeSelectedPosition(selectedItem)

        /*selectedItem?.let {
            changeSelectedPosition(it)
        } ?: run {
            selectedPosition = null
            *//*if (selectFirstItemIfNotExist && currentList.isNotEmpty())
                setSelectedItemIfExist(currentList[0])*//*
        }*/

        /*_selectedItem?.let {
            setSelectedItemIfExist(it)
        } ?: run {
            if (selectFirstItemIfNotExist && currentList.isNotEmpty())
                setSelectedItemIfExist(currentList[0])
        }*/
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]

        val isSelected = position == selectedPosition
        if (isSelected)
            selectedViewHolder = holder

        val _position = position

        holder.bind(item, isSelected) {
            selectedItem = currentList.getOrNull(_position)
            selectedPosition = _position
            //selectionChangeListener?.invoke(selectedItem)
            /*_selectedItem = currentList.getOrNull(_position)
            _selectedPosition = _position
            selectionChangeListener?.invoke(_selectedItem)*/

            //setSelectedPositionWithSelectedItemChange(_position)
        }

    }

}