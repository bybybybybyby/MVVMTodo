package com.codinginflow.mvvmtodo.util

import androidx.appcompat.widget.SearchView

inline fun SearchView.onQueryTextChange(crossinline listener: (String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            // Don't care about this override function, so just returning true.
            // Using the onQueryTextChange for immediate results instead.
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText.orEmpty())
            return true
        }
    })
}