package net.stapxs.xiaoshou.features

import net.stapxs.xiaoshou.exit
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object Log {

    private val logList: Deque<String> = LinkedList()            // Log 队列

    /**
     * @Author Stapxs
     * @Description //TODO 添加待输出的 Log
     * @Date 下午 08:25 2020/11/22
     * @Param
     * @return
    **/
    fun addLog(type: String, log: String) {
        val nowTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        val outString = "[$nowTime][$type] $log"
        logList.add(outString)
    }

    /**
     * @Author Stapxs
     * @Description //TODO 日志写入服务
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
     * @Description //TODO 写入日志
     * @Date 下午 09:52 2020/11/22
     * @Param
     * @return
    **/
    fun writeLog()
    {
        println(">> 开始输出日志")
        while (!exit)
        {
            while (logList.peek() != null)
            {
                val log = logList.poll()
                println(">> 输出日志$log")
                File("LogNow.log").appendText("$log\n")
            }
        }
        println(">> 输出日志结束")
    }

    class CustomThread : Thread() {
        override fun run() {
            super.run()
            writeLog()
        }
    }
}