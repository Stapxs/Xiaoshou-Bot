package net.stapxs.xiaoshou.features

object GroupList {

    class GroupVer(name:String, value:List<Long>) {
        val groupName:String = name
        val groupID:List<Long> = value
    }
    private var GroupLists:MutableList<GroupVer> = mutableListOf()
    private var FriendLists:MutableList<GroupVer> = mutableListOf()

    var hasGroupLists = false
    var hasFriendLists = false

    fun initGroupList() {
        Log.addLog("group", "正在初始化权限组……")
        for(opt in Options.optList) {
            if(opt.optName == "groupList") {
                hasGroupLists = true
                val list = opt.optValue.split(",")
                val iDList:MutableList<Long> = mutableListOf()
                for(i in 1 until list.count()) {
                    iDList.add(list[i].toLong())
                }
                GroupLists.add(GroupVer(list[0], iDList))
            }
            else if(opt.optName == "friendList") {
                hasFriendLists = true
                val list = opt.optValue.split(",")
                val listl:MutableList<Long> = mutableListOf()
                for(i in 1 until list.count()) {
                    listl.add(list[i].toLong())
                }
                FriendLists.add(GroupVer(list[0], listl))
            }
        }
    }

    fun isInGroupList(name: String, idin:Long): Boolean {
        for(info in GroupLists) {
            if(info.groupName == name) {
                for(id in info.groupID) {
                    if(id == idin) {
                        return true
                    }
                }
            }
        }
        return false
    }

}