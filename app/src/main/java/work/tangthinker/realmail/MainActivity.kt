package work.tangthinker.realmail

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import androidx.core.view.isVisible
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.tangthinker.realmail.cache.CacheService
import work.tangthinker.realmail.cache.CacheServiceSharedPreferencesImp
import work.tangthinker.realmail.databinding.ActivityMainBinding
import work.tangthinker.realmail.list.adapter.MailCollectionAdapter
import work.tangthinker.realmail.list.adapter.MailListAdapter
import work.tangthinker.realmail.mail.item.MailItemActivity
import work.tangthinker.realmail.network.service.RealMailService
import work.tangthinker.realmail.network.service.RealMailService.MailWhitPersonalInfo
import work.tangthinker.realmail.utils.RetrofitServiceCreator
import work.tangthinker.realmail.utils.STOMPClient
import work.tangthinker.realmail.utils.SharedPreferencesHelper
import work.tangthinker.realmail.utils.ToastCreator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var selectRecording: Int = 0

    private val realMailService = RetrofitServiceCreator.create<RealMailService>()

    private lateinit var authPreferencesHelper: SharedPreferencesHelper
    private lateinit var toastCreator: ToastCreator
    private lateinit var tokenPackage: SharedPreferencesHelper.TokenPackage
    private lateinit var cacheService: CacheService
    private lateinit var stompClient: STOMPClient

    private val CACHE_PERSONAL_MAIL = "personal_mail_list"
    private val CACHE_PUBLIC_MAIL = "public_mail_list"
    private val CACHE_COLLECTION_MAIL = "collection_mail_list"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authPreferencesHelper = SharedPreferencesHelper(getSharedPreferences("authInfo", MODE_PRIVATE))
        toastCreator = ToastCreator(this)

        tokenPackage = authPreferencesHelper.getTokenPackage()

        stompClient = STOMPClient(tokenPackage.uuid!!, tokenPackage.token!!)

        cacheService = CacheServiceSharedPreferencesImp(this)


        if (cacheService.isCacheExists(CACHE_PERSONAL_MAIL)){
            setPersonalMailList(cacheService.getCache(CACHE_PERSONAL_MAIL, RealMailService.GetPersonalMailsResult::class.java))
        }

        refreshPersonalMail()


        binding.btnCategorySelect.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.layout_category_select, null)
            val popupWindow = PopupWindow(view,  700, ViewGroup.LayoutParams.WRAP_CONTENT)
            popWindowIcoAntiAlias(view)
            val ivPersonal = view.findViewById<View>(R.id.iv_personal_select)
            val ivPublic = view.findViewById<View>(R.id.iv_public_select)
            val ivStar = view.findViewById<View>(R.id.iv_star_select)

            when(selectRecording){
                0 -> ivPersonal.isVisible = true
                1 -> ivPublic.isVisible = true
                2 -> ivStar.isVisible = true
            }

            val personalSelect = view.findViewById<View>(R.id.rl_personal_select)
            val publicSelect = view.findViewById<View>(R.id.rl_public_select)
            val starSelect = view.findViewById<View>(R.id.rl_star_select)

            personalSelect.setOnClickListener {
                selectRecording = 0
                binding.tvTitle.text = "个人信件"

                if (cacheService.isCacheExists(CACHE_PERSONAL_MAIL)){
                    setPersonalMailList(cacheService.getCache(CACHE_PERSONAL_MAIL, RealMailService.GetPersonalMailsResult::class.java))
                }

                refreshPersonalMail()

                popupWindow.dismiss()
            }


            publicSelect.setOnClickListener {
                selectRecording = 1
                binding.tvTitle.text = "公开信件"
                println(tokenPackage)

                if (cacheService.isCacheExists(CACHE_PUBLIC_MAIL)){
                    setPublicMailList(cacheService.getCache(CACHE_PUBLIC_MAIL, RealMailService.GetPublicMailsResult::class.java))
                }

                refreshPublicMail()

                popupWindow.dismiss()
            }


            starSelect.setOnClickListener {
                selectRecording = 2
                binding.tvTitle.text = "加星信件"

                if (cacheService.isCacheExists(CACHE_COLLECTION_MAIL)){
                    setCollectionMailList(cacheService.getCache(CACHE_COLLECTION_MAIL, RealMailService.GetMailCollectionsResult::class.java))
                }

                refreshCollectMail()


                popupWindow.dismiss()
            }

            popupWindow.isFocusable = true
            popupWindow.isFocusable = true
            popupWindow.isOutsideTouchable = true
            popupWindow.showAsDropDown(binding.btnCategorySelect, -230, 5)

        }

        binding.btnSetting.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.setting_open, R.anim.bottom_silent)
        }

        binding.btnAddMail.setOnClickListener {
            val intent = Intent(this, NewRealMailActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.horizontal_open, R.anim.bottom_silent)
        }

        stompClient.init(object : STOMPClient.OnNewMessage{
            override fun onNewMessage(message: STOMPClient.Message) {
                when(message.messageType){
                    0 -> {
                        // 刷新公开信件
                        refreshPublicMail()
                    }
                    1 -> {
                        // 刷新个人信件
                        refreshPersonalMail()
                    }
                }
            }

        })



    }

    override fun onRestart() {
        super.onRestart()
        when (selectRecording){
            0 -> {
                refreshPersonalMail()
            }
            1 -> {
                refreshPublicMail()
            }
            2 -> {
                refreshCollectMail()
            }

        }



    }



    private fun popWindowIcoAntiAlias(view: View){
        view.findViewById<Button>(R.id.btn_1).paint.isAntiAlias = true
        view.findViewById<Button>(R.id.btn_2).paint.isAntiAlias = true
        view.findViewById<Button>(R.id.btn_3).paint.isAntiAlias = true
    }


    private fun fromResponseGetListOfMailWhitPersonalInfo(listOfRealMail: List<RealMailService.RealMail>, listOfPersonalInfo: List<RealMailService.PersonalInfo>): MutableList<MailWhitPersonalInfo> {
        var listOfMailWithPersonalInfo: MutableList<MailWhitPersonalInfo> = mutableListOf()
        for(i in listOfRealMail.indices){
            listOfMailWithPersonalInfo.add(MailWhitPersonalInfo(listOfRealMail[i], listOfPersonalInfo[i]))
        }
        return listOfMailWithPersonalInfo
    }

    private fun fromResponseGetListOfMailWhitPersonalInfoCollection(listOfRealMail: List<RealMailService.MailCollection>, listOfPersonalInfo: List<RealMailService.PersonalInfo>): MutableList<RealMailService.MailCollectionWhitPersonalInfo> {
        var listOfMailWithPersonalInfo: MutableList<RealMailService.MailCollectionWhitPersonalInfo> = mutableListOf()
        for(i in listOfRealMail.indices){
            listOfMailWithPersonalInfo.add(RealMailService.MailCollectionWhitPersonalInfo(listOfRealMail[i], listOfPersonalInfo[i]))
        }
        return listOfMailWithPersonalInfo
    }


    private fun setPersonalMailList(getMailsResult: RealMailService.GetPersonalMailsResult){
        val listOfMailWithPersonalInfo = fromResponseGetListOfMailWhitPersonalInfo(getMailsResult.personal_mail, getMailsResult.personal_info)
        if (getMailsResult.response_size != 0){
            binding.llEmptyTip.visibility = View.GONE
        }else{
            binding.llEmptyTip.visibility = View.VISIBLE
        }
        binding.lvMails.adapter = MailListAdapter(this@MainActivity, R.layout.layout_mail_item, listOfMailWithPersonalInfo)
        binding.lvMails.divider = null
        binding.lvMails.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this@MainActivity, MailItemActivity::class.java)
            intent.putExtra("list_of_mails", listOfMailWithPersonalInfo[position])
            intent.putExtra("type_flag", selectRecording)
            startActivity(intent)
            overridePendingTransition(R.anim.horizontal_open, R.anim.bottom_silent)
        }
    }

    private fun setPublicMailList(getMailsResult: RealMailService.GetPublicMailsResult){
        val listOfMailWithPersonalInfo = fromResponseGetListOfMailWhitPersonalInfo(getMailsResult.public_mail, getMailsResult.personal_info)
        if (getMailsResult.response_size != 0){
            binding.llEmptyTip.visibility = View.GONE
        }else{
            binding.llEmptyTip.visibility = View.VISIBLE
        }
        binding.lvMails.adapter = MailListAdapter(this@MainActivity, R.layout.layout_mail_item, listOfMailWithPersonalInfo)
        binding.lvMails.divider = null
        binding.lvMails.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this@MainActivity, MailItemActivity::class.java)
            intent.putExtra("list_of_mails", listOfMailWithPersonalInfo[position])
            intent.putExtra("type_flag", selectRecording)
            startActivity(intent)
            overridePendingTransition(R.anim.horizontal_open, R.anim.bottom_silent)
        }
    }

    private fun setCollectionMailList(getMailCollectionsResult: RealMailService.GetMailCollectionsResult){
        val listOfMailWhitPersonalInfoCollection = fromResponseGetListOfMailWhitPersonalInfoCollection(getMailCollectionsResult.mail_collections, getMailCollectionsResult.personal_info)
        if (getMailCollectionsResult.response_size != 0){
            binding.llEmptyTip.visibility = View.GONE
        }else{
            binding.llEmptyTip.visibility = View.VISIBLE
        }
        binding.lvMails.adapter = MailCollectionAdapter(this@MainActivity, R.layout.layout_mail_item, listOfMailWhitPersonalInfoCollection)
        binding.lvMails.divider = null
        binding.lvMails.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this@MainActivity, MailItemActivity::class.java)
            intent.putExtra("list_of_collections", listOfMailWhitPersonalInfoCollection[position])
            intent.putExtra("type_flag", selectRecording)
            startActivity(intent)
            overridePendingTransition(R.anim.horizontal_open, R.anim.bottom_silent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stompClient.disconnect()
    }

    private fun refreshPersonalMail(){
        realMailService.getPersonalMail(tokenPackage.uuid.toString(), tokenPackage.token.toString()).enqueue(object : Callback<RealMailService.GetPersonalMailsResult>{
            override fun onResponse(call: Call<RealMailService.GetPersonalMailsResult>, response: Response<RealMailService.GetPersonalMailsResult>) {
                val getMailsResult = response.body()
                getMailsResult?.let {
//                    println(getMailsResult)
                    setPersonalMailList(getMailsResult)
                    cacheService.saveCache(CACHE_PERSONAL_MAIL, getMailsResult)
                }
            }

            override fun onFailure(call: Call<RealMailService.GetPersonalMailsResult>, t: Throwable) {
                t.printStackTrace()
                toastCreator.networkError.show()
            }

        })
    }

    private fun refreshPublicMail(){
        realMailService.getPublicMail(tokenPackage.uuid.toString(), tokenPackage.token.toString()).enqueue(object : Callback<RealMailService.GetPublicMailsResult>{
            override fun onResponse(call: Call<RealMailService.GetPublicMailsResult>, response: Response<RealMailService.GetPublicMailsResult>) {
                val getMailsResult = response.body()
                getMailsResult?.let {
//                    println(getMailsResult)
                    setPublicMailList(getMailsResult)
                    cacheService.saveCache(CACHE_PUBLIC_MAIL, getMailsResult)
                }
            }

            override fun onFailure(call: Call<RealMailService.GetPublicMailsResult>, t: Throwable) {
                t.printStackTrace()
                toastCreator.networkError.show()
            }

        })
    }

    private fun refreshCollectMail(){
        realMailService.getAllCollections(tokenPackage.uuid.toString(), tokenPackage.token.toString()).enqueue(object : Callback<RealMailService.GetMailCollectionsResult> {
            override fun onResponse(call: Call<RealMailService.GetMailCollectionsResult>, response: Response<RealMailService.GetMailCollectionsResult>) {
                val getMailsResult = response.body()
                getMailsResult?.let {
//                    println(getMailsResult)
                    setCollectionMailList(getMailsResult)
                    cacheService.saveCache(CACHE_COLLECTION_MAIL, getMailsResult)
                }
            }

            override fun onFailure(
                call: Call<RealMailService.GetMailCollectionsResult>,
                t: Throwable
            ) {
                t.printStackTrace()
                toastCreator.networkError.show()
            }

        })
    }

}