package net.stapxs.xiaoshou

import com.google.gson.Gson
import net.stapxs.xiaoshou.features.Log
import net.stapxs.xiaoshou.features.Options
import java.util.*
import kotlin.system.exitProcess

// 版本号
const val sysVersion = "Dev-0.3.1"
// 全局 Json 反序列化 Gson
val gson = Gson()
// 全局 IO
val reader = Scanner(System.`in`)
// 退出标识
var exit = false


fun main() {
    // 初始化日志输出
    Log.logSaver()
    Log.addLog("log", "Log 输出系统初始化完成！")

    // 初始化设置
    if(!Options.readOpt()) {
        // 设置文件不存在
        println("ERR >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 设置文件不存在。")
        exit = true
        exitProcess(-1)
    }
    Log.addLog("log", "设置读取初始化完成！")
}
