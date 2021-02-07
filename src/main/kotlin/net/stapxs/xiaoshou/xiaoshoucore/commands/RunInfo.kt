package net.stapxs.xiaoshou.xiaoshoucore.commands

import net.mamoe.mirai.event.events.GroupMessageEvent
import net.stapxs.xiaoshou.features.RunStat
import net.stapxs.xiaoshou.xiaoshoucore.Xiaoshou

suspend fun RunInfo(event: GroupMessageEvent) {
    val info = RunStat.getInfo()
    var str = "> 运行统计信息：\n━━━━━━━━━━━━\n"
    for(stat in info) {
        str += stat.optName + "：" + stat.optValue + "\n"
    }
    str += "━━━━━━━━━━━━"
    Xiaoshou.sendMessage("Group", str ,event)
}