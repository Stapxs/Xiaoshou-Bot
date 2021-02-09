package net.stapxs.xiaoshou.xiaoshoucore

import net.mamoe.mirai.event.events.GroupMessageEvent
import net.stapxs.xiaoshou.xiaoshoucore.commands.CommandList

suspend fun sendHelp(event: GroupMessageEvent, command: String) {
    if(command != "all") {
        for (com in CommandList.comList) {
            if (com.comName == command) {
                if (com.comDescription != "") {
                    var out = "";
                    out = if(com.comDescription.indexOf("-") > 0) {
                        val use = com.comDescription.split("-")
                            "> 指令 - ${com.comType} - ${com.comName}\n${use[0]}\n" +
                            "━━━━━━━━━━━━\n" +
                            "*例子*\n" +
                            "${use[1]}\n" +
                            "━━━━━━━━━━━━"
                    } else {
                            "> 指令 - ${com.comType} - ${com.comName}\n" +
                            "━━━━━━━━━━━━\n" +
                            "${com.comDescription}\n" +
                            "━━━━━━━━━━━━"
                    }
                    Xiaoshou.sendMessage("Group", out, event)
                    return
                } else {
                    Xiaoshou.sendMessage("Group", "这个指令没有提示欸", event)
                    return
                }
            }
        }
        Xiaoshou.sendMessage("Group", "这是啥指令？没见过……", event)
        return
    } else {

    }
}