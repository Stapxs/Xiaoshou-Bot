package net.stapxs.xiaoshou.xiaoshoucore.commands

import net.mamoe.mirai.message.GroupMessageEvent
import net.stapxs.xiaoshou.features.Log
import net.stapxs.xiaoshou.features.SSUserClass
import net.stapxs.xiaoshou.xiaoshoucore.Monitor
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
                Monitor.messageSender("Group", groupId, msg)
            }
            SSUserClass.downloadImg(url, "PicsCache/$time")
            Monitor.imangeSender("Group", groupId, File("PicsCache/$time"))
            File("PicsCache/$time").delete()
        }
        catch (e: Throwable) {
            Monitor.messageSender("Group", groupId, "获取错误：\n" + e.message)
            Log.addLog("xiaoshou", "图片服务器出了点问题：$e")
        }
    }

}