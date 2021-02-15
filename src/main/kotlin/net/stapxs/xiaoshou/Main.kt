package net.stapxs.xiaoshou

import com.google.gson.Gson
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.utils.BotConfiguration
import net.stapxs.xiaoshou.features.Log
import net.stapxs.xiaoshou.features.Options
import net.stapxs.xiaoshou.features.RunStat
import net.stapxs.xiaoshou.features.SSUserClass
import net.stapxs.xiaoshou.xiaoshoucore.Xiaoshou
import java.util.*
import kotlin.system.exitProcess

// 版本号
const val sysVersion = "Dev-0.5.20-2"
// 全局 Json 反序列化 Gson
val gson = Gson()
// 退出标识
var exit = false
// 初始化开始时间戳
var startTime = System.currentTimeMillis()

suspend fun main() {
    // 开始初始化
    startTime = System.currentTimeMillis()
    Log.addLog("core", "开始初始化……")
    // 初始化日志输出
    Log.logSaver()
    Log.addLog("log", "Log 输出系统初始化完成！")

    // 初始化设置
    if(!Options.readOpt()) {
        // 设置文件不存在
        Log.printErr("设置文件不存在或读取错误。")
        exit = true
        exitProcess(-1)
    }
    Log.addLog("opt", "设置读取初始化完成！")

    // 获取账号设置
    if(Options.getOpt("qqID").indexOf("Err") > 0 ||
            Options.getOpt("qqPassword").indexOf("Err") > 0) {
        println("> 账户或密码设置项不存在。")
        Log.addLog("bot", "账户或密码设置项不存在。")
        exit = true
        exitProcess(-1)
    }
    val qqID = Options.getOpt("qqID").toLong()
    val qqPassword = Options.getOpt("qqPassword")

    // 启动 Mirai Bot
    Log.addLog("bot", "正在启动 Bot ……")
    val miraiBot = BotFactory.newBot(qqID, qqPassword) {
        fileBasedDeviceInfo()
        protocol = BotConfiguration.MiraiProtocol.ANDROID_PHONE
    }.alsoLogin()    // 登录

    // 启动 Xiaoshou Core 事件监听
    Thread{Xiaoshou.xiaoshouCore(miraiBot)}.run()

    // 判断登陆状态
    while (!miraiBot.isOnline) {}
    Log.addLog("core", "初始化成功，耗时：${SSUserClass.getDistanceTime(Date().time, startTime)}")

    // 启动运行状态监听
    Thread{RunStat.runStat()}.run()

    // 启动远程控制台事件处理

    // 启动控制台事件处理
    while (!exit){

    }
}