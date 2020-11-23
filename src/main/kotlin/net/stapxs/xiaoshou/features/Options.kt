package net.stapxs.xiaoshou.features

import java.io.File

object Options {

    class OptVer(name:String, value:String) {
        val optName:String = name
        var optValue:String = value

    }
    var optList:MutableList<OptVer> = mutableListOf()

    /**
     * @Author Stapxs
     * @Description //TODO 初始化读取设置
     * @Date 下午 08:09 2020/11/22
     * @Param
     * @return
     **/
    fun readOpt(): Boolean {
        println("> 开始初始化设置系统……")
        try {
            if(!File("Options.ini").exists()) {
                // 未找到设置文件……
                return  false
            }
            val optFileList: List<String> = File("Options.ini").readLines()
            println(">> 读取到如下设置：")
            for (str in optFileList) {
                if(str.substring(0, 2) != "##") {
                    val strList:List<String> = mutableListOf(
                        str.substring(0, str.indexOf(":")),
                        str.substring(str.indexOf(":") + 1)
                    )
                    val opt = OptVer(strList[0], strList[1])
                    optList.add(opt)
                    if(opt.optName != "qqPassword") {
                        println("\t" + opt.optName + " : " + opt.optValue)
                    }
                }
            }
            // 初始化 GroupList
            GroupList.initGroupList()

            return true
        }
        catch (e: Throwable) {
            return  false
        }
    }

    fun getOpt(name:String): String {
        for(opt in optList) {
            if(opt.optName == name) {
                return opt.optValue
            }
        }
        return "Err"
    }

    /**
     * @Author Stapxs
     * @Description //TODO 更改设置（自动保存）
     * @Date 下午 08:12 2020/11/22
     * @Param
     * @return
     **/
    fun setOpt(name:String, value: String):Boolean {
        return try {
            var isChanged = false
            for (opt in optList) {
                if (opt.optName == name) {
                    opt.optValue = value
                    isChanged = true
                }
            }
            if(!isChanged) {
                val opt = OptVer(name, value)
                optList.add(opt)
            }
            saveOpt()
            true
        }
        catch (e: Throwable) {
            false
        }
    }

    /**
     * @Author Stapxs
     * @Description //TODO 保存设置
     * @Date 下午 08:12 2020/11/22
     * @Param
     * @return
     **/
    private fun saveOpt():String {
        try {
            File("Options.ini").writeText("")
            for (opt in optList) {
                File("Options.ini").appendText(opt.optName + ":" + opt.optValue + "\n")
            }
        }
        catch (e: Throwable) {
            return "err " + e.message
        }
        return "Err 未知错误 > Options.tk"
    }
}
