package net.stapxs.xiaoshou.xiaoshoucore

import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.message.data.quote
import net.mamoe.mirai.message.sendImage
import net.stapxs.xiaoshou.features.Log
import net.stapxs.xiaoshou.features.Options
import java.io.File

object Monitor {

    lateinit var bot: Bot

    fun XiaoshouCore(miraiBot: Bot) {
        bot = miraiBot
        println("> 开始监听消息……")
        Log.addLog("xiaoshou", "你好人类，这是我插入在 Core Log 中的日志！如果有什么问题的话，可以看“xiaoshou”开头的日志哦")
        Log.addLog("xiaoshou", "开始监听消息……")
        var run = true;
        // 群组
        miraiBot.subscribeAlways<GroupMessageEvent> { event ->
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
                } catch (e: Throwable) { Log.addLog("xiaoshou", "处理 指令 : 出现错误 -> $e") }
                if(!get) {
                    Migrate.at(event, "&", msg)
                }
            }
        }

        // 全局
        miraiBot.subscribeAlways<MessageEvent> { event ->
            val msg = event.message.content
            if(msg.contains("休息啦") || msg.contains("干活啦")) {
                if (Options.getOpt("masterID") != "Err") {
                    if (msg.contains("休息啦") && event.sender.id == Options.getOpt("masterID").toLong()) {
                        miraiBot.friends[Options.getOpt("masterID").toLong()].sendMessage("好的 ——")
                        run = false;
                    }
                    if (msg.contains("干活啦") && event.sender.id == Options.getOpt("masterID").toLong()) {
                        miraiBot.friends[Options.getOpt("masterID").toLong()].sendMessage("好的 ——")
                        run = true;
                    }
                } else {
                    Log.addLog("xiaoshou", "没有找到主人 > Options Null master")
                }
            }
            // SS!
            try {
                if (event.message.content.substring(0, 2).equals("ss", true) ||
                    event.message.content.substring(0, 2) == "晓狩" ) {
                    if ((event.message.content[2] == '!' || event.message.content[2] == '！') &&
                        event.message.content.length == 3
                    ) {
                        reply(sender.nick + event.message.content[2])
                    }
                    else if (event.message.content[2] == '~' &&
                        event.message.content.length == 3
                    ) {
                        reply(sender.nick + event.message.content[2] + " 要抱抱 ——")
                    }
                }
            } catch (e: Throwable) { }
        }
    }

    /**
     * @Author Stapxs
     * @Description 发送消息，支持 At, Quote
     * @Date 下午 07:56 2020/11/23
     * @Param
     * @return
    **/
    suspend fun messageSender(type: String, id: Long, msg: String, at: Long = 0L, event: GroupMessageEvent? = null, doAt: Boolean = false) {
        if(type == "Group") {
            if(at == 0L && event == null) {
                bot.groups[id].sendMessage(msg)
                Log.addLog("xiaoshou", "$id (Group) <- \"${msg.replace("\n", " \\n ")}\"")
            }
            if(!doAt && event != null){
                bot.groups[id].sendMessage(event.message.quote() + msg)
                Log.addLog("xiaoshou", "$id (Group) <- [Quote - ${event.sender.id}] \"${msg.replace("\n", " \\n ")}\"")
            }
            if(doAt && at != 0L) {
                bot.groups[id].sendMessage(At(bot.groups[id].members[at]) + msg)
                Log.addLog("xiaoshou", "$id (Group) <- [At - $at] \"${msg.replace("\n", " \\n ")}\"")
            }
        } else {

        }
    }

    /**
     * @Author Stapxs
     * @Description 发送图片
     * @Date 下午 07:57 2020/11/23
     * @Param
     * @return
    **/
    suspend fun imangeSender(type: String, id: Long, img: File) {
        if(type == "Group") {
            Log.addLog("xiaoshou", "$id (Group) <- [Image]${img}")
            bot.groups[id].sendImage(img)
        } else {

        }
    }

}