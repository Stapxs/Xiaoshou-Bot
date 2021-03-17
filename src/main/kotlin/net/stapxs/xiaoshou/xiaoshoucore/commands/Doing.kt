package net.stapxs.xiaoshou.xiaoshoucore.commands

import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.code.MiraiCode.deserializeMiraiCode
import net.mamoe.mirai.message.data.At
import net.stapxs.xiaoshou.features.Options
import net.stapxs.xiaoshou.sysVersion
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

object Doing {
    suspend fun Get(event: GroupMessageEvent, str: String): String {
        val group = event.group.id
        var url = ""
        var out = ""

        val chain = event.message.serializeToMiraiCode().deserializeMiraiCode()
        val at = chain.filterIsInstance<At>().firstOrNull()
        if(at != null)
        {
            val atst = at.toString().split(":")
            val atid = atst[2].substring(0, atst[2].length - 1)
            url = "${Options.getOpt("doingAPILink")}/Get/?qq=$atid&bot=xiaoshou&group=$group"
            out = "$atid 正在运行：\n━━━━━━━━━━━━\n"
        }
        else
        {
            url = "${Options.getOpt("doingAPILink")}/Get/?name=$str&bot=xiaoshou&group=$group"
            out = "$str 正在运行：\n━━━━━━━━━━━━\n"
        }
        val gets = URL(url).readText()
        println("￥获取到的Doing数据：$gets")
        val getsList = gets.split("\n")
        for(info in getsList) {
            if(info != "") {
                if(info.substring(0,3).indexOf("Err") >= 0) {
                    out = info + "\n"
                    break
                }
                var doing = info.substring(info.indexOf(":") + 1)
                var type = doing.substring(0, doing.indexOf(":"))
                doing = doing.substring(doing.indexOf(":") + 1)
                doing = doing.substring(doing.indexOf(":") + 1)
                var time = doing.substring(0, doing.indexOf(":"))
                doing = doing.substring(doing.indexOf(":") + 1)

//                val dateFormat = SimpleDateFormat("dd-MM-yy HH:mm:ss")
//                val dt =  Date(unix)

                if(time.toLong() + 600 < System.currentTimeMillis() / 1000) {
                    out += "数据已经失效 (${time.toLong() + 180} < ${System.currentTimeMillis() / 1000})\n"
                }
                else {
                    out += "$type > $doing\n"
                }
            }
        }
        out += "━━━━━━━━━━━━"
        return out
    }
}