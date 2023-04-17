package work.tangthinker.realmail.utils

import android.content.Context
import android.widget.Toast

class ToastCreator(private val context: Context) {


    fun shortToastCreator(message: String): Toast {
        return Toast.makeText(context, message, Toast.LENGTH_SHORT)!!
    }

    val idOccupied = shortToastCreator( "该RealMail ID已被占用！")
    val idNotExist = shortToastCreator( "该RealMail ID不存在！")
    val idIsEmpty = shortToastCreator( "收信人RealMail ID不能为空！")
    val idNotOccupied = shortToastCreator("恭喜您！该RealMail ID可用！")
    val verifyCodeError = shortToastCreator("验证码错误！")
    val verifyCodeSent = shortToastCreator("验证码发送成功！")
    val networkError = shortToastCreator("网络连接失败！")
    val registerSuccessful = shortToastCreator("RealMail ID注册成功！")
    val modifyPasswordSuccessful = shortToastCreator("密码修改成功！")
    val infoOrVerifyCodeError = shortToastCreator("输入信息或验证码错误！")
    val mailSendSuccessful = shortToastCreator("邮件已投递！")
    val mailSendFailed = shortToastCreator("邮件已投递失败！")
    val collectSuccessful = shortToastCreator("邮件收藏成功！")
    val collectFailed = shortToastCreator("邮件收藏失败！")
    val collectAlreadyExists = shortToastCreator("邮件已收藏！")
    val removeSuccessful = shortToastCreator("删除成功！")
    val removeFailed = shortToastCreator("删除失败！")
    val modifySuccessful = shortToastCreator("修改成功！")
    val modifyFailed = shortToastCreator("修改失败！")
    val inputDataInvalid = shortToastCreator("请输入有效信息！")
    val commentSuccessful = shortToastCreator("评论成功！")
    val commentFailed = shortToastCreator("评论失败！")
    val replySuccessful = shortToastCreator("回复成功！")
    val replyFailed = shortToastCreator("回复失败！")
    val addSuccessful = shortToastCreator("添加成功！")
    val addFailed = shortToastCreator("添加失败！")
    val bookAlreadyExists = shortToastCreator("邮友已存在！")
    val emptyMail = shortToastCreator("信件内容为空！")





}