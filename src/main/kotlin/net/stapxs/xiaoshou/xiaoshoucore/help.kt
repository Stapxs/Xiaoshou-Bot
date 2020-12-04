package net.stapxs.xiaoshou.xiaoshoucore

import net.stapxs.xiaoshou.xiaoshoucore.commands.CommandList

suspend fun sendHelp(id: Long, command: String) {
    if(command != "all") {
        for (com in CommandList.comList) {
            if (com.comName == command) {
                if (com.comDescription != "") {
                    Monitor.messageSender("Group", id, com.comDescription)
                } else {
                    Monitor.messageSender("Group", id, "这个指令没有提示欸")
                    return
                }
            }
        }
        Monitor.messageSender("Group", id, "这是啥指令？没见过……")
        return
    } else {

    }
}