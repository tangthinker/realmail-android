package work.tangthinker.realmail

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import work.tangthinker.realmail.databinding.ActivityLoadingInfoBinding
import work.tangthinker.realmail.utils.SharedPreferencesHelper

class LoadingInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoadingInfoBinding

    private val handler : Handler = Handler(Looper.myLooper()!!)

    private lateinit var preferences : SharedPreferences



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = getSharedPreferences("authInfo", MODE_PRIVATE)

        val sharedPreferencesHelper = SharedPreferencesHelper(preferences)

        val tokenPackage = sharedPreferencesHelper.getTokenPackage()

        val intent : Intent = if (tokenPackage.token != null && tokenPackage.uuid != null){
            Intent(this, MainActivity::class.java)
        }else {
            Intent(this, LoginActivity::class.java)
        }

        handler.postDelayed({
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }, 1000)


    }

    override fun onRestart() {
        super.onRestart()
        handler.postDelayed({
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }, 300)
    }





}
