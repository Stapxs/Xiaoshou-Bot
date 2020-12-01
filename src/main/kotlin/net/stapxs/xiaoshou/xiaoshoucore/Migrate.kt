package net.stapxs.xiaoshou.xiaoshoucore

import net.mamoe.mirai.message.GroupMessageEvent
import net.stapxs.xiaoshou.features.Log
import net.stapxs.xiaoshou.xiaoshoucore.commands.*

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
                            // "wiki" -> WIKI.wikiMain()
                            "getpic" -> ImgGetter.DownloadASend(event, msg[0], "正在和图片服务器交涉……")
                            "say" -> Monitor.messageSender("Group", event.group.id, ((msg.toString()).substring(1, (msg.toString()).length - 1)).replace(", ", " "))
                            "help" -> sendHelp(event.group.id, msg[0])
                            "?" -> sendHelp(event.group.id, msg[0])
                        }
                    }
                    "." -> {
                        when (topCommand) {
                            "cat" -> ImgGetter.DownloadASend(event, "https://thiscatdoesnotexist.com", "正在给猫猫小鱼干……")
                            "dog" -> ImgGetter.DownloadASend(event, "https://dog.ceo/api/breeds/image/random", "正在给狗砸撒狗粮……")
                            "fox" -> ImgGetter.DownloadASend(event, "https://foxrudor.de", "正在偷偷靠近狐狸……")
                            "runInfo" -> RunInfo(event.group.id)
                            "林槐语录" -> {val ana = SSAna.getSSAna();Monitor.messageSender("Group", event.group.id, "> 林槐语录：${ana[1]}\n━━━━━━━━━━━━\n${ana[0]}\n━━━━━━━━━━━━")}
                            "lhyl" -> {val ana = SSAna.getSSAna();Monitor.messageSender("Group", event.group.id, "> 林槐语录：${ana[1]}\n━━━━━━━━━━━━\n${ana[0]}\n━━━━━━━━━━━━")}
                            "lhyl-a" -> {val ana = SSAna.getSSAna(true);Monitor.messageSender("Group", event.group.id, "> 林槐语录：${ana[1]}\n━━━━━━━━━━━━\n${ana[0]}\n━━━━━━━━━━━━")}
                        }
                    }
                    ":" -> {
                        when(topCommand) {
                            "林槐语录" -> Monitor.messageSender("Group", event.group.id, "> 林槐语录：\n━━━━━━━━━━━━\n${SSAna.getOneSSAna(msg[0])}\n━━━━━━━━━━━━")
                            "lhyl" -> Monitor.messageSender("Group", event.group.id, "> 林槐语录：\n━━━━━━━━━━━━\n${SSAna.getOneSSAna(msg[0])}\n━━━━━━━━━━━━")
                        }
                    }
                }
            }
        } else {
            OtherJump(event)
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
                }
                "." -> {
                    top = msg
                }
                ":" -> {
                    msgs.add(msg.split(":")[1])
                    top = msg.split(":")[0]
                }
                "&" -> {
                    top = "other"
                    msgs.add(msg)
                }
        }
        if(top != "") {
            if(top != "other") {
                Log.addLog("xiaoshou", "执行指令：$type => $top => $msgs")
            }
            jump(event, type, top, msgs)
        }
    }

}