package net.stapxs.xiaoshou.xiaoshoucore

import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.content
import net.stapxs.xiaoshou.features.Log
import net.stapxs.xiaoshou.features.Options

object monitor {

    fun XiaoshouCore(miraiBot: Bot) {
        println("> 开始监听消息……")
        Log.addLog("xiaoshou", "开始监听消息……")
        var run = true;
        // 群组
        miraiBot.subscribeAlways<GroupMessageEvent> { event ->
            if(run) {
                val msg = event.message.content
                when(msg.substring(0,1)) {
                    "/" -> {
                        migrater.at("/", msg.substring(1))
                    }
                    "." -> {
                        migrater.at(".", msg.substring(1))
                    }
                    "。" -> {
                        migrater.at(".", msg.substring(1))
                    }
                }
                if(msg.indexOf(":") >= 0) {
                    migrater.at(":", msg.substring(0, msg.indexOf(":"))
                            + "|" + msg.substring(msg.indexOf(":" + 1)))
                }
                migrater.at("&", msg)
            }
        }

        // 全局
        miraiBot.subscribeAlways<MessageEvent> { event ->
            if(Options.getOpt("masterID").indexOf("Err") == 0) {
                if (event.message.content.contains("休息啦") && event.sender.id == Options.getOpt("masterID").toLong()) {
                    miraiBot.friends[Options.getOpt("masterID").toLong()].sendMessage("好的 ——")
                    run = false;
                }
                if (event.message.content.contains("干活啦") && event.sender.id == Options.getOpt("masterID").toLong()) {
                    miraiBot.friends[Options.getOpt("masterID").toLong()].sendMessage("好的 ——")
                    run = true;
                }
            } else {
                Log.addLog("xiaoshou", "没有找到主人 > Options null master")
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
                }
            } catch (e: Throwable) { }
        }
    }

}