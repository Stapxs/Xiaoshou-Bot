package net.stapxs.xiaoshou.xiaoshoucore.commands

import net.stapxs.xiaoshou.features.GroupList
import net.stapxs.xiaoshou.features.Log

object CommandList {

    class ComVer(type:String, name:String, list:String, Des: String = "") {
        val comType:String = type
        val comName:String = name
        var groupListNames:String = list
        val comDescription: String = Des

    }
    private var comList:List<ComVer> = mutableListOf(
        ComVer("/", "wiki", "DHWGroups,PdsGroups"),
        ComVer("/", "night", "DHWGroups,PdsGroups"),
        ComVer("/", "getpic", "DHWGroups,PdsGroups", "> 下载网络图片"),
        ComVer("/", "doing", "DHWGroups,PdsGroups"),
        ComVer("/", "say", "All"),

        ComVer(".", "林槐语录", "All"),
        ComVer(".", "小龙语录", "DHWGroups"),
        ComVer(".", "runInfo", "DHWGroups,PdsGroups"),
        ComVer(".", "cat", "DHWGroups,PdsGroups"),
        ComVer(".", "dog", "DHWGroups,PdsGroups"),
        ComVer(".", "fox", "DHWGroups,PdsGroups"),

        ComVer(":", "mcwiki", "DHWGroups,PdsGroups"),
        ComVer(":", "trwiki", "DHWGroups,PdsGroups"),
        ComVer(":", "qx", "DHWGroups"),

        ComVer("&", "Xiaoshou Ver", "BotTestGroups"),
        ComVer("&", "你好，小受！", "All"),

        ComVer("&", "[[wiki]]", "DHWGroups"),
        ComVer("&", "晚安问候", "DHWGroups,PdsGroups"),
        ComVer("&", "早安问候", "DHWGroups,PdsGroups")
    )

    // ComVer("&", "垃圾人品", "DHWGroups"),

    /**
     * @Author Stapxs
     * @Description 是否拥有执行权限
     * @Date 下午 08:20 2020/11/23
     * @Param
     * @return
    **/
    fun hasAuthority(type:String, command:String, id:Long):Boolean {
        if(hasCommand(type, command)) {
            for (com in comList) {
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
        for(com in comList) {
            if(com.comName == command && com.comType == type) {
                return  true
            }
        }
        return false
    }

}