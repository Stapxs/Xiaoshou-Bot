package net.stapxs.xiaoshou

import com.google.gson.Gson
import net.mamoe.mirai.Bot
import net.stapxs.xiaoshou.features.Log
import net.stapxs.xiaoshou.features.Options
import net.stapxs.xiaoshou.xiaoshoucore.monitor
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

// 版本号
const val sysVersion = "Dev-0.4.22"
// 全局 Json 反序列化 Gson
val gson = Gson()
// 全局 IO
val reader = Scanner(System.`in`)
// 退出标识
var exit = false
// 是否初始化完成标识
var finished = false
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
        Log.printErr("设置文件不存在。")
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
    val miraiBot = Bot(qqID, qqPassword) { fileBasedDeviceInfo() }
    miraiBot.login()    // 登录

    // 启动 Xiaoshou Core 事件监听
    Thread{monitor.XiaoshouCore(miraiBot)}.run()

    // 判断登陆状态
    while (!miraiBot.isOnline) {}
    finished = true
    val format = "HH:mm:ss"
    val sdf = SimpleDateFormat(format)
    val diff = System.currentTimeMillis() - startTime
    val day: Long = diff / (1000 * 60 * 60 * 24) //以天数为单位取整
    val hour: Long = diff / (60 * 60 * 1000) - day * 24 //以小时为单位取整
    val min: Long = diff / (60 * 1000) - day * 24 * 60 - hour * 60 //以分钟为单位取整
    val second: Long = diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60 //秒

    Log.addLog("core", "初始化成功，耗时：$hour:$min:$second")

    // 启动运行状态监听

    // 启动控制台事件处理

    // 启动远程控制台事件处理

    while (!exit){

    }
}