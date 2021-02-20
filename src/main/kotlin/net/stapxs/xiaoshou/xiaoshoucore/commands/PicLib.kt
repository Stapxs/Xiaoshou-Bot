package net.stapxs.xiaoshou.xiaoshoucore.commands

import net.mamoe.mirai.event.events.GroupMessageEvent
import net.stapxs.xiaoshou.features.SSUserClass
import net.stapxs.xiaoshou.xiaoshoucore.Xiaoshou
import java.io.File

object PicLib {

    suspend fun jumper(event: GroupMessageEvent, msg: MutableList<String>) {
        when(msg[0]) {
            "getpic" -> get(event, msg[1])
        }
    }

    /**
     * @Author Stapxs
     * @Description 发送 lib 库内的图片
     * @Date 下午 07:09 2021/2/19
     * @Param
     * @return
    **/
    private suspend fun get(event: GroupMessageEvent, what: String) {
        var who = what
        var name = ""
        if(what.indexOf("/") > 0) {
            who = what.split("/")[0]
            name = what.split("/")[1]
        }
        if(name == "") {
            val fileNames: MutableList<String> = mutableListOf()
            //在该目录下走一圈，得到文件目录树结构
            val fileTree: FileTreeWalk = File("Pics/lib/$who").walk()
            fileTree.maxDepth(1)                      //需遍历的目录层次为1，即无须检查子目录
                .filter { it.isFile }                       //只挑选文件，不处理文件夹
                .forEach { fileNames.add(it.name) }         //循环 处理符合条件的文件
            fileNames.forEach(::println)
            // 随机抽取
            val back = Xiaoshou.sendImange("Group", File("Pics/Lib/$who/${SSUserClass.getOne(fileNames)}"), event)
            if(back != "OK") {
                Xiaoshou.sendMessage("Group", "发送失败：${back.split(" > ")[3]}", event)
            }
        } else {
            // 指定文件名
            val back = Xiaoshou.sendImange("Group", File("Pics/Lib/$who/$name"), event)
            if(back != "OK") {
                Xiaoshou.sendMessage("Group", "发送失败：${back.split(" > ")[3]}", event)
            }
        }
    }

}