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
}