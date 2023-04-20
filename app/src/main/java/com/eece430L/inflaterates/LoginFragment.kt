package com.eece430L.inflaterates

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout

class LoginFragment : Fragment() {

    private var activity: Activity? = null
    private var navigationView: NavigationView? = null
    private var usernameEditText: TextInputLayout? = null
    private var passwordEditText: TextInputLayout? = null
    private var loginButton : Button? = null
    private var linkToSignUpTexView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_login, container, false)

        activity = requireActivity()

        navigationView = activity?.findViewById(R.id.navigation_view)

        usernameEditText = view.findViewById(R.id.usernameLayout)
        usernameEditText?.setTextChangeListener()

        passwordEditText = view.findViewById(R.id.passwordLayout)
        passwordEditText?.setTextChangeListener()

        loginButton = view.findViewById(R.id.btnLogin)
        loginButton?.setOnClickListener { _ -> login() }

        linkToSignUpTexView = view.findViewById(R.id.textViewLinkToSignup)
        linkToSignUpTexView?.setOnClickListener {  }
        val linkToSignUp = SpannableString("Don't have an account? Register now!")
        linkToSignUp.setSpan(UnderlineSpan(), 0, linkToSignUp.length, 0)
        linkToSignUpTexView?.text = linkToSignUp

        return view
    }

    private fun login() {

        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(loginButton?.windowToken, 0)

        val username = usernameEditText?.editText?.text?.toString()
        val password = passwordEditText?.editText?.text?.toString()

        if (username == null || password == null) {
            return
        }

        var inputIsValid = true
        if (username.isBlank()) {
            inputIsValid = false
            usernameEditText?.error = "Please enter your username"
        }
        if (password.isBlank()) {
            inputIsValid = false
            passwordEditText?.error = "Please enter your password"
        }
        if (!inputIsValid) {
            return
        }

        (activity as MainActivity).updateNavigationMenu(loggedIn = true)

    }

    private fun TextInputLayout.setTextChangeListener() {
        this.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                this@setTextChangeListener.error = null
            }
        })
    }
}