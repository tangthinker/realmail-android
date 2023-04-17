package work.tangthinker.realmail.utils

import android.util.Log
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import org.java_websocket.WebSocket
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader
import ua.naiksoftware.stomp.dto.StompMessage


class STOMPClient(private val uuid: String, private val token: String) {

    private lateinit var stompClient : StompClient

    private val WEBSOCKET_URL = "${RetrofitServiceCreator.BASE_URL}/websocketServer/websocket"

    private val USER_TOPIC = "/user/${uuid}/refresh_personal_notice"
    private val PUBLIC_TOPIC = "/public/refresh_public_notice"

    private var errorFlag = false

    private var reconnectionNum = 0

    private var compositeDisposable: CompositeDisposable? = null

    private var gson = Gson()


    fun init(onNewMessage: OnNewMessage){
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WEBSOCKET_URL)
        resetSubscriptions()
        connectStomp(onNewMessage)
    }


    private fun resetSubscriptions(){
        compositeDisposable?.dispose()
        compositeDisposable = CompositeDisposable()
    }


    private fun connectStomp(onNewMessage: OnNewMessage){
        val headers: MutableList<StompHeader> = ArrayList()
        headers.add(StompHeader("uuid", uuid))
        headers.add(StompHeader("token", token))
        stompClient.withClientHeartbeat(20000).withServerHeartbeat(0)
        resetSubscriptions()
        //监听lifecycleEvent的回调状态
        val dispLifecycle = stompClient.lifecycle()
            .doOnError { throwable: Throwable ->  println("connect error! ${throwable.message}")
                throwable.printStackTrace() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ lifecycleEvent: LifecycleEvent ->
                when (lifecycleEvent.type) {
                    LifecycleEvent.Type.OPENED -> println("Stomp connection opened")
                    LifecycleEvent.Type.ERROR -> {
                        errorFlag = true
                        println("Stomp connection error${lifecycleEvent.exception}")
                        lifecycleEvent.exception.printStackTrace()
                    }
                    LifecycleEvent.Type.CLOSED -> {
                        println("Stomp connection closed")
                        resetSubscriptions()
                        reconnectionNum++
                        if (errorFlag && reconnectionNum < 11) {
                            println("连接异常断开，第${reconnectionNum}次自动重连")
                            connectStomp(onNewMessage)
                        }
                    }
                    LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> println("Stomp failed server heartbeat")
                }
            }, { throwable: Throwable? -> println("stomp连接时subscribe发生异常：${throwable?.message}") })
        compositeDisposable?.add(dispLifecycle)

        // Receive greetings
        val dispTopic = stompClient.topic(USER_TOPIC)
            .doOnError { throwable: Throwable -> println("订阅异常：$throwable.message") }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ topicMessage: StompMessage ->
                val payloadStr = topicMessage.payload
                println("接收到核查消息： $payloadStr")
                if (payloadStr.isNullOrEmpty()) {
                    println("推送的数据为空")
                } else {
                    onNewMessage.onNewMessage(gson.fromJson(payloadStr, Message::class.java))
                }
            }, { throwable: Throwable? -> println("stomp订阅时subscribe发生异常：${throwable?.message}") })

        val dispPublicTopic = stompClient.topic(PUBLIC_TOPIC)
            .doOnError { throwable: Throwable -> println("订阅异常：$throwable.message") }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ topicMessage: StompMessage ->
                val payloadStr = topicMessage.payload
                println("接收到核查消息： $payloadStr")
                if (payloadStr.isNullOrEmpty()) {
                    println("推送的数据为空")
                } else {
                    onNewMessage.onNewMessage(gson.fromJson(payloadStr, Message::class.java))
                }
            }, { throwable: Throwable? -> println("stomp订阅时subscribe发生异常：${throwable?.message}") })

        compositeDisposable?.add(dispTopic)
        compositeDisposable?.add(dispPublicTopic)

        //开始连接
        stompClient.connect(headers)
    }


    fun disconnect(){
        stompClient.disconnect()
        compositeDisposable?.dispose()
    }

    data class Message(val messageType: Int)


    interface OnNewMessage{
        fun onNewMessage(message: Message)
    }









}