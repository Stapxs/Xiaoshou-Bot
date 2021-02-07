package net.stapxs.xiaoshou.xiaoshoucore.commands

import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.content
import net.stapxs.xiaoshou.features.Log
import net.stapxs.xiaoshou.features.Options
import net.stapxs.xiaoshou.xiaoshoucore.Xiaoshou

suspend fun OtherJump(event: GroupMessageEvent) {

    // 因为其他的触发句子很难规范化的去判断，所以单独写了个 jumper 来保证尽量整齐点

    // 单句返回
    if(CommandList.hasAuthority("&", event.message.content, event.group.id))
        for (re: OneBack.SayVer in OneBack.sayList)
            if(re.comSay == event.message.content) {
                Log.addLog("xiaoshou", "执行指令：& => ${event.message.content}")
                Xiaoshou.sendMessage("Group", re.comBack, event)
                return
            }

    // 晚安问候
    if(Options.getOpt("nightTrigger") != "Err") {
        for (says in Options.getOpt("nightTrigger").split(","))
            if (says == event.message.content) {
                Log.addLog("xiaoshou", "执行指令：& => 晚安问候")
                Xiaoshou.sendMessage("Group", Night.sendNight(event.sender.id), event, false, true)
                return
            }
    } else {
        Log.addLog("xiaoshou", "Err <- 缺失配置：nightTrigger")
    }
}