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

    var comList:List<ComVer> = mutableListOf(
        ComVer("/", "wiki", "DHWGroups,PdsGroups"),
        ComVer("/", "night", "DHWGroups,PdsGroups"),
        ComVer("/", "getpic", "DHWGroups,PdsGroups", "> 指令 - /getpic\n下载网络图片\n━━━━━━━━━━━━\n*例子*\n/getpic <图片链接>\n━━━━━━━━━━━━"),
        ComVer("/", "doing", "DHWGroups,PdsGroups"),
        ComVer("/", "say", "All", "> 指令 - /say\n让晓狩说点什么？\n    /say <说点什么>\n━━━━━━━━━━━━\n*例子*\n/say 栗子\n━━━━━━━━━━━━"),
        ComVer("/", "help", "All", "> 指令 - /help\n就是 help！使用/help all 查看所有。\n━━━━━━━━━━━━\n*例子*\n/help fox\n━━━━━━━━━━━━"),
        ComVer("/", "?", "All", "> 指令 - /?\n就是 help！使用/? all 查看所有。\n━━━━━━━━━━━━\n*例子*\n/? cat\n━━━━━━━━━━━━"),

        ComVer(".", "林槐语录", "All", "> 指令 - .林槐语录\n抽一条林槐语录\n━━━━━━━━━━━━"),
        ComVer(".", "lhyl", "All", "> 指令 - .lhyl\n抽一条林槐语录\n━━━━━━━━━━━━"),
        ComVer(".", "lhyl-a", "DHWGroups", "> 指令 - .lhyl-a\n抽一条林槐语录\n━━━━━━━━━━━━"),
        ComVer(".", "小龙语录", "DHWGroups"),
        ComVer(".", "runInfo", "DHWGroups,PdsGroups"),
        ComVer(".", "cat", "DHWGroups,PdsGroups"),
        ComVer(".", "dog", "DHWGroups,PdsGroups"),
        ComVer(".", "fox", "DHWGroups,PdsGroups"),

        ComVer(":", "林槐语录", "All", "> 指令 - 林槐语录:\n获取一条指定的一条林槐语录\n━━━━━━━━━━━━\n*例子*\n林槐语录:32\n━━━━━━━━━━━━"),
        ComVer(":", "lhyl", "All", "> 指令 - lhyl:\n获取一条指定的一条林槐语录\n━━━━━━━━━━━━\n*例子*\nlhyl:32\n━━━━━━━━━━━━"),
        ComVer(":", "mcwiki", "DHWGroups,PdsGroups"),
        ComVer(":", "trwiki", "DHWGroups,PdsGroups"),
        ComVer(":", "qx", "DHWGroups"),

        ComVer("&", "你好，晓狩！", "All"),

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