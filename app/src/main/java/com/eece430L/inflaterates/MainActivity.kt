package com.eece430L.inflaterates

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    var drawerLayout: DrawerLayout? = null;
    var navigationView: NavigationView? = null;

    lateinit var toggle: ActionBarDrawerToggle;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout,  R.string.open, R.string.close)
        drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        replaceFragment(ExchangeRateFragment(), "Exchange Rate")       

        navigationView?.setNavigationItemSelectedListener {

            when(it.itemId) {
                R.id.nav_exchange_rate -> replaceFragment(ExchangeRateFragment(), it.title.toString())
                R.id.nav_statistics -> replaceFragment(StatisticsFragment(), it.title.toString())
                R.id.nav_record_an_exchange -> replaceFragment(RecordAnExchangeFragment(), it.title.toString())
                R.id.nav_offer_a_transaction -> replaceFragment(OfferATransactionFragment(), it.title.toString())
                R.id.nav_my_transactions -> replaceFragment(MyTransactionsFragment(), it.title.toString())
                R.id.nav_profile -> replaceFragment(MyProfileFragment(), it.title.toString())
                R.id.nav_login -> replaceFragment(LoginFragment(), it.title.toString())
            }
            true
        }
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
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        val menu = navigationView.menu
        menu.findItem(R.id.authenticatedSection).isVisible = loggedIn
        menu.findItem(R.id.unAuthenticatedSection).isVisible = !loggedIn
    }

}