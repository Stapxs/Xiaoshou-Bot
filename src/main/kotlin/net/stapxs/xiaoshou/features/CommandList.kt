package net.stapxs.xiaoshou.features

import java.io.File

object CommandList {

    class ComVer(type:String, name:String, list:String, Des: String = "") {
        val comType:String = type
        val comName:String = name
        var groupListNames:String = list
        val comDescription: String = Des
    }

    var comList:MutableList<ComVer> = mutableListOf()

    var file = File("Options/Commands.sconf")

    /**
     * @Author Stapxs
     * @Description 读取 Command List
     * @Date 下午 05:43 2021/2/19
     * @Param
     * @return
    **/
    fun read(): Boolean {
        val list:MutableList<ComVer> = mutableListOf()
        try {
            if(!file.exists()) {
                // 未找到设置文件……
                Log.addLog("xiaoshou", "没有找到命令列表！")
                return false
            }
            val fileList: List<String> = file.readLines()
            println(">> 读取到如下命令列表：")
            for (str in fileList) {
                var coms = str.split(" | ")
                println(coms)
                if(coms.count() == 3) {
                    val com = ComVer(coms[0], coms[1], coms[2])
                    comList.add(com)
                } else {
                    val com = ComVer(coms[0], coms[1], coms[2], coms[3])
                    comList.add(com)
                }
            }
            return true
        }
        catch (e: Throwable) {
            Log.addLog("xiaoshou", "在整理命令列表的时候出了点问题。")
            return false
        }
    }

    /**
     * @Author Stapxs
     * @Description 是否拥有执行权限
     * @Date 下午 08:20 2020/11/23
     * @Param
     * @return
     **/
    fun hasAuthority(type:String, command:String, id:Long):Boolean {
        if(hasCommand(type, command)) {
            for (com in CommandList.comList) {
                if (com.comName == command && com.comType == type) {
                    if (com.groupListNames == "All") {
                        return true
                    }
                    val groupLists = com.groupListNames.split(",")
                    for (groupList in groupLists) {
                        if (GroupList.isInGroupList(groupList, id)) {
                            return true
                        }
                    }
                }
            }
            Log.addLog("group", "$id 没有 $type$command 的运行权限。")
        }
        return  false
    }

    /**
     * @Author Stapxs
     * @Description 有没有这个指令
     * @Date 下午 08:22 2020/11/23
     * @Param
     * @return
     **/
    private fun hasCommand(type: String, command: String): Boolean {
        for(com in CommandList.comList) {
            if(com.comName == command && com.comType == type) {
                return  true
            }
        }
        if(type != "&") {
            Log.addLog("xiaoshou", "指令 $type $command 不存在。")
        }
        return false
    }

}