package net.stapxs.xiaoshou

import com.google.gson.Gson
import net.mamoe.mirai.Bot
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.utils.BotConfiguration
import net.stapxs.xiaoshou.features.*
import net.stapxs.xiaoshou.xiaoshoucore.Xiaoshou
import java.util.*
import kotlin.system.exitProcess

// 版本号
const val sysVersion = "Dev-0.5.22-2"
// 全局 Json 反序列化 Gson
val gson = Gson()
// 退出标识
var exit = false
// 初始化开始时间戳
var startTime = System.currentTimeMillis()
// 全局 Bot 列表
class BotVer(bot: Bot, name: String) {
    var bot: Bot = bot
    var name: String = name
}
var botList:MutableList<BotVer> = mutableListOf()

suspend fun main() {
    // 开始初始化
    startTime = System.currentTimeMillis()
    Log.addLog("core", "开始初始化……")
    // 初始化日志输出
    Log.logSaver()
    Log.addLog("log", "Log 输出系统初始化完成！")

    // 启动 Bot
    mainRun()

    // 启动运行状态监听
    Thread{RunStat.runStat()}.run()

    // 启动远程控制台事件处理

    // 启动控制台事件处理
    while (!exit){
        val command = readLine().toString()
        val back = controlRun(command)
        if(back != "OK") {
            Log.printErr("执行控制台指令错误 > $back")
        }
    }
}

/**
 * @Author Stapxs
 * @Description 控制台指令执行
 * @Date 下午 03:41 2021/2/20
 * @Param
 * @return
**/
private fun controlRun(msg: String): String {
    if(msg.indexOf(" > ") <= 0) {
        return "指令格式错误 > 缺少 Bot 对象"
    }
    val Bot = getBot(msg.substring(0, msg.indexOf(" > ")))
    val BotName = msg.substring(0, msg.indexOf(" > "))
    val commands = msg.substring(msg.indexOf(" > ") + 3).split(" ")
    if(Bot == null && BotName != "all") {
        return "执行错误 > 目标 Bot 不存在"
    }
    when(commands[0]) {
        "exit" -> {
            exitBot(BotName)
            if(BotName == "all") {
                exit = true
            }
            return "OK"
        }
    }
    return "执行错误 > 指令不存在"
}

/**
 * @Author Stapxs
 * @Description Bot 启动部分
 * @Date 下午 03:41 2021/2/20
 * @Param
 * @return
 **/
suspend fun mainRun() {
    // 初始化设置
    if(!Options.readOpt()) {
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

    // ----------------------------------------------------------------------------------------
    // Xiaoshou

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
    // 添加列表
    botList.add(BotVer(miraiBot, "xiaoshou"))

    // ----------------------------------------------------------------------------------------
}

/**
 * @Author Stapxs
 * @Description 获取指定的 Bot
 * @Date 下午 03:55 2021/2/20
 * @Param
 * @return
**/
private fun getBot(name: String): Bot? {
    for(botInfo in botList) {
        if(botInfo.name == name) {
            return botInfo.bot
        }
    }
    return null
}

/**
 * @Author Stapxs
 * @Description 退出所有(指定) Bot
 * @Date 下午 04:05 2021/2/20
 * @Param
 * @return
**/
private fun exitBot(name: String) {
    if(name == "all") {
        for(botInfo in botList) {
            botInfo.bot.close()
        }
    } else {
        for(botInfo in botList) {
            if(botInfo.name == name) {
                botInfo.bot.close()
                return
            }
        }
    }
}