package work.tangthinker.realmail

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.tangthinker.realmail.cache.CacheService
import work.tangthinker.realmail.cache.CacheServiceSharedPreferencesImp
import work.tangthinker.realmail.databinding.ActivityPersonalSpaceBinding
import work.tangthinker.realmail.network.service.PersonalInfoService
import work.tangthinker.realmail.utils.LoadingDialog
import work.tangthinker.realmail.utils.RetrofitServiceCreator
import work.tangthinker.realmail.utils.SharedPreferencesHelper
import work.tangthinker.realmail.utils.ToastCreator

class PersonalSpaceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalSpaceBinding

    private val personalInfoService = RetrofitServiceCreator.create<PersonalInfoService>()

    private lateinit var preferences : SharedPreferences

    private lateinit var cacheService: CacheService

    private lateinit var sharedPreferencesHelper : SharedPreferencesHelper

    private val CACHE_NAME = "personal_info"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPersonalSpaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rlAddToAddressBook.visibility = View.GONE
        binding.rlSendMailTo.visibility = View.GONE

        preferences = getSharedPreferences("authInfo", MODE_PRIVATE)
        cacheService = CacheServiceSharedPreferencesImp(this)
        sharedPreferencesHelper = SharedPreferencesHelper(preferences)
        val loadingDialog = LoadingDialog(this, layoutInflater)
//        loadingDialog.show()
        val toastCreator = ToastCreator(this)

        val tokenPackage = sharedPreferencesHelper.getTokenPackage()

        binding.tvUserId.text = sharedPreferencesHelper.getUserId()

        if (cacheService.isCacheExists(CACHE_NAME)){
            setData(cacheService.getCache(CACHE_NAME, PersonalInfoService.PersonalInfoResult::class.java))
        }

        personalInfoService.getPersonalInfo(tokenPackage.uuid!!, tokenPackage.token!!).enqueue(object : Callback<PersonalInfoService.PersonalInfoResult>{
            override fun onResponse(call: Call<PersonalInfoService.PersonalInfoResult>, response: Response<PersonalInfoService.PersonalInfoResult>) {
                println(response.body())
                val personalInfoResult = response.body()
                personalInfoResult?.let {
                    setData(personalInfoResult)
                    cacheService.saveCache(CACHE_NAME, personalInfoResult)
//                    loadingDialog.dismiss()
                    return
                }
//                loadingDialog.dismiss()
                toastCreator.networkError.show()


            }

            override fun onFailure(
                call: Call<PersonalInfoService.PersonalInfoResult>,
                t: Throwable
            ) {
//                loadingDialog.dismiss()
                toastCreator.networkError.show()
            }

        })


    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.bottom_silent, R.anim.horizontal_close)
    }


    fun setData(personalInfoResult: PersonalInfoService.PersonalInfoResult){
        binding.tvNeckName.text = personalInfoResult.personal_info.neckName
        binding.tvSlogan.text = personalInfoResult.personal_info.personalSlogan
        binding.tvBirthdayDate.text = personalInfoResult.personal_info.birthday
        binding.tvReceiveTime.text = personalInfoResult.personal_info.mailReceiveTime
        binding.tvSignTime.text = personalInfoResult.personal_info.accountSignTime
        if ("defaultHeadIcon" != personalInfoResult.personal_info.headIcon){
            val imageUrl = "${RetrofitServiceCreator.BASE_URL}${personalInfoResult.personal_info.headIcon}"
            Glide.with(this@PersonalSpaceActivity).load(imageUrl).apply(
                RequestOptions.bitmapTransform(
                    RoundedCorners(20)
                )).into(binding.ivHeadIcon)
        }
        if ("defaultBackground" != personalInfoResult.personal_info.spaceBackground){
            val imageUrl = "${RetrofitServiceCreator.BASE_URL}${personalInfoResult.personal_info.spaceBackground}"
            Glide.with(this@PersonalSpaceActivity).load(imageUrl)
                .into(binding.ivSpaceImage)
        }
    }



}