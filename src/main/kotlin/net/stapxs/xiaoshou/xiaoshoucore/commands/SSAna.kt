package net.stapxs.xiaoshou.xiaoshoucore.commands

import net.stapxs.xiaoshou.features.Log
import java.net.URL

object SSAna {

    fun getSSAna(isAll: Boolean = false): MutableList<String> {
        Log.addLog("xiaoshou", "正在获取主人的笔记……")

        var anaJson = URL("https://stapx.chuhelan.com/api/SS-Ana/Get/?type=json").readText()
        if(isAll)
        {
            anaJson = URL("https://stapx.chuhelan.com/api/SS-Ana/Get/?type=json&all=true").readText()
        }

        if(anaJson.indexOf("\"stat\":200") > 0)
        {
            val out: MutableList<String> = mutableListOf()
            out.add(anaJson.substring(anaJson.indexOf("\"ana\":\"") + 7, anaJson.indexOf("\"}") - 1))
            out.add(anaJson.substring(anaJson.indexOf("\"id\":") + 5, anaJson.indexOf(",\"ana\":\"")))
            Log.addLog("xiaoshou", "拿到了主人的笔记：${out[1]}")
            return out
        }

        val out: MutableList<String> = mutableListOf()
        out.add("ERR")
        out.add("ERR")
        return out
    }

    fun getOneSSAna(num: String): String {
        Log.addLog("xiaoshou", "正在获取主人的笔记……")
        val get = URL("https://stapx.chuhelan.com/api/SS-Ana/Get/?id=$num").readText()
        return if(get != "") {
            Log.addLog("xiaoshou", "拿到了主人的笔记")
            get.substring(0, get.length - 1)
        } else {
            "ERR"
        }
    }

}