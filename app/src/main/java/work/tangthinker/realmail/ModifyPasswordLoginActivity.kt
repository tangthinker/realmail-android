package work.tangthinker.realmail

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.tangthinker.realmail.databinding.ActivityModifyPasswordBinding
import work.tangthinker.realmail.network.service.UserAuthService
import work.tangthinker.realmail.utils.*

class ModifyPasswordLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModifyPasswordBinding

    private lateinit var preferences: SharedPreferences

    private val userAuthService = RetrofitServiceCreator.create<UserAuthService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModifyPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvTitle.text = "修改RealMail ID密码"

        binding.etUserId.visibility = View.GONE

        preferences = getSharedPreferences("authInfo", MODE_PRIVATE)
        val sharedPreferencesHelper = SharedPreferencesHelper(preferences)
        val loadingDialog = LoadingDialog(this, layoutInflater)
        val userId = sharedPreferencesHelper.getUserId()

        val toastCreator = ToastCreator(this)
        val buttonCountDownTimer = ButtonCountDownTimer(binding.rlSendVerifyCode, binding.tvSendVerifyCode)

        val intent = Intent(this, MainActivity::class.java)

        binding.rlModifyPassword.setOnClickListener {
            loadingDialog.show()
            userAuthService.modifyPassword(userId!!, binding.etNewPassword.text.toString(), binding.etEmail.text.toString(),
                binding.etVerifyCode.text.toString()).enqueue(object : Callback<UserAuthService.UserAuthResult>{
                override fun onResponse(call: Call<UserAuthService.UserAuthResult>, response: Response<UserAuthService.UserAuthResult>) {
                    loadingDialog.dismiss()
                    val userAuthResult = response.body()
                    userAuthResult?.let {
                        println(userAuthResult)
                        if (userAuthResult.result == 1){
                            sharedPreferencesHelper.setTokenPackage(SharedPreferencesHelper.TokenPackage(userAuthResult.uuid, userAuthResult.token))
                            sharedPreferencesHelper.setUserId(userAuthResult.user_id)
                            toastCreator.modifyPasswordSuccessful.show()
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            return
                        }
                    }
                    toastCreator.infoOrVerifyCodeError.show()
                }

                override fun onFailure(call: Call<UserAuthService.UserAuthResult>, t: Throwable) {
                    loadingDialog.dismiss()
                    toastCreator.networkError.show()
                }

            })
        }

        binding.rlSendVerifyCode.setOnClickListener {
            loadingDialog.show()
            userAuthService.sendCode(userId!!, binding.etEmail.text.toString()).enqueue(object : Callback<UserAuthService.SingleResult> {
                override fun onResponse(call: Call<UserAuthService.SingleResult>, response: Response<UserAuthService.SingleResult>) {
                    loadingDialog.dismiss()
                    val singleResult = response.body()
                    singleResult?.let {
                        if (singleResult.result == 1){
                            toastCreator.verifyCodeSent.show()
                            buttonCountDownTimer.start()
                        }
                    }
                }

                override fun onFailure(call: Call<UserAuthService.SingleResult>, t: Throwable) {
                    loadingDialog.dismiss()
                    toastCreator.networkError.show()
                }

            })
        }



    }


    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.bottom_silent, R.anim.setting_close)

    }



}