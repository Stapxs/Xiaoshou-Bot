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
     * @Description TODO 初始化读取设置
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
            optList = read(File("Options.ini"))
            if(optList.count() <= 0) {
                // 读取错误
                return false
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
        return get(optList, name)
    }

    /**
     * @Author Stapxs
     * @Description TODO 更改设置（自动保存）
     * @Date 下午 08:12 2020/11/22
     * @Param
     * @return
     **/
    fun setOpt(name:String, value: String):Boolean {
        return set(File("Options.ini"), optList, name, value)
    }

    /**
     * @Author Stapxs
     * @Description TODO 保存设置
     * @Date 下午 08:12 2020/11/22
     * @Param
     * @return
     **/
    private fun saveOpt():String {
        return save(File("Options.ini"), optList)
    }

    /** ------------------------------------------------------------------------ **/

    /**
     * @Author Stapxs
     * @Description TODO 读取指定的ini设置文件
     * @Date 下午 03:59 2021/2/10
     * @Param
     * @return
    **/
    fun read(file: File): MutableList<OptVer> {
        val list:MutableList<OptVer> = mutableListOf()
        try {
            if(!file.exists()) {
                // 未找到设置文件……
                    Log.addLog("xiaoshou", "没有找到 ${file.name} 这个东西！")
                return mutableListOf()
            }
            val fileList: List<String> = file.readLines()
            println(">> 在 ${file.name} 读取到如下设置：")
            for (str in fileList) {
                val strList: List<String> = mutableListOf(
                    str.substring(0, str.indexOf(":")),
                    str.substring(str.indexOf(":") + 1)
                )
                val opt = OptVer(strList[0], strList[1])
                list.add(opt)
                if (opt.optName != "qqPassword") {
                    println("\t" + opt.optName + " : " + opt.optValue)
                }
            }
            return list
        }
        catch (e: Throwable) {
            Log.addLog("xiaoshou", "在整理 ${file.name} 这个东西的时候出了点问题。")
            return mutableListOf()
        }
    }

    fun get(opts:MutableList<OptVer>, name:String): String {
        for(opt in opts) {
            if(opt.optName == name) {
                return opt.optValue
            }
        }
        return "Err"
    }

    /**
     * @Author Stapxs
     * @Description TODO 保存指定的ini设置文件
     * @Date 下午 04:09 2021/2/10
     * @Param
     * @return
    **/
    private fun save(file: File, opts: MutableList<OptVer>):String {
        try {
            file.writeText("")
            for (opt in opts) {
                file.appendText(opt.optName + ":" + opt.optValue + "\n")
            }
        }
        catch (e: Throwable) {
            return "Err > Options.tk > fun save > " + e.message
        }
        return "OK"
    }

    /**
     * @Author Stapxs
     * @Description TODO 更改指定的ini设置文件（自动保存）
     * @Date 下午 04:18 2021/2/10
     * @Param
     * @return
    **/
    fun set(file: File, opts: MutableList<OptVer>, name:String, value: String): Boolean {
        try {
            var isChanged = false
            for (opt in opts) {
                if (opt.optName == name) {
                    opt.optValue = value
                    isChanged = true
                }
            }
            if(!isChanged) {
                val opt = OptVer(name, value)
                opts.add(opt)
            }
            if(save(file, opts) == "OK") {
                return true
            }
        }
        catch (e: Throwable) {
            return false
        }
        return false
    }
}
