package net.stapxs.xiaoshou.features

import net.stapxs.xiaoshou.exit
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object Log {

    private var logList: Deque<String> = LinkedList()            // Log 队列

    /**
     * @Author Stapxs
     * @Description 添加待输出的 Log
     * @Date 下午 08:25 2020/11/22
     * @Param
     * @return
    **/
    fun addLog(type: String, log: String) {
        val nowTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        val outString = "[$nowTime][$type] $log"
        logList.addFirst(outString)
        // println("> 添加日志：$outString")
    }

    /**
     * @Author Stapxs
     * @Description 日志写入服务
     * @Date 下午 08:29 2020/11/22
     * @Param
     * @return
    **/
    fun logSaver() {
        println("> 开始初始化日志系统……")
        // 清空log文件
        if(!File("LogNow.log").exists()) {
            File("LogNow.log").createNewFile()
        } else {
            File("LogNow.log").delete()
            File("LogNow.log").createNewFile()
        }
        println(">> 启动日志系统……")
        // 运行输出线程
        // Thread{writeLog()}.run()
        val mThread = CustomThread()
        mThread.start()
    }

    /**
     * @Author Stapxs
     * @Description 写入日志
     * @Date 下午 09:52 2020/11/22
     * @Param
     * @return
    **/
    fun writeLog()
    {
        println("> 开始输出日志")
        while (!exit)
        {
            try {
                val log = logList.last
                if (log != null) {
                    logList.removeLast()
                    println("> 输出日志$log")
                    File("LogNow.log").appendText("$log\n")
                }
            } catch (e: Throwable) {}
        }
        println("> 输出日志结束")
    }

    class CustomThread : Thread() {
        override fun run() {
            super.run()
            writeLog()
        }
    }

    fun printErr(errStr: String) {
        println("ERR >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> $errStr")
    }

}