package net.stapxs.xiaoshou.features

import net.stapxs.xiaoshou.exit
import net.stapxs.xiaoshou.sysVersion
import java.lang.management.ManagementFactory
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

object RunStat {

    // 运行状态监测
    fun runStat() {
        println("> 初始化状态监控……")
        Log.addLog("stat", "正在初始化状态监控……")
        while(!exit) {
            val statList = getInfo()

            // 同步 API
            if(Options.getOpt("botAPILink") != "Err" ) {
                println("> 同步 API……")

                // 拼接json
                try {
                    var json = "{"
                    for (info in statList) {
                        json += "\"" + info.optName + "\":\"" + info.optValue + "\","
                    }
                    json = json.substring(0, json.length - 1) + "}"
                    json = URLEncoder.encode(json, "utf-8")
                    println(json)

                    val apiLink = Options.getOpt("botAPILink") + json
                    val back = URL(apiLink).readText()

                    println("提交API完成：$back")
                    Log.addLog("stat", "Xiaoshou 已运行：${statList[2].optValue}， 提交 API 成功：$back")
                }
                catch (e: Throwable) {
                    println("提交API失败：$e")
                    Log.addLog("stat", "Xiaoshou 已运行：${statList[2].optValue}， 提交 API 失败：$e")
                }
            } else {
                Log.addLog("stat", "Xiaoshou 已运行：${statList[2].optValue}，没有找到 BotAPI 地址，请确认设置并重启或刷新设置。")
            }

            Thread.sleep(600000L)
        }
    }

    fun getInfo():MutableList<Options.OptVer> {
        val memoryMXBean = ManagementFactory.getMemoryMXBean()
        val memoryUsage = memoryMXBean.heapMemoryUsage //椎内存使用情况
        val maxMemorySize = SSUserClass.getNetFileSizeDescription(memoryUsage.max, "MB") //最大可用内存
        val usedMemorySize = SSUserClass.getNetFileSizeDescription(memoryUsage.used, "MB") //已使用的内存
        //现在时间 - 程序启动时间
        val runtimeMXBean = ManagementFactory.getRuntimeMXBean()
        val startTime: Long = runtimeMXBean.startTime
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS")
        val startDate = sdf.format(Date(startTime))
        val sdfnow = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val nowDate = sdfnow.format(Date())

        val statList: MutableList<Options.OptVer> = mutableListOf()
        statList.add(Options.OptVer("botVer", sysVersion))
        statList.add(Options.OptVer("nowTime", nowDate))
        statList.add(Options.OptVer("runTime", SSUserClass.getDistanceTime(Date().time, startTime).toString()))
        statList.add(Options.OptVer("usedMemorySize", usedMemorySize.toString()))
        statList.add(Options.OptVer("maxMemorySize", maxMemorySize.toString()))

        return statList
    }

}