package com.eece430L.inflaterates.signup_and_login

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import com.eece430L.inflaterates.MainActivity
import com.eece430L.inflaterates.R
import com.eece430L.inflaterates.api.InflateRatesService
import com.eece430L.inflaterates.api.models.UserModel
import com.eece430L.inflaterates.utilities.HttpStatusCodesUtil
import com.eece430L.inflaterates.utilities.ProgressBarManager
import com.eece430L.inflaterates.utilities.TextChangeListenerUtils
import com.eece430L.inflaterates.utilities.ValidatorUtils
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupFragment : Fragment() {

    private var activity: Activity? = null
    private val progressBarManager = ProgressBarManager()
    private var emailEditText: TextInputLayout? = null
    private var usernameEditText: TextInputLayout? = null
    private var passwordEditText: TextInputLayout? = null
    private var signupButton : Button? = null
    private var linkToLoginTextView: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View =  inflater.inflate(R.layout.fragment_signup, container, false)

        activity = requireActivity()

        // obtain a reference to different ui elements
        // configure some elements by setting their on click / text change listeners

        emailEditText = view.findViewById(R.id.emailLayout)
        TextChangeListenerUtils.setTextChangeListener(emailEditText!!)

        usernameEditText = view.findViewById(R.id.usernameLayout)
        TextChangeListenerUtils.setTextChangeListener(usernameEditText!!)

        passwordEditText = view.findViewById(R.id.passwordLayout)
        TextChangeListenerUtils.setTextChangeListener(passwordEditText!!)

        signupButton = view.findViewById(R.id.btnLogin)
        signupButton?.setOnClickListener { _ -> signup() }

        linkToLoginTextView = view.findViewById(R.id.textViewLinkToLogin)
        linkToLoginTextView?.setOnClickListener { _ ->
            (activity as MainActivity).switchToLoginFragment()
        }
        val linkToLogin = SpannableString("Already have an account? Login now!")
        linkToLogin.setSpan(UnderlineSpan(), 0, linkToLogin.length, 0)
        linkToLoginTextView?.text = linkToLogin

        return view
    }

    private fun signup() {

        // hide the keyboard when the user presses on signup
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(signupButton?.windowToken, 0)

        val email = emailEditText?.editText?.text?.toString()
        val username = usernameEditText?.editText?.text?.toString()
        val password = passwordEditText?.editText?.text?.toString()

        val inputIsValid = ValidatorUtils.validateCredentialsInput(
            username, password, usernameEditText, passwordEditText)
        val emailIsValid = ValidatorUtils.validateEmail(email, emailEditText)

        if(!inputIsValid || !emailIsValid) {
            return
        }

        val user = UserModel(email, username, password)
        progressBarManager.showProgressBar(requireActivity())

        InflateRatesService.inflateRatesApi().signup(user).enqueue(object: Callback<UserModel> {
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                progressBarManager.hideProgressBar()
                if(response.isSuccessful) {
                    val snackbar = Snackbar.make(
                        signupButton as View,
                        "Account created. Please sign in.",
                        1000
                    )
                    snackbar.addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            (activity as MainActivity).switchToLoginFragment()
                        }
                    })
                    snackbar.show()
                }
                else {
                    var message = HttpStatusCodesUtil.httpStatusCodeToMessage(response.code())
                    if(response.code() == 409) { message = "User Already exists!" }
                    else if(message == "") { message = response.code().toString() }
                    Snackbar.make(
                        signupButton as View,
                        message,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                progressBarManager.hideProgressBar()
                Snackbar.make(
                    signupButton as View,
                    t.message.toString(),
                    Snackbar.LENGTH_LONG
                ).show()
            }

        })
    }
}