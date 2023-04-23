package com.eece430L.inflaterates.utilities

import android.util.Patterns
import com.google.android.material.textfield.TextInputLayout

object ValidatorUtils {

    fun validateCredentialsInput(
        username: String?, password: String?, usernameInputLayout: TextInputLayout?,
        passwordInputLayout: TextInputLayout?): Boolean {

        var inputIsValid = true
        if (username == null || password == null) {
            return false
        }
//        ChatGPT
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

    fun validateOfferInput(receiver: String?,
                           offeredAmount: String?,
                           requestedAmount: String?,
                           receiverEditText: TextInputLayout?,
                           offeredAmountEditText: TextInputLayout?,
                           requestedAmountEditText: TextInputLayout?): Boolean {

        var inputIsValid = true

        if (receiver == null || offeredAmount == null || requestedAmount == null) {
            return false
        }

        if (receiver.isBlank()) {
            inputIsValid = false
            receiverEditText?.error = "Please enter the receiver's username"
        }
        else {
            receiverEditText?.error = null
        }

        if (offeredAmount.isBlank()) {
            inputIsValid = false
            offeredAmountEditText?.error = "Please enter a valid number"
        }
        else {
            offeredAmountEditText?.error = null
        }

        if (requestedAmount.isBlank()) {
            inputIsValid = false
            requestedAmountEditText?.error = "Please enter a valid number"
        }
        else {
            requestedAmountEditText?.error = null
        }

        return inputIsValid
    }

    fun validateEmail(email: String?, emailEditText: TextInputLayout?): Boolean {

        if(email?.isBlank() == true) {
            emailEditText?.error = "Please enter your email"
            return false
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText?.error = "Please enter a valid email address"
            return false
        }
        emailEditText?.error = null
        return true
    }
}