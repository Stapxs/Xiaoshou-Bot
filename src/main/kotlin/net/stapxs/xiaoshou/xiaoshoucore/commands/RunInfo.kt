package net.stapxs.xiaoshou.xiaoshoucore.commands

import net.stapxs.xiaoshou.features.RunStat
import net.stapxs.xiaoshou.xiaoshoucore.Monitor

suspend fun RunInfo(id: Long) {
    val info = RunStat.getInfo()
    var str = "> 运行统计信息：\n━━━━━━━━━━━━\n"
    for(stat in info) {
        str += stat.optName + "：" + stat.optValue + "\n"
    }
    str += "━━━━━━━━━━━━"
    Monitor.messageSender("Group", id, str)
}