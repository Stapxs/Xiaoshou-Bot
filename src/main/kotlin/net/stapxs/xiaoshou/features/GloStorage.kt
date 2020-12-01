package net.stapxs.xiaoshou.features

object GloStorage {

    // 存储一些需要跨指令使用的变量
    // ---------------------------------------------------------------------
    // 每日晚安记录
    class NightStorageVer(time:List<String>, qq:Long) {
        var sayNightTime:List<String> = time
        var qqID:Long = qq
        var sayTimes:Int = 1
    }
    var NightStorage:MutableList<NightStorageVer> = mutableListOf()

}