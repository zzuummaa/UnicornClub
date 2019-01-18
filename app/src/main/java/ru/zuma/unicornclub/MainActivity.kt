package ru.zuma.unicornclub

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),
        DailyUnicornFragment.OnFragmentInteractionListener,
        CollectionFragment.OnFragmentInteractionListener,
        AboutAuthorFragment.OnFragmentInteractionListener {

    private lateinit var dailyUnicornFragment: DailyUnicornFragment
    private lateinit var collectionFragment: CollectionFragment
    private lateinit var aboutAuthorFragment: AboutAuthorFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = getString(R.string.unicorn_club)

        launchPrintThrowable {
            initAuthToken()
        }

        dailyUnicornFragment = DailyUnicornFragment()
        collectionFragment = CollectionFragment()
        aboutAuthorFragment = AboutAuthorFragment()
        setContent(dailyUnicornFragment)

        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.new_unicorn -> { setContent(dailyUnicornFragment) }
                R.id.collection  -> { setContent(collectionFragment); collectionFragment.doLoadCollection() }
                R.id.about_author-> setContent(aboutAuthorFragment)
            }
            return@setOnNavigationItemSelectedListener true
        }
    }

    @SuppressLint("HardwareIds")
    private fun initAuthToken() {
        val token = Storage.loadAuthToken(this)
        if (token != null) {
            Backend.auth.token = token
            return
        }

        val androidId = Settings.Secure.getString(
                contentResolver,Settings.Secure.ANDROID_ID)

        Backend.authByAndroidId(androidId)?.let {
            Storage.storeAuthToken(this, it)
        }
    }

    private fun setContent(contentFragment: Fragment?) {
        // Create new fragment and transaction
        val transaction = supportFragmentManager.beginTransaction()

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.frContent, contentFragment)

        // Commit the transaction
        transaction.commit()
    }

    override fun onNewUnicornFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCollectionFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAboutAuthorFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
