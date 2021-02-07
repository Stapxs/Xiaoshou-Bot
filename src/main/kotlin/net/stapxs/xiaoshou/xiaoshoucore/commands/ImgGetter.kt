package net.stapxs.xiaoshou.xiaoshoucore.commands

import net.mamoe.mirai.event.events.GroupMessageEvent
import net.stapxs.xiaoshou.features.Log
import net.stapxs.xiaoshou.features.SSUserClass
import net.stapxs.xiaoshou.xiaoshoucore.Xiaoshou
import java.io.File

object ImgGetter {

    suspend fun DownloadASend(event: GroupMessageEvent, url: String, msg: String = "") {
        Log.addLog("xiaoshou", "正在和图片服务器 AI 谈话……")
        val groupId = event.group.id
        val file = File("PicsCache")
        file.mkdir()
        try {
            val time = SSUserClass.getTimeAdd()
            if(msg != "") {
                Xiaoshou.sendMessage("Group", msg ,event)
            }
            SSUserClass.downloadImg(url, "PicsCache/$time")
            Xiaoshou.imangeSender("Group", File("PicsCache/$time") ,event)
            File("PicsCache/$time").delete()
        }
        catch (e: Throwable) {
            Xiaoshou.sendMessage("Group", "获取错误：\n" + e.message ,event)
            Log.addLog("xiaoshou", "图片服务器出了点问题：$e")
        }
    }

}