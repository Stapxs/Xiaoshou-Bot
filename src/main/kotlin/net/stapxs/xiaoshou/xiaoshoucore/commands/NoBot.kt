package net.stapxs.xiaoshou.xiaoshoucore.commands

import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.data.content
import net.stapxs.xiaoshou.features.Log
import net.stapxs.xiaoshou.features.SSUserClass
import net.stapxs.xiaoshou.xiaoshoucore.Monitor

suspend fun noBot(event: GroupMessageEvent) {

    val saysList: List<String> = mutableListOf(
        "你才是机器人！",
        "晓狩才不是机器人",
        "不-是-机-器-人-",
        "机器人才不会理你！",

        "嗯……人类。",
        "你好人类……",
        "这个人类和我说话了（记笔记）",
        "这个人类好奇怪（记笔记）",

        "（不想理你）",
        "……",

        "我是晓狩！",
        "这是晓狩……",

        "Err 对象不是人类",

        " ",
        " ",
        " "
    )

    if(event.message.content.indexOf("机器人") >= 0) {
        Log.addLog("xiaoshou", "执行指令：& => 才不是机器人")
        val say = SSUserClass.getOne(saysList)
        if(say != " ") {
            Monitor.messageSender("Group", event.group.id, say, event.sender.id, event, false)
        }
    }

}