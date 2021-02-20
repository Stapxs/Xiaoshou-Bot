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

    // 命令错误语句（不想读设置了，写死写死）
    var comErrSay:MutableList<String> = mutableListOf(
        "你在说啥？听不懂欸 XD",
        "不认识这个指令呢",
        "指令错了……不想理你……",
        "喵喵喵？是不懂的指令。",
        "再说一遍……真的是这个指令么。"
    )

}