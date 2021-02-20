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
            Xiaoshou.sendImange("Group", File("PicsCache/$time") ,event)
            File("PicsCache/$time").delete()
        }
        catch (e: Throwable) {
            Xiaoshou.sendMessage("Group", "获取错误：\n" + e.message ,event)
            Log.addLog("xiaoshou", "图片服务器出了点问题：$e")
        }
    }

    fun Download(url: String): String {
        Log.addLog("xiaoshou", "正在和图片服务器 AI 谈话……（$url）")
        val file = File("PicsCache")
        file.mkdir()
        try {
            val time = SSUserClass.getTimeAdd()
            SSUserClass.downloadImg(url, "PicsCache/$time")
            return "PicsCache/$time"
        }
        catch (e: Throwable) {
            Log.addLog("xiaoshou", "图片服务器出了点问题：$e")
            return ""
        }
    }

    object pixiv {

        suspend fun getASend(msg: String, event: GroupMessageEvent) {
            val id = getID(msg)
            if(id == "") {
                // 字符串不包含 ID
                Log.addLog("xiaoshou", "Err > ImgGetter.kt > fun getASend > 字符串不包含 Pixiv ID")
                return
            }
            val getUrl = "https://pixiv.cat/"
            var downLoad = Download("$getUrl$id.png")
            if(downLoad == "") {
                downLoad = Download("$getUrl$id-1.png")
                if(downLoad != "") {
                    Xiaoshou.sendImange("Group", File(downLoad) ,event)
                    File(downLoad).delete()
                }
            } else {
                Xiaoshou.sendImange("Group", File(downLoad) ,event)
                File(downLoad).delete()
            }
        }

        private fun getID(msg: String): String {
            val pixivURL = "https://www.pixiv.net/artworks/"
            val msgWUrl = msg.substring(msg.indexOf(pixivURL) + pixivURL.length)
            var id = ""
            for(num: Char in msgWUrl) {
                if(num.toByte().toInt() in 47..58) {
                    id += num.toString()
                } else {
                    continue
                }
            }
            return id
        }

    }

}