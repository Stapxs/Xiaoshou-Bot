package net.stapxs.xiaoshou.xiaoshoucore

import net.mamoe.mirai.message.GroupMessageEvent
import net.stapxs.xiaoshou.features.Log
import net.stapxs.xiaoshou.xiaoshoucore.commands.CommandList
import net.stapxs.xiaoshou.xiaoshoucore.commands.ImgGetter
import net.stapxs.xiaoshou.xiaoshoucore.commands.RunInfo
import net.stapxs.xiaoshou.xiaoshoucore.commands.WIKI

object Migrate {

    /**
     * @Author Stapxs
     * @Description 函数跳转列表
     * @Date 下午 05:05 2020/11/23
     * @Param
     * @return
    **/
    private suspend fun jump(event: GroupMessageEvent, type: String, topCommand: String, msg: MutableList<String>) {
        if(type != "&") {
            if (CommandList.hasAuthority(type, topCommand, event.group.id)) {
                when (type) {
                    "/" -> {
                        when (topCommand) {
                            "wiki" -> WIKI.wikiMain()
                            "getpic" -> ImgGetter.DownloadASend(event, msg[0], "正在和图片服务器交涉……")
                            "say" -> Monitor.messageSender("Group", event.group.id, msg[0], event.sender.id, event)
                        }
                    }
                    "." -> {
                        when (topCommand) {
                            "cat" -> ImgGetter.DownloadASend(event, "https://thiscatdoesnotexist.com", "正在给猫猫小鱼干……")
                            "dog" -> ImgGetter.DownloadASend(event, "https://dog.ceo/api/breeds/image/random", "正在给狗砸撒狗粮……")
                            "fox" -> ImgGetter.DownloadASend(event, "https://foxrudor.de", "正在偷偷靠近狐狸……")
                            "runInfo" -> RunInfo(event.group.id)
                        }
                    }
                    ":" -> {

                    }
                }
            }
        } else {

        }
    }

    /**
     * @Author Stapxs
     * @Description 消息拆封
     * @Date 下午 05:05 2020/11/23
     * @Param
     * @return
    **/
    suspend fun at(event: GroupMessageEvent, type: String, msg: String) {
        var top = ""
        var msgs = mutableListOf<String>()
        when(type) {
                "/" -> {
                    top = msg.substring(0, msg.indexOf(" "))
                    val other = msg.substring(msg.indexOf(" ") + 1)
                    msgs = other.split(" ") as MutableList<String>
                    Log.addLog("xiaoshou", "执行 $type$top > $other")
                }
                "." -> {
                    top = msg
                    Log.addLog("xiaoshou", "执行 $type$msg")
                }
                ":" -> {
                    Log.addLog("xiaoshou", "执行 $type 指令 > $msg")
                    return
                }
        }
        if(top != "") {
            jump(event, type, top, msgs)
        }
    }

}