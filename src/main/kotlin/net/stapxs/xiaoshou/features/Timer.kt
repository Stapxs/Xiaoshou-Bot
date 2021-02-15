package net.stapxs.xiaoshou.features

import java.util.*

/**
 * @Author Stapxs
 * @Description 负责处理限时功能的模块
 * @Date 下午 11:31 2021/2/15
 * @Param 
 * @return 
**/
object Timer {

    class RunVer(name: String, type: Int, finTime: String, maxTimes: Int, nowTimes: Int) {
        var name: String = name
        var type: Int = type
        var finTime: String = finTime
        var maxTimes: Int = maxTimes
        var nowTimes: Int = nowTimes
    }
    private var runList: ArrayList<RunVer> = ArrayList<RunVer>()    //限时数组


    fun isFinished(name: String): String {
        val run = get(name)
        if(run.name == "Err" && run.type == 0) {
            return "null"
        }
        when (run.type) {
            1 -> {
                // 不写了 NMD
            }
        }
        return ""
    }

    fun get(name: String): RunVer {
        for(run: RunVer in runList) {
            if(run.name == name) {
                return run
            }
        }
        return RunVer("Err", 0, "Err", 0, 0)
    }

    fun add(data: RunVer) {
        runList.add(data)
    }
    
}