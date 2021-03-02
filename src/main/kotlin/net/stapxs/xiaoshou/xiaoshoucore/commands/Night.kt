package net.stapxs.xiaoshou.xiaoshoucore.commands

import net.stapxs.xiaoshou.features.GloStorage
import net.stapxs.xiaoshou.features.Log
import net.stapxs.xiaoshou.features.Options
import net.stapxs.xiaoshou.features.SSUserClass
import java.io.File

object Night {

    private val file = File("Options/nightSays.ini")

    fun send(id: Long): String {
        val nights = Options.read(file)
        if(nights.count() <= 0) {
            Log.addLog("xiaoshou", "Err > Night.kt > fun sendNight > 读取晚安设置错误！")
            return "Err > Night.kt > fun sendNight > 读取晚安设置错误！"
        } else {
            // 判断时间
            if(SSUserClass.getTimeH().toInt() > 21 || SSUserClass.getTimeH().toInt() < 4)
            {
                // 判断是否发送
                var canSay = false
                var sayed = false
                var hasUse = false
                for(save in GloStorage.NightStorage) {
                    val nowDay = SSUserClass.getDayList()
                    if(save.qqID == id) {
                        hasUse = true
                        if (save.sayNightTime[0].toInt() < nowDay[0].toInt() ||
                            save.sayNightTime[1].toInt() < nowDay[1].toInt() ||
                            save.sayNightTime[2].toInt() < nowDay[2].toInt()
                        ) {
                            save.sayTimes = 1
                            canSay = true
                            save.sayNightTime = nowDay
                        }
                        else {
                            if(save.sayTimes in 1..3) {
                                save.sayTimes ++
                                canSay = true
                                sayed = true
                            }
                        }
                    }
                }
                if(!hasUse) {
                    canSay = true
                    GloStorage.NightStorage.add(
                        GloStorage.NightStorageVer(SSUserClass.getDayList(), id))
                }
                println("是否发送：$canSay")
                println("是否发送过：$sayed")
                if(canSay) {
                    if(!sayed) {
                        var hasSend = false
                        for (says in nights) {
                            if (says.optName.toLong() == id) {
                                hasSend = true
                                return SSUserClass.getOne(says.optValue.split("/"))
                            }
                        }
                        if (!hasSend) {
                            return "晚安！"
                        }
                    }
                    else {
                        return if(Options.getOpt("goNight") != "Err") {
                            val goNightList = Options.getOpt("goNight").split(",")
                            SSUserClass.getOne(goNightList)
                        } else {
                            "赶紧去睡啦 = ="
                        }
                    }
                }
            } else {
                return "这么早就睡了 = ="
            }

        }
        return "Err > Night.tk > fun sendNight > 未知错误"
    }

    private fun all(id: Long): String {
        val nights = Options.read(file)
        var out = "> 所有晚安语句：\n━━━━━━━━━━━━\n"
        for(night in nights) {
            if(night.optName.toLong() == id) {
                val nightSays = night.optValue.split("/")
                for (nightSay in nightSays) {
                    out += nightSay + "\n"
                }
            }
        }
        out += "━━━━━━━━━━━━"
        return out
    }

    private fun add(id: Long, say: String): String {
        if(say.indexOf("/") >= 0 || say.indexOf(":") >= 0) {
            return "不能有“:”和“/”哦"
        }
        val nights = Options.read(file)
        for(night in nights) {
            if(night.optName.toLong() == id) {
                if(Options.set(file, nights, night.optName, night.optValue + "/$say")) {
                    return "记住啦"
                } else {
                    return "啊呀没记住呢 XD"
                }
            }
        }
        if(Options.set(file, nights, id.toString(), say)) {
            return "记住啦"
        } else {
            return "啊呀没记住呢 XD"
        }
    }

    fun nightJump(command: MutableList<String>, id: Long): String {
        return when(command[0]) {
            "all" -> all(id)
            "add" -> add(id, SSUserClass.mergeStringList(command, 1, command.count() - 1, " "))
            else -> SSUserClass.getOne(GloStorage.comErrSay)
        }
    }
}