package net.stapxs.xiaoshou.xiaoshoucore.commands

object OneBack {

    class SayVer(say:String, back:String) {
        val comSay:String = say
        val comBack:String = back

    }
    var sayList:List<SayVer> = mutableListOf(
        SayVer("你好，晓狩！", "你好，人类 ——，这是晓狩！")
    )

}