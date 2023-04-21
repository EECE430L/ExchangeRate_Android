package com.eece430L.inflaterates.utilities

import com.google.android.material.textfield.TextInputLayout

object ValidatorUtils {

    fun validateCredentialsInput(
        username: String?, password: String?, usernameInputLayout: TextInputLayout?,
        passwordInputLayout: TextInputLayout?): Boolean {

        var inputIsValid = true
        if (username == null || password == null) {
            return false
        }

        if (username.isBlank()) {
            inputIsValid = false
            usernameInputLayout?.error = "Please enter your username"
        } else {
            usernameInputLayout?.error = null
        }

        if (password.isBlank()) {
            inputIsValid = false
            passwordInputLayout?.error = "Please enter your password"
        } else {
            passwordInputLayout?.error = null
        }

        return inputIsValid
    }

    fun validateTransactionInput(usdAmount: String?,
                                 lbpAmount: String?,
                                 usdAmountEditText: TextInputLayout?,
                                 lbpAmountEditText: TextInputLayout?): Boolean {
        var inputIsValid = true
        if (usdAmount == null || lbpAmount == null) {
            return false
        }
        val usdAmountFloat: Float? = usdAmount.toFloatOrNull()
        if (usdAmount.isBlank() || usdAmountFloat == null) {
            inputIsValid = false
            usdAmountEditText?.error = "Please enter a valid number"
        }
        else if (usdAmountFloat <= 0) {
            inputIsValid = false
            usdAmountEditText?.error = "Please enter a positive number"
        }
        else {
            usdAmountEditText?.error = null
        }

        val lbpAmountFloat: Float? = lbpAmount.toFloatOrNull()
        if (lbpAmount.isBlank() || lbpAmountFloat == null) {
            inputIsValid = false
            lbpAmountEditText?.error = "Please enter a valid number"
        }
        else if (lbpAmountFloat <= 0) {
            inputIsValid = false
            lbpAmountEditText?.error = "Please enter a positive number"
        }
        else {
            lbpAmountEditText?.error = null
        }

        return inputIsValid
    }

}