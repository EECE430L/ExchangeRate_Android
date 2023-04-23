package com.eece430L.inflaterates.utilities

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputLayout

// https://stackoverflow.com/questions/68285436/how-do-i-clear-an-error-when-typing-in-an-edittext-field
// Converted to utility function by ChatGPT
object TextChangeListenerUtils {

      fun setTextChangeListener(textInputLayout: TextInputLayout) {
         textInputLayout.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textInputLayout.error = null
            }
        })
    }
}