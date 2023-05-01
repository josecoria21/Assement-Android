package dev.propoc.honeywell.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.propoc.honeywell.model.Items

class ListViewModel: ViewModel() {

    private val _itemsList = MutableLiveData<MutableList<Items>>()
    var itemsList: LiveData<MutableList<Items>> = _itemsList

    init {
        _itemsList.value = ArrayList()
    }

    fun addNewItem(element: Items) {
        _itemsList.value?.add(element)
        _itemsList.value = _itemsList.value
    }

    fun updateItem(item: Items?, position: Int) {
        _itemsList.value?.get(position)?.name = item?.name.toString()
        item?.image?.let { _itemsList.value?.get(position)?.image = it }
        item?.colorCoding?.let { _itemsList.value?.get(position)?.colorCoding = it }
    }
}