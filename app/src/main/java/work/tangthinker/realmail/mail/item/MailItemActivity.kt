package work.tangthinker.realmail.mail.item

import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.tangthinker.realmail.NewRealMailActivity
import work.tangthinker.realmail.OtherPersonalSpaceActivity
import work.tangthinker.realmail.R
import work.tangthinker.realmail.databinding.ActivityMailItemPersonalBinding
import work.tangthinker.realmail.list.adapter.CommentListAdapter
import work.tangthinker.realmail.network.service.CollectionService
import work.tangthinker.realmail.network.service.CommentService
import work.tangthinker.realmail.network.service.RealMailService
import work.tangthinker.realmail.network.service.UserAuthService
import work.tangthinker.realmail.utils.*
import kotlin.math.sin

class MailItemActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMailItemPersonalBinding

    private lateinit var preferencesHelper: SharedPreferencesHelper

    private val commentService = RetrofitServiceCreator.create<CommentService>()

    private val collectionService = RetrofitServiceCreator.create<CollectionService>()

    private val realMailService = RetrofitServiceCreator.create<RealMailService>()

    private lateinit var loadingDialog: LoadingDialog

    private lateinit var commentDialog: InputDialog

    private lateinit var replyDialog: InputDialog

    private var listOfCommentOrReply : MutableList<CommentService.CommentOrReply>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMailItemPersonalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var mailWithPersonalInfo : RealMailService.MailWhitPersonalInfo? = null
        var mailCollectionWhitPersonalInfo: RealMailService.MailCollectionWhitPersonalInfo? =null
        preferencesHelper = SharedPreferencesHelper(getSharedPreferences("authInfo", MODE_PRIVATE))
        loadingDialog = LoadingDialog(this, layoutInflater)

        val tokenPackage = preferencesHelper.getTokenPackage()
        val toastCreator = ToastCreator(this)
        val typeFlag = intent.getIntExtra("type_flag", 0)
        commentDialog = InputDialog("我说一句", this, layoutInflater)
        replyDialog = InputDialog("回复一下", this, layoutInflater)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (typeFlag != 2){
                mailWithPersonalInfo = intent.getSerializableExtra("list_of_mails", RealMailService.MailWhitPersonalInfo::class.java)!!
            }else {
                mailCollectionWhitPersonalInfo = intent.getSerializableExtra("list_of_collections", RealMailService.MailCollectionWhitPersonalInfo::class.java)!!
            }
        }else {
            if (typeFlag != 2){
                mailWithPersonalInfo = intent.getSerializableExtra("list_of_mails") as RealMailService.MailWhitPersonalInfo
            }else {
                mailCollectionWhitPersonalInfo = intent.getSerializableExtra("list_of_collections") as RealMailService.MailCollectionWhitPersonalInfo
            }
        }



        binding.rlCollectOrDelete.setOnClickListener {
            loadingDialog.show()
            collectionService.run {
                addCollection(tokenPackage.uuid!!, tokenPackage.token!!, mailWithPersonalInfo?.realMail!!.mailId).enqueue(
                        object : Callback<UserAuthService.SingleResult>{
                            override fun onResponse(
                                call: Call<UserAuthService.SingleResult>,
                                response: Response<UserAuthService.SingleResult>
                            ) {
                                loadingDialog.dismiss()
                                val singleResult = response.body()
                                if (singleResult != null) {
                                    if(singleResult.result == 1){
                                        toastCreator.collectSuccessful.show()
                                        return
                                    }
                                }
                                toastCreator.collectAlreadyExists.show()
                            }

                            override fun onFailure(call: Call<UserAuthService.SingleResult>, t: Throwable) {
                                toastCreator.networkError.show()
                                loadingDialog.dismiss()
                            }

                        }
                    )
            }
        }
        if (typeFlag != 2 ){
            binding.rlCollectOrDelete.setOnClickListener {
                loadingDialog.show()
                collectionService.run {
                    addCollection(tokenPackage.uuid!!, tokenPackage.token!!, mailWithPersonalInfo?.realMail!!.mailId).enqueue(
                        object : Callback<UserAuthService.SingleResult>{
                            override fun onResponse(
                                call: Call<UserAuthService.SingleResult>,
                                response: Response<UserAuthService.SingleResult>
                            ) {
                                loadingDialog.dismiss()
                                val singleResult = response.body()
                                if (singleResult != null) {
                                    if(singleResult.result == 1){
                                        toastCreator.collectSuccessful.show()
                                        return
                                    }
                                }
                                toastCreator.collectAlreadyExists.show()
                            }

                            override fun onFailure(call: Call<UserAuthService.SingleResult>, t: Throwable) {
                                toastCreator.networkError.show()
                                loadingDialog.dismiss()
                            }

                        }
                    )
                }
            }
        }




        fun getComments(){
            commentService.getCommentsWithReplies(tokenPackage.uuid.toString(), tokenPackage.token.toString(), mailWithPersonalInfo?.realMail!!.mailId)
                .enqueue(object : Callback<CommentService.GetCommentsWithRepliesResult>{
                    override fun onResponse(
                        call: Call<CommentService.GetCommentsWithRepliesResult>,
                        response: Response<CommentService.GetCommentsWithRepliesResult>
                    ) {
                        println(response.body())
                        val getCommentsWithRepliesResult = response.body()
                        if (getCommentsWithRepliesResult != null) {
                            listOfCommentOrReply = fromCommentsWithRepliesGetListOfCommentOrReply(getCommentsWithRepliesResult.comments_with_replies)
                            if (getCommentsWithRepliesResult.response_size != 0){
                                binding.lvComments.adapter = CommentListAdapter(this@MailItemActivity, R.layout.layout_comment_item,
                                    listOfCommentOrReply!!)
                                binding.lvComments.divider = null
                                binding.rlNoCommentFlag.visibility = View.GONE
                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<CommentService.GetCommentsWithRepliesResult>,
                        t: Throwable
                    ) {
                    }

                })
        }

        binding.lvComments.setOnItemClickListener { parent, view, position, id ->
            println(listOfCommentOrReply!![position])
            var replyTo: String = if (listOfCommentOrReply!![position].isReply == 1){
                listOfCommentOrReply!![position].attachId
            }else{
                listOfCommentOrReply!![position].commentId
            }
            replyDialog.show(object : InputDialog.OnConfirmClick{
                override fun onConfirmClick(inputData: String) {
                    if("" == inputData.trim()){
                        toastCreator.inputDataInvalid.show()
                        return
                    }
                    commentService.addReply(tokenPackage.uuid!!, tokenPackage.token!!, replyTo, inputData).enqueue(
                        object : Callback<UserAuthService.SingleResult>{
                            override fun onResponse(
                                call: Call<UserAuthService.SingleResult>,
                                response: Response<UserAuthService.SingleResult>
                            ) {
                                val singleResult = response.body()
                                singleResult?.let {
                                    if (singleResult.result == 1){
                                        replyDialog.dismiss()
                                        toastCreator.replySuccessful.show()
                                        getComments()
                                        return
                                    }
                                }
                                toastCreator.replyFailed.show()
                                replyDialog.dismiss()
                            }

                            override fun onFailure(
                                call: Call<UserAuthService.SingleResult>,
                                t: Throwable
                            ) {
                                toastCreator.networkError.show()
                                replyDialog.dismiss()
                            }
                        }
                    )
                }
            })
            replyDialog.setTitle("回复${listOfCommentOrReply!![position].sendUserId}")
        }

        when (typeFlag){
            0 -> {
                //个人邮件
                mailWithPersonalInfo!!
                binding.tvReceiverUserId.text = mailWithPersonalInfo.realMail.receiveUserId
                binding.rlComments.visibility = View.GONE
                binding.rlDeleteMail.visibility = View.VISIBLE
            }
            1 -> {
                //公开邮件
                mailWithPersonalInfo!!
                binding.rlReplyMail.visibility = View.GONE
                binding.tvReceiverUserId.visibility = View.GONE
                binding.tvFlagReceiverUserId.text = "公开信件"
                binding.rlDeleteMail.visibility = View.GONE
                getComments()

                binding.btnAddComment.setOnClickListener {
                    commentDialog.show(object : InputDialog.OnConfirmClick{
                        override fun onConfirmClick(inputData: String) {
                            if ("" == inputData.trim()){
                                toastCreator.inputDataInvalid.show()
                                return
                            }
                            commentService.addComment(tokenPackage.uuid!!, tokenPackage.token!!, mailWithPersonalInfo.realMail.mailId, inputData).enqueue(
                                object : Callback<UserAuthService.SingleResult>{
                                    override fun onResponse(
                                        call: Call<UserAuthService.SingleResult>,
                                        response: Response<UserAuthService.SingleResult>
                                    ) {
                                        val singleResult = response.body()
                                        singleResult?.let {
                                            if (singleResult.result == 1){
                                                commentDialog.dismiss()
                                                toastCreator.commentSuccessful.show()
                                                getComments()
                                                return
                                            }
                                        }
                                        toastCreator.commentFailed.show()
                                        commentDialog.dismiss()
                                    }

                                    override fun onFailure(
                                        call: Call<UserAuthService.SingleResult>,
                                        t: Throwable
                                    ) {
                                        toastCreator.networkError.show()
                                        commentDialog.dismiss()
                                    }

                                }
                            )
                        }
                    })
                    commentDialog.setTitle("我也说一句")
                }
            }
            2 -> {
                //加星邮件
                mailCollectionWhitPersonalInfo!!
                binding.rlComments.visibility = View.GONE
                binding.rlReplyMail.visibility = View.GONE
                binding.rlDeleteMail.visibility = View.GONE
                binding.tvTextCollectOrDelete.text = "删除"
                binding.rlCollectOrDelete.setBackgroundResource(R.drawable.btn_red_small)
                binding.ivIconCollectDelete.setBackgroundResource(R.drawable.delete_icon)
                binding.rlCollectOrDelete.setOnClickListener {
                    loadingDialog.show()
                    collectionService.removeCollection(tokenPackage.uuid!!, tokenPackage.token!!, mailCollectionWhitPersonalInfo.mailCollection.collectionId)
                        .enqueue(object : Callback<UserAuthService.SingleResult>{
                            override fun onResponse(
                                call: Call<UserAuthService.SingleResult>,
                                response: Response<UserAuthService.SingleResult>
                            ) {
                                loadingDialog.dismiss()
                                val singleResult = response.body()
                                if (singleResult != null) {
                                    if(singleResult.result == 1){
                                        finish()
                                        toastCreator.removeSuccessful.show()
                                        return
                                    }
                                }
                                toastCreator.removeFailed.show()
                            }

                            override fun onFailure(call: Call<UserAuthService.SingleResult>, t: Throwable) {
                                toastCreator.networkError.show()
                                loadingDialog.dismiss()
                            }


                        })
                }

            }
        }

        if (typeFlag == 0){
            binding.rlDeleteMail.setOnClickListener {
                loadingDialog.show()
                realMailService.setWasReceived(tokenPackage.uuid!!, tokenPackage.token!!, mailWithPersonalInfo!!.realMail.mailId)
                    .enqueue(object : Callback<UserAuthService.SingleResult>{
                        override fun onResponse(
                            call: Call<UserAuthService.SingleResult>,
                            response: Response<UserAuthService.SingleResult>
                        ) {
                            val singleResult = response.body()
                            singleResult?.let {
                                if(singleResult.result == 1){
                                    toastCreator.removeSuccessful.show()
                                    loadingDialog.dismiss()
                                    finish()
                                    return
                                }
                            }
                            loadingDialog.dismiss()
                            toastCreator.removeFailed.show()
                        }

                        override fun onFailure(
                            call: Call<UserAuthService.SingleResult>,
                            t: Throwable
                        ) {
                            toastCreator.networkError.show()
                            loadingDialog.dismiss()
                        }

                    })
            }
        }

        if (typeFlag != 2){
            mailWithPersonalInfo!!
            binding.tvMailPayload.text = mailWithPersonalInfo.realMail.mailPayload
            binding.tvNeckName.text = mailWithPersonalInfo.personalInfo.neckName
            binding.tvMailTime.text = mailWithPersonalInfo.realMail.receiveTime
            binding.tvMailPublicId.text = mailWithPersonalInfo.realMail.publicId
            binding.tvUserId.text = mailWithPersonalInfo.realMail.sendUserId
            if ("defaultHeadIcon" != mailWithPersonalInfo.personalInfo.headIcon){
                val imageUrl = "${RetrofitServiceCreator.BASE_URL}${mailWithPersonalInfo.personalInfo.headIcon}"
                Glide.with(this@MailItemActivity).load(imageUrl).apply(RequestOptions.bitmapTransform(
                    RoundedCorners(20)
                )).into(binding.ivHeadIcon)
            }
            binding.rlIntoPersonalSpace.setOnClickListener {
                val intent = Intent(this@MailItemActivity, OtherPersonalSpaceActivity::class.java)
                intent.putExtra("personalInfo", mailWithPersonalInfo.personalInfo)
                intent.putExtra("userId", mailWithPersonalInfo.realMail.sendUserId)
                intent.putExtra("enterType", 0)
                startActivity(intent)
                overridePendingTransition(R.anim.horizontal_open, R.anim.bottom_silent)
            }
            binding.rlReplyMail.setOnClickListener {
                val intent = Intent(this@MailItemActivity, NewRealMailActivity::class.java)
                intent.putExtra("reply_user_id", mailWithPersonalInfo.realMail.sendUserId)
                startActivity(intent)
                overridePendingTransition(R.anim.horizontal_open, R.anim.bottom_silent)
            }
        }else {
            mailCollectionWhitPersonalInfo!!
            binding.tvMailPayload.text = mailCollectionWhitPersonalInfo.mailCollection.mailPayload
            binding.tvNeckName.text = mailCollectionWhitPersonalInfo.personalInfo.neckName
            binding.tvMailTime.text = mailCollectionWhitPersonalInfo.mailCollection.receiveTime
            binding.tvMailPublicId.text = mailCollectionWhitPersonalInfo.mailCollection.publicId
            binding.tvUserId.text = mailCollectionWhitPersonalInfo.mailCollection.sendUserId
            if ("defaultHeadIcon" != mailCollectionWhitPersonalInfo.personalInfo.headIcon){
                val imageUrl = "${RetrofitServiceCreator.BASE_URL}${mailCollectionWhitPersonalInfo.personalInfo.headIcon}"
                Glide.with(this@MailItemActivity).load(imageUrl).apply(RequestOptions.bitmapTransform(
                    RoundedCorners(20)
                )).into(binding.ivHeadIcon)
            }
            binding.rlIntoPersonalSpace.setOnClickListener {
                val intent = Intent(this@MailItemActivity, OtherPersonalSpaceActivity::class.java)
                intent.putExtra("personalInfo", mailCollectionWhitPersonalInfo.personalInfo)
                intent.putExtra("userId", mailCollectionWhitPersonalInfo.mailCollection.sendUserId)
                intent.putExtra("enterType", 0)
                startActivity(intent)
                overridePendingTransition(R.anim.horizontal_open, R.anim.bottom_silent)
            }
        }






        binding.btnGoBack.setOnClickListener {
            finish()
        }








    }


    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.bottom_silent, R.anim.horizontal_close)
    }


    fun fromCommentsWithRepliesGetListOfCommentOrReply(listOfCommentWithReplies: List<CommentService.CommentWithReplies>): MutableList<CommentService.CommentOrReply> {
        val listOfCommentOrReply = mutableListOf<CommentService.CommentOrReply>()
        for (commentWithReply in listOfCommentWithReplies) {
            listOfCommentOrReply.add(CommentService.CommentOrReply(commentWithReply.comment))
            var repliedUserId = commentWithReply.comment.sendUserId
            for (reply in commentWithReply.replies) {
                listOfCommentOrReply.add(CommentService.CommentOrReply(reply, repliedUserId))
                repliedUserId = reply.sendUserId
            }
        }
        return listOfCommentOrReply
    }



}