package com.eece430L.inflaterates

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.eece430L.inflaterates.utilities.Authentication
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    var drawerLayout: DrawerLayout? = null;
    var navigationView: NavigationView? = null;

    lateinit var toggle: ActionBarDrawerToggle;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Authentication.initialize(this)

        drawerLayout = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(this, drawerLayout,  R.string.open, R.string.close)
        drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        replaceFragment(ExchangeRateFragment(), "Exchange Rate")

        navigationView = findViewById(R.id.navigation_view)
        navigationView?.setNavigationItemSelectedListener {

            when(it.itemId) {
                R.id.nav_exchange_rate -> replaceFragment(ExchangeRateFragment(), it.title.toString())
                R.id.nav_statistics -> replaceFragment(StatisticsFragment(), it.title.toString())
                R.id.nav_record_an_exchange -> replaceFragment(RecordAnExchangeFragment(), it.title.toString())
                R.id.nav_offer_a_transaction -> replaceFragment(OfferATransactionFragment(), it.title.toString())
                R.id.nav_my_transactions -> replaceFragment(MyTransactionsFragment(), it.title.toString())
                R.id.nav_offers_i_sent -> replaceFragment(OffersISentFragment(), it.title.toString())
                R.id.nav_offers_i_received -> replaceFragment(OffersIReceivedFragment(), it.title.toString())
                R.id.nav_logout -> logout()
                R.id.nav_login -> replaceFragment(LoginFragment(), it.title.toString())
                R.id.nav_signup -> replaceFragment(SignupFragment(), it.title.toString())
            }
            true
        }

        val isUserLoggedIn: Boolean = Authentication.getToken() != null
        updateNavigationMenu(isUserLoggedIn)
    }

     fun logout() {
        Authentication.clearToken()
        replaceFragment(ExchangeRateFragment(), "Exchange Rate")
        updateNavigationMenu(loggedIn = false)
    }

    private fun replaceFragment(fragment: Fragment, title: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
        drawerLayout?.closeDrawers()
        setTitle(title)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun updateNavigationMenu(loggedIn: Boolean) {
        navigationView?.menu?.clear()
        navigationView?.inflateMenu(R.menu.navigation_menu)
        navigationView?.menu?.findItem(R.id.authenticatedSection)?.isVisible = loggedIn
        navigationView?.menu?.findItem(R.id.transactionServiceSection)?.isVisible = loggedIn
        navigationView?.menu?.findItem(R.id.unAuthenticatedSection)?.isVisible = !loggedIn
    }

    fun switchToSignUpFragment() {
        replaceFragment(SignupFragment(), "Signup")
    }

    fun switchToLoginFragment() {
        replaceFragment(LoginFragment(), "Login")
    }

    fun switchToMyTransactionsFragment() {
        replaceFragment(MyTransactionsFragment(), "My Transactions")
    }

    fun switchToOfferATransactionFragment() {
        replaceFragment(OfferATransactionFragment(), "Offer a Transaction to a User")
    }

    fun switchToOffersIReceivedFragment() {
        replaceFragment(OffersIReceivedFragment(), "Offers I Received")
    }

    fun switchToOffersISentFragment() {
        replaceFragment(OffersISentFragment(), "Offers I Sent")
    }

    fun switchToRecordAnExchangeFragment() {
        replaceFragment(RecordAnExchangeFragment(), "Record an Exchange")
    }
}