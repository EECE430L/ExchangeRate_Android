package com.eece430L.inflaterates.signup_and_login

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.eece430L.inflaterates.MainActivity
import com.eece430L.inflaterates.R
import com.eece430L.inflaterates.api.InflateRatesService
import com.eece430L.inflaterates.api.models.TokenModel
import com.eece430L.inflaterates.api.models.UserModel
import com.eece430L.inflaterates.utilities.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    private var activity: Activity? = null
    private var navigationView: NavigationView? = null
    private val progressBarManager = ProgressBarManager()

    private var usernameEditText: TextInputLayout? = null
    private var passwordEditText: TextInputLayout? = null
    private var loginButton : Button? = null
    private var linkToSignUpTexView: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_login, container, false)

        activity = requireActivity()

        navigationView = activity?.findViewById(R.id.navigation_view)

        usernameEditText = view.findViewById(R.id.usernameLayout)
        TextChangeListenerUtils.setTextChangeListener(usernameEditText!!)

        passwordEditText = view.findViewById(R.id.passwordLayout)
        TextChangeListenerUtils.setTextChangeListener(passwordEditText!!)

        loginButton = view.findViewById(R.id.btnLogin)
        loginButton?.setOnClickListener { _ -> login() }

        linkToSignUpTexView = view.findViewById(R.id.textViewLinkToSignup)
        linkToSignUpTexView?.setOnClickListener { _ ->
            (activity as MainActivity).switchToSignUpFragment()
        }
//        https://www.geeksforgeeks.org/how-to-underline-text-in-textview-in-android/
        val linkToSignUp = SpannableString("Don't have an account? Register now!")
        linkToSignUp.setSpan(UnderlineSpan(), 0, linkToSignUp.length, 0)
        linkToSignUpTexView?.text = linkToSignUp

        return view
    }

    private fun login() {
//        ChatGPT
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(loginButton?.windowToken, 0)

        val username = usernameEditText?.editText?.text?.toString()
        val password = passwordEditText?.editText?.text?.toString()

        val inputIsValid = ValidatorUtils.validateCredentialsInput(
            username, password, usernameEditText, passwordEditText)

        if(!inputIsValid) {
            return
        }

        val user = UserModel(null, username, password)

        loginButton?.isEnabled = false
        progressBarManager.showProgressBar(requireActivity())

        InflateRatesService.inflateRatesApi().login(user).enqueue(object:
            Callback<TokenModel> {

            override fun onResponse(call: Call<TokenModel>, response: Response<TokenModel>) {
                progressBarManager.hideProgressBar()
                if(response.isSuccessful) {
                    response.body()?.token?.let { Authentication.saveToken(it) }

                    val mainActivity: MainActivity = activity as MainActivity
                    mainActivity.switchToMyTransactionsFragment()
                    mainActivity.updateNavigationMenu(loggedIn = true)
                }
                else {
                    var message = HttpStatusCodesUtil.httpStatusCodeToMessage(response.code())
                    if(response.code() == 404) { message = "A user with username $username was not found!"}
                    if(message == "") { message = response.code().toString() }
                    Snackbar.make(
                        loginButton as View,
                        message,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                loginButton?.isEnabled = true
            }

            override fun onFailure(call: Call<TokenModel>, t: Throwable) {
                progressBarManager.hideProgressBar()
                Snackbar.make(
                    loginButton as View,
                    t.message.toString(),
                    Snackbar.LENGTH_LONG
                ).show()
                loginButton?.isEnabled = true
            }

        })
    }
}