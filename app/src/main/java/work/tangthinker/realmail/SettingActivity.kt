package work.tangthinker.realmail

import android.content.Intent
import android.content.SharedPreferences
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Response
import work.tangthinker.realmail.databinding.ActivitySettingBinding
import work.tangthinker.realmail.network.service.UserAuthService
import work.tangthinker.realmail.utils.RetrofitServiceCreator
import work.tangthinker.realmail.utils.SharedPreferencesHelper

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding

    private lateinit var preferences: SharedPreferences

    private val userAuthService = RetrofitServiceCreator.create<UserAuthService>()

    private var noticeSetting: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferences = getSharedPreferences("authInfo", MODE_PRIVATE)
        val sharedPreferencesHelper = SharedPreferencesHelper(preferences)

        binding.rlLogout.setOnClickListener {
            val tokenPackage = sharedPreferencesHelper.getTokenPackage()
            userAuthService.logout(tokenPackage.uuid.toString(), tokenPackage.token.toString()).enqueue(logoutCallback)
            sharedPreferencesHelper.clear()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            overridePendingTransition(R.anim.setting_open, R.anim.bottom_silent)
        }

        binding.btnDoneSetting.setOnClickListener {
            finish()
        }

        binding.rlEnterPersonalSpace.setOnClickListener {
            val intent = Intent(this, PersonalSpaceActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.horizontal_open, R.anim.bottom_silent)
        }

        binding.rlModifyPersonalInfo.setOnClickListener {
            val intent = Intent(this, ModifyPersonalInfo::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.setting_open, R.anim.bottom_silent)
        }

        binding.rlModifyPassword.setOnClickListener {
            val intent = Intent(this, ModifyPasswordLoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.setting_open, R.anim.bottom_silent)
        }

        binding.rlCollectMailId.setOnClickListener {
            val intent = Intent(this, AddressBookActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.horizontal_open, R.anim.bottom_silent)
        }

        binding.switchNoticeSetting.setOnCheckedChangeListener { buttonView, isChecked ->
            noticeSetting = isChecked
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.vibrate(CombinedVibration.createParallel(VibrationEffect.createOneShot(1, 1)))
            } else {
                val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(1)
            }

        }
        
        binding.rlAboutApp.setOnClickListener {
            var intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.setting_open, R.anim.bottom_silent)
        }


    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.bottom_silent, R.anim.setting_close)
    }

    private var logoutCallback = object : retrofit2.Callback<UserAuthService.SingleResult> {
        override fun onResponse(
            call: Call<UserAuthService.SingleResult>,
            response: Response<UserAuthService.SingleResult>
        ) {
        }

        override fun onFailure(call: Call<UserAuthService.SingleResult>, t: Throwable) {
        }

    }



}