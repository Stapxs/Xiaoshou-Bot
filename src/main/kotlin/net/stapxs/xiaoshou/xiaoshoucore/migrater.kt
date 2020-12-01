package net.stapxs.xiaoshou.xiaoshoucore

import net.mamoe.mirai.message.data.content
import net.stapxs.xiaoshou.features.Log

object migrater {

    class ComVer(type:String, name:String, list:String) {
        val comType:String = type
        val comName:String = name
        var groupListNames:String = list

    }
    private var comList:List<ComVer> = mutableListOf(
        ComVer("/", "wiki", "DHWGroups,PdsGroups"),
        ComVer("/", "night", "DHWGroups,PdsGroups"),
        ComVer("/", "getpic", "DHWGroups,PdsGroups"),
        ComVer("/", "doing", "DHWGroups,PdsGroups"),
        ComVer("/", "upload", "DHWGroups"),

        ComVer(".", "林槐语录", "All"),
        ComVer(".", "小龙语录", "DHWGroups"),
        ComVer(".", "runInfo", "DHWGroups,PdsGroups"),
        ComVer(".", "cat", "DHWGroups,PdsGroups"),
        ComVer(".", "dog", "DHWGroups,PdsGroups"),
        ComVer(".", "fox", "DHWGroups,PdsGroups"),

        ComVer(":", "mcwiki", "DHWGroups,PdsGroups"),
        ComVer(":", "trwiki", "DHWGroups,PdsGroups"),
        ComVer(":", "qx", "DHWGroups"),

        ComVer("&", "Xiaoshou Ver", "BotTestGroups"),
        ComVer("&", "你好，小受！", "All"),

        ComVer("&", "[[wiki]]", "DHWGroups"),
        ComVer("&", "晚安问候", "DHWGroups,PdsGroups"),
        ComVer("&", "早安问候", "DHWGroups,PdsGroups")
    )

    fun at(type: String, msg: String) {
        when(type) {
                "/" -> {
                    Log.addLog("xiaoshou", "执行 $type 指令 > $msg")
                    return
                }
                "." -> {
                    Log.addLog("xiaoshou", "执行 $type 指令 > $msg")
                    return
                }
                "。" -> {
                    Log.addLog("xiaoshou", "执行 $type 指令 > $msg")
                    return
                }
                ":" -> {
                    Log.addLog("xiaoshou", "执行 $type 指令 > $msg")
                    return
                }
        }
    }

}