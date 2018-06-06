package de.everybag.express

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import de.everybag.express.ui.mainscreen.MainActivity
import de.everybag.express.utils.DialogUtils
import de.everybag.express.utils.NetworkUtils

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val dialogUtils = DialogUtils(this)
        if (!NetworkUtils.isNetworkConnected(this)) {
            dialogUtils.messageBoxDialog(getString(R.string.error_no_network_label),
                    getString(R.string.error_no_network_msg), DialogUtils.KindMessageBox.Finish)
            return
        }

        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 500)
    }
}
