package net.stapxs.xiaoshou.xiaoshoucore.commands

import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.content
import net.stapxs.xiaoshou.features.Log
import net.stapxs.xiaoshou.features.Options
import net.stapxs.xiaoshou.xiaoshoucore.Monitor

suspend fun OtherJump(event: GroupMessageEvent) {

    // 因为其他的触发句子很难规范化的去判断，所以单独写了个 jumper 来保证尽量整齐点

    // 单句返回
    if(CommandList.hasAuthority("&", event.message.content, event.group.id))
        for (re: OneBack.SayVer in OneBack.sayList)
            if(re.comSay == event.message.content) {
                Log.addLog("xiaoshou", "执行指令：& => ${event.message.content}")
                Monitor.messageSender("Group", event.group.id, re.comBack)
                return
            }

    // 晚安问候
    if(CommandList.hasAuthority("&", "晚安问候", event.group.id)) {
        if (Options.getOpt("nightTrigger") != "Err") {
            for (says in Options.getOpt("nightTrigger").split(","))
                if (says == event.message.content) {
                    Log.addLog("xiaoshou", "执行指令：& => 晚安问候")
                    Monitor.messageSender("Group", event.group.id, Night.sendNight(event.sender.id), event.sender.id, event, false )
                    return
                }} else {
            Log.addLog("xiaoshou", "Err <- 缺失配置：nightTrigger")
            return
        }
    }

    // 机器人
    Log.addLog("debug", event.message[At].toString())
    if(CommandList.hasAuthority("&", "机器人", event.group.id) && event.message[At].toString() != "null"){noBot(event);return}
}