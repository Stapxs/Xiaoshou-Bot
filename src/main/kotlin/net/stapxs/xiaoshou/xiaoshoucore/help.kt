package net.stapxs.xiaoshou.xiaoshoucore

import net.mamoe.mirai.event.events.GroupMessageEvent
import net.stapxs.xiaoshou.features.CommandList

suspend fun sendHelp(event: GroupMessageEvent, command: String) {
    if(command != "all") {
        var out = "> 指令 - $command\n━━━━━━━━━━━━\n"
        for (com in CommandList.comList) {
            if (com.comName == command) {
                if (com.comDescription != "") {
                    if (com.comDescription.indexOf("-") > 0) {
                        val use = com.comDescription.split("-")
                        for(useIn in use) {
                            if(useIn != "hidden") {
                                out += useIn + "\n"
                            }
                        }
                    } else {
                        out += com.comDescription
                    }
                }
            }
        }
        out += "━━━━━━━━━━━━"
        if(out == "> 指令 - $command\n━━━━━━━━━━━━\n") {
            out = "这是啥指令？没见过……"
        }
        Xiaoshou.sendMessage("Group", out, event)
        return
    } else {
        var out = "> 所有指令：\n━━━━━━━━━━━━\n"
        for(com in CommandList.comList) {
            if (com.comDescription.indexOf("hidden") < 0 && com.comType != "&" && com.comDescription != "") {
                if (CommandList.hasAuthority(com.comType, com.comName, event.group.id)) {
                    out += "[o] " + com.comName + "\n"
                } else {
                    out += "[x] " + com.comName + "\n"
                }
            }
        }
        out += "━━━━━━━━━━━━"
        Xiaoshou.sendMessage("Group", out, event)
    }
}