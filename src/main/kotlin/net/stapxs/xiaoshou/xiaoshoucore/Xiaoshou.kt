package net.stapxs.xiaoshou.xiaoshoucore

import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.data.content
import net.stapxs.xiaoshou.features.Log
import net.stapxs.xiaoshou.features.Options
import java.io.File

object Xiaoshou {

    private lateinit var bot: Bot

    fun xiaoshouCore(miraiBot: Bot) {
        bot = miraiBot
        println("> 开始监听消息……")
        Log.addLog("xiaoshou", "你好人类，这是我插入在 Core Log 中的日志！如果有什么问题的话，可以看“xiaoshou”开头的日志哦")
        Log.addLog("xiaoshou", "开始监听消息……")
        var run = true
        // 群组
        miraiBot.eventChannel.subscribeAlways<GroupMessageEvent> { event ->
            if(run) {
                var get = false
                val msg = event.message.content
                when(msg.substring(0,1)) {
                    "/" -> {
                        get = true
                        Migrate.at(event,"/", msg.substring(1))
                    }
                    "." -> {
                        get = true
                        Migrate.at(event,".", msg.substring(1))
                    }
                    "。" -> {
                        get = true
                        Migrate.at(event,".", msg.substring(1))
                    }
                }
                try {
                    if (msg.indexOf(":") >= 0 && msg.indexOf("\n") < 0) {
                        get = true
                        Migrate.at(event, ":", msg)
                    }
                } catch (e: Throwable) { Log.addLog("xiaoshou", "Err > Xiaoshou.kt > fun XiaoshuoCore > 处理 指令 : 出现错误 -> $e") }
                if(!get) {
                    Migrate.at(event, "&", msg)
                }
            }
        }

        // 全局
        miraiBot.eventChannel.subscribeAlways<MessageEvent> { event ->
            val msg = event.message.content
            if(msg == "休息啦" || msg == "干活啦") {
                if (Options.getOpt("masterID") != "Err") {
                    if (msg.contains("休息啦") && event.sender.id == Options.getOpt("masterID").toLong()) {
                        subject.sendMessage("好的 ——")
                        run = false
                    }
                    if (msg.contains("干活啦") && event.sender.id == Options.getOpt("masterID").toLong()) {
                        subject.sendMessage("好的 ——")
                        run = true
                    }
                } else {
                    subject.sendMessage("Err > Xiaoshou.kt > fun XiaoshuoCore > Options Null master")
                    Log.addLog("xiaoshou", "Err > Xiaoshou.kt > fun XiaoshuoCore > Options Null master")
                }
            }
            // SS!
            try {
                if (event.message.content.substring(0, 2).equals("ss", true) ||
                    event.message.content.substring(0, 2) == "晓狩" ) {
                    if ((event.message.content[2] == '!' || event.message.content[2] == '！') &&
                        event.message.content.length == 3
                    ) {
                        subject.sendMessage(sender.nick + event.message.content[2])
                    }
                    else if (event.message.content[2] == '~' &&
                        event.message.content.length == 3
                    ) {
                        subject.sendMessage(sender.nick + event.message.content[2] + " 要抱抱 ——")
                    }
                }
            } catch (e: Throwable) { }
        }
    }

    /**
     * @Author Stapxs
     * @Description 发送消息，支持 At, Quote
     * @Date 下午 07:56 2020/11/23 (下午 02:11 2021/2/7)
     * @Param
     * @return
    **/
    suspend fun sendMessage(type: String, msg: String, event: GroupMessageEvent, doAt: Boolean = false, doQuote: Boolean = false) {
        if(type == "Group") {
            if(doAt) {
                event.group.sendMessage(At(event.sender) + msg)
                Log.addLog("xiaoshou", "${event.group.id} (Group) <- [Quote - ${event.sender.id}] \"$msg\"")
            } else if(doQuote) {
                event.group.sendMessage(event.message.quote() + msg)
                Log.addLog("xiaoshou", "${event.group.id} (Group) <- [Quote - ${event.sender.id}] \"$msg\"")
            } else {
                event.group.sendMessage(msg)
                Log.addLog("xiaoshou", "${event.group.id} (Group) <- \"$msg\"")
            }
        }
    }

    /**
     * @Author Stapxs
     * @Description 发送图片
     * @Date 下午 07:57 2020/11/23 (下午 02:27 2021/2/7)
     * @Param
     * @return
    **/
    suspend fun imangeSender(type: String, img: File, event: GroupMessageEvent) {
        if(type == "Group") {
            Log.addLog("xiaoshou", "${event.group.id} (Group) <- [Image]${img}")
            event.group.sendImage(img)
        }
    }

}