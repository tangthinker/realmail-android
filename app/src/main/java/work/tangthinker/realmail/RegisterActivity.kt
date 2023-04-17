package work.tangthinker.realmail

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.tangthinker.realmail.databinding.ActivityRegisterBinding
import work.tangthinker.realmail.network.service.UserAuthService
import work.tangthinker.realmail.utils.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val userAuthService = RetrofitServiceCreator.create<UserAuthService>()

    private lateinit var preferences: SharedPreferences

    private var idValid : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferences = getSharedPreferences("authInfo", MODE_PRIVATE)
        val sharedPreferencesHelper = SharedPreferencesHelper(preferences)

        val toastCreator = ToastCreator(this)

        val intent = Intent(this, MainActivity::class.java)
        val loadingDialog = LoadingDialog(this, layoutInflater)
        val buttonCountDownTimer = ButtonCountDownTimer(binding.rlSendVerifyCode, binding.tvSendVerifyCode)

        binding.rlRegister.setOnClickListener {
            if (idValid){
                loadingDialog.show()
                userAuthService.register(binding.etUserId.text.toString(), binding.etPassword.text.toString(),
                    binding.etEmail.text.toString(), binding.etVerifyCode.text.toString())
                    .enqueue(object : Callback<UserAuthService.UserAuthResult> {
                        override fun onResponse(call: Call<UserAuthService.UserAuthResult>, response: Response<UserAuthService.UserAuthResult>) {
                            loadingDialog.dismiss()
                            val userAuthResult = response.body()
                            userAuthResult?.let {
                                println(userAuthResult)
                                if (userAuthResult.result == 1) {
                                    sharedPreferencesHelper.setTokenPackage(SharedPreferencesHelper.TokenPackage(userAuthResult.uuid, userAuthResult.token))
                                    sharedPreferencesHelper.setUserId(userAuthResult.user_id)
                                    toastCreator.registerSuccessful.show()
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    return
                                }
                            }
                            toastCreator.verifyCodeError.show()
                        }

                        override fun onFailure(call: Call<UserAuthService.UserAuthResult>, t: Throwable) {
                            loadingDialog.dismiss()
                            toastCreator.networkError.show()
                        }

                    })
            }else toastCreator.idOccupied.show()
        }

        binding.rlSendVerifyCode.setOnClickListener {
            loadingDialog.show()
            userAuthService.sendCode(binding.etUserId.text.toString(), binding.etEmail.text.toString()).enqueue(object : Callback<UserAuthService.SingleResult>{
                override fun onResponse(call: Call<UserAuthService.SingleResult>, response: Response<UserAuthService.SingleResult>) {
                    loadingDialog.dismiss()
                    val singleResult = response.body()
                    singleResult?.let {
                        if (singleResult.result == 1){
                            buttonCountDownTimer.start()
                            toastCreator.verifyCodeSent.show()
                        }
                    }
                }

                override fun onFailure(call: Call<UserAuthService.SingleResult>, t: Throwable) {
                    loadingDialog.dismiss()
                    toastCreator.networkError.show()
                }

            })
        }

        binding.etUserId.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus){
                userAuthService.checkUserId(binding.etUserId.text.toString()).enqueue(object : Callback<UserAuthService.SingleResult>{
                    override fun onResponse(call: Call<UserAuthService.SingleResult>, response: Response<UserAuthService.SingleResult>) {
                        val singleResult = response.body()
                        print(singleResult)
                        if (singleResult != null){
                            if (singleResult.result != 1){
                                idValid = false
                                toastCreator.idOccupied.show()
                            }else {
                                idValid = true
                                toastCreator.idNotOccupied.show()
                            }
                        }
                    }
                    override fun onFailure(call: Call<UserAuthService.SingleResult>, t: Throwable) {
                        toastCreator.networkError.show()
                    }

                })
            }
        }




    }


    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.bottom_silent, R.anim.setting_close)

    }



}