package net.stapxs.xiaoshou.xiaoshoucore.commands

import net.stapxs.xiaoshou.features.GloStorage
import net.stapxs.xiaoshou.features.Log
import net.stapxs.xiaoshou.features.Options
import net.stapxs.xiaoshou.features.SSUserClass

object Night {

    class NightVer(id: Long, says: MutableList<String>) {
        val nId: Long = id
        val nSays: List<String> = says
    }

    fun sendNight(id: Long): String {
        if(Options.getOpt("nightSays") == "Err") {
            Log.addLog("xiaoshou", "Err <- 缺失配置：nightSays")
            return "Err <- 缺失配置：nightSays"
        } else {
            // 判断时间
            if(SSUserClass.getTimeH().toInt() > 21 || SSUserClass.getTimeH().toInt() < 4)
            {
                // 反序列化
                val nightSays = recodeNSays(Options.getOpt("nightSays"))
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
                        for (says in nightSays) {
                            if (says.nId == id) {
                                hasSend = true
                                return SSUserClass.getOne(says.nSays)
                            }
                        }
                        if (!hasSend) {
                            return "晚安！"
                        }
                    }
                    else {
                        if(Options.getOpt("goNight") != "Err") {
                            var goNightList = Options.getOpt("goNight").split(",")
                            return SSUserClass.getOne(goNightList)
                        }
                        else {
                            return "赶紧去睡啦 = ="
                        }
                    }
                }
            } else {
                return "这么早就睡了 = ="
            }

        }
        return "Err <- 未知错误：Night.tk"
    }
    
    /**
     * @Author Stapxs
     * @Description 序列化晚安存储信息
     * @Date 下午 04:35 2020/12/1
     * @Param 
     * @return 
    **/
    private fun encodeNSays() {
        
    }

    /**
     * @Author Stapxs
     * @Description 反序列化晚安存储信息
     * @Date 下午 04:36 2020/12/1
     * @Param
     * @return
    **/
    private fun recodeNSays(set: String): MutableList<NightVer> {
        println("> 开始反序列化晚安设置……")
        var nights: MutableList<NightVer> = mutableListOf()
        val main = set.split(",")
        for (says in main) {
            val sayQQ: MutableList<String> = says.split("/") as MutableList<String>
            val qq = sayQQ[0].toLong()
            sayQQ.removeAt(0)
            println("    $qq -> $sayQQ")
            nights.add(NightVer(qq, sayQQ))
        }
        return nights
    }

}