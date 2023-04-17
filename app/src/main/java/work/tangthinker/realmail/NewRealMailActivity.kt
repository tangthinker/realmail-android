package work.tangthinker.realmail

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.loper7.date_time_picker.dialog.CardDatePickerDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.tangthinker.realmail.databinding.ActivityNewRealMailBinding
import work.tangthinker.realmail.network.service.PersonalInfoService
import work.tangthinker.realmail.network.service.RealMailService
import work.tangthinker.realmail.utils.LoadingDialog
import work.tangthinker.realmail.utils.RetrofitServiceCreator
import work.tangthinker.realmail.utils.SharedPreferencesHelper
import work.tangthinker.realmail.utils.ToastCreator
import java.text.SimpleDateFormat
import java.util.*


class NewRealMailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewRealMailBinding

    private val handler : Handler = Handler(Looper.myLooper()!!)

    private val simpleDateFormat = SimpleDateFormat("yyyy年MM月dd日 HH:mm")

    private val realMailService = RetrofitServiceCreator.create<RealMailService>()

    private val personalInfoService = RetrofitServiceCreator.create<PersonalInfoService>()

    private lateinit var authInfoPreferencesHelper: SharedPreferencesHelper

    private lateinit var mailInfoPreferencesHelper: SharedPreferencesHelper

    private var receiveTime : Date = Date()

    // 访问标识 0仅双方可见 1仅自己可见 9公开可见
    private var accessFlag : Int = 0

    private var accessChoiceDialog : Dialog? = null

    private var isUserIdExists = false

    private var isHaveContent = false

    private lateinit var tokenPackage: SharedPreferencesHelper.TokenPackage

    private lateinit var toastCreator: ToastCreator

    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewRealMailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authInfoPreferencesHelper = SharedPreferencesHelper(getSharedPreferences("authInfo", MODE_PRIVATE))
        mailInfoPreferencesHelper = SharedPreferencesHelper(getSharedPreferences("mailInfo", MODE_PRIVATE))
        tokenPackage = authInfoPreferencesHelper.getTokenPackage()
        toastCreator = ToastCreator(this)
        loadingDialog = LoadingDialog(this, layoutInflater)

        val replyUserId : String? = intent.getStringExtra("reply_user_id")
        if (replyUserId != null){
            binding.etReceiveUserId.setText(replyUserId)
            checkUserIdAndInjectHeadIcon(replyUserId)
        }

        binding.etMailPayload.addTextChangedListener{
            isHaveContent = "" != it.toString().trim()
        }

        binding.etReceiveUserId.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                checkUserIdAndInjectHeadIcon(binding.etReceiveUserId.text.toString())
            }else {
                binding.etReceiveUserId.setTextColor(resources.getColor(R.color.black, null))
                binding.ivHeadIcon.setImageResource(R.color.white)
            }
        }

        binding.tvReceiveTimeSetting.text = simpleDateFormat.format(receiveTime)

        binding.rlAccessFlag.setOnClickListener {
            showAccessChoiceDialog()
        }

        binding.rlTimeSetting.setOnClickListener {
            showTimeSettingDialog()
        }

        binding.rlSendRealMail.setOnClickListener {

            //todo 信息检查
            if (!isHaveContent){
                toastCreator.emptyMail.show()
                return@setOnClickListener
            }
            if (!isUserIdExists && accessFlag == 0){
                toastCreator.idNotExist.show()
                return@setOnClickListener
            }

            loadingDialog.show()
            realMailService.sendMail(tokenPackage.uuid.toString(), tokenPackage.token.toString(),
                if (accessFlag == 0 ) binding.etReceiveUserId.text.toString() else null,
                this.accessFlag, binding.etMailPayload.text.toString(), simpleDateFormat.format(receiveTime))
                .enqueue(object : Callback<RealMailService.MailServiceResult>{
                override fun onResponse(call: Call<RealMailService.MailServiceResult>, response: Response<RealMailService.MailServiceResult>) {
                    val mailServiceResult = response.body()
                    mailServiceResult?.let {
                        println(mailServiceResult)
                        if(mailServiceResult.result == 1){
                            toastCreator.mailSendSuccessful.show()
                            loadingDialog.dismiss()
                            finish()
                            return
                        }
                    }
                    loadingDialog.dismiss()
                    toastCreator.mailSendFailed.show()
                }

                override fun onFailure(call: Call<RealMailService.MailServiceResult>, t: Throwable) {
                    loadingDialog.dismiss()
                    toastCreator.networkError.show()
                }

            })
        }



    }


    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.bottom_silent, R.anim.horizontal_close)
    }


    private fun checkUserIdAndInjectHeadIcon(userId: String){
        if ("" == userId){
            toastCreator.idIsEmpty.show()
            return
        }
        personalInfoService.checkUserIdAndGetHeadIcon(tokenPackage.uuid!!, tokenPackage.token!!, userId).enqueue(
            object : Callback<PersonalInfoService.CheckUserIdResult>{
                override fun onResponse(
                    call: Call<PersonalInfoService.CheckUserIdResult>,
                    response: Response<PersonalInfoService.CheckUserIdResult>
                ) {
                    val checkUserIdResult = response.body()
                    checkUserIdResult?.let {
                        println(checkUserIdResult)
                        if (checkUserIdResult.result == 1){
                            isUserIdExists = true
                            binding.etReceiveUserId.setTextColor(resources.getColor(R.color.black, null))
                            if ("defaultHeadIcon" != checkUserIdResult.head_icon){
                                val imageUrl = "${RetrofitServiceCreator.BASE_URL}${checkUserIdResult.head_icon}"
                                Glide.with(this@NewRealMailActivity).load(imageUrl).apply(RequestOptions.bitmapTransform(
                                    CircleCrop()
                                )).into(binding.ivHeadIcon)
                            }else{
                                binding.ivHeadIcon.setImageResource(R.drawable.default_head_icon)
                            }

                            return
                        }else {
                            isUserIdExists = false
                            binding.etReceiveUserId.setTextColor(resources.getColor(R.color.button_red, null))
//                                    binding.etReceiveUserId.paintFlags = (Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG)
                            binding.ivHeadIcon.setImageResource(R.color.white)
                        }
                    }
                    toastCreator.idNotExist.show()
                }

                override fun onFailure(
                    call: Call<PersonalInfoService.CheckUserIdResult>,
                    t: Throwable
                ) {
                    toastCreator.networkError.show()
                }

            }
        )
    }


    private fun showTimeSettingDialog() {
        CardDatePickerDialog.builder(this)
            .showBackNow(false)
            .showFocusDateInfo(false)
            .showDateLabel(false)
            .setBackGroundModel(CardDatePickerDialog.STACK)
            .setPickerLayout(R.layout.layout_date_picker_segmentation)
            .setTitle("送达时间")
            .setDefaultTime(receiveTime.time)
            .setOnChoose {millisecond->
                println(millisecond)
                receiveTime  = Date(millisecond)
                val formatDate = simpleDateFormat.format(receiveTime)
                println(formatDate)
                binding.tvReceiveTimeSetting.text = formatDate
            }.build().show()
    }

    private fun showAccessChoiceDialog() {
        val dialogView = layoutInflater.inflate(R.layout.layout_access_choice_dialog, null)
        accessChoiceDialog?.let {
            accessChoiceDialog!!.show()
        } ?: let {
            accessChoiceDialog = Dialog(this, R.style.BottomDialog)
            accessChoiceDialog!!.setContentView(dialogView)
            val layoutParams = dialogView.layoutParams
            layoutParams.width = resources.displayMetrics.widthPixels
            dialogView.layoutParams = layoutParams
            accessChoiceDialog!!.window?.setGravity(Gravity.BOTTOM)
            accessChoiceDialog!!.window?.setWindowAnimations(R.style.BottomDialog_Animation)
            val accessRadio = dialogView.findViewById<RadioGroup>(R.id.rg_access_flag)
            val privateRadioButton = dialogView.findViewById<RadioButton>(R.id.rbt_access_private)
            val publicRadioButton = dialogView.findViewById<RadioButton>(R.id.rbt_access_public)
            accessRadio.setOnCheckedChangeListener { _, checkedId ->
                when(checkedId){
                    R.id.rbt_access_private -> {
                        privateRadioButton.setTextColor(Color.WHITE)
                        publicRadioButton.setTextColor(Color.BLACK)
                        accessFlag = 0
                        binding.tvAccessFlagText.text = "私密"
                        binding.rlReceiveUserId.visibility = View.VISIBLE
                    }
                    R.id.rbt_access_public -> {
                        publicRadioButton.setTextColor(Color.WHITE)
                        privateRadioButton.setTextColor(Color.BLACK)
                        accessFlag = 9
                        binding.tvAccessFlagText.text = "公开"
                        binding.rlReceiveUserId.visibility = View.GONE
                    }
                }
                handler.postDelayed({
                    accessChoiceDialog!!.dismiss()
                }, 50)
            }
            accessChoiceDialog!!.show()
        }
    }

}