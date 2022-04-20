package com.dev_sammi.packagename.guessit

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import timber.log.Timber


inline fun SearchView.onTextChangeListener(crossinline listener: (String)-> Unit){
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
        override fun onQueryTextSubmit(queryTextS: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(queryText: String?): Boolean {
            listener(queryText.orEmpty())
            Timber.d("this is the query: $queryText")
            return true
        }
    })

}

// fun to hide keyboard
fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}