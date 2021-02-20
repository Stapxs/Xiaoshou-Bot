package net.stapxs.xiaoshou.features

import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object SSUserClass {

    /**
     * @Author Stapxs
     * @Description 将 byte大小 转为合适的可读表示方式
     * @Date 下午 04:35 2020/11/23
     * @Param
     * @return
    **/
    fun getNetFileSizeDescription(size: Long): String? {
        val bytes = StringBuffer()
        val format = DecimalFormat("###.0")
        if (size >= 1024 * 1024 * 1024) {
            val i = size / (1024.0 * 1024.0 * 1024.0)
            bytes.append(format.format(i)).append("GB")
        } else if (size >= 1024 * 1024) {
            val i = size / (1024.0 * 1024.0)
            bytes.append(format.format(i)).append("MB")
        } else if (size >= 1024) {
            val i = size / 1024.0
            bytes.append(format.format(i)).append("KB")
        } else if (size < 1024) {
            if (size <= 0) {
                bytes.append("0B")
            } else {
                bytes.append(size.toInt()).append("B")
            }
        }
        return bytes.toString()
    }

    /**
     * @Author Stapxs
     * @Description 将 byte大小 转为指定的可读表示方式
     * @Date 下午 04:34 2020/11/23
     * @Param
     * @return
    **/
    fun getNetFileSizeDescription(size: Long, type: String): String? {
        val bytes = StringBuffer()
        val format = DecimalFormat("###.0")
        if (size >= 1024 * 1024 * 1024 && type == "GB") {
            val i = size / (1024.0 * 1024.0 * 1024.0)
            bytes.append(format.format(i)).append("GB")
        } else if (size >= 1024 * 1024 && type == "MB") {
            val i = size / (1024.0 * 1024.0)
            bytes.append(format.format(i)).append("MB")
        } else if (size >= 1024 && type == "KB") {
            val i = size / 1024.0
            bytes.append(format.format(i)).append("KB")
        } else if (size < 1024 && type == "B") {
            if (size <= 0) {
                bytes.append("0B")
            } else {
                bytes.append(size.toInt()).append("B")
            }
        }
        return bytes.toString()
    }

    /**
     * @Author Stapxs
     * @Description 获取时间差
     * @Date 下午 04:34 2020/11/23
     * @Param
     * @return
    **/
    fun getDistanceTime(time1: Long, time2: Long): String? {
        var day: Long = 0
        var hour: Long = 0
        var min: Long = 0
        var sec: Long = 0
        val diff: Long = if (time1 < time2) {
            time2 - time1
        } else {
            time1 - time2
        }
        day = diff / (24 * 60 * 60 * 1000)
        hour = diff / (60 * 60 * 1000) - day * 24
        min = diff / (60 * 1000) - day * 24 * 60 - hour * 60
        sec = diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60
        if (day != 0L) return day.toString() + "天" + hour + "小时" + min + "分钟" + sec + "秒"
        if (hour != 0L) return hour.toString() + "小时" + min + "分钟" + sec + "秒"
        if (min != 0L) return min.toString() + "分钟" + sec + "秒"
        return if (sec != 0L) sec.toString() + "秒" else "0秒"
    }

    /**
     * @Author Stapxs
     * @Description 保存图片到指定目录
     * @Date 下午 06:24 2020/11/23
     * @Param
     * @return
    **/
    fun downloadImg(url: String, to: String) {
        var conn : HttpURLConnection? = null
        conn = URL(url).openConnection() as HttpURLConnection       //建立链接
        conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
        conn.connect()      //打开输入流
        conn.inputStream.use { input ->
            BufferedOutputStream(FileOutputStream(to)).use { output ->
                input.copyTo(output)
            }
        }
        conn.disconnect()
    }

    /**
     * @Author Stapxs
     * @Description 获取时间相关的用于命名文件的字符串
     * @Date 下午 06:39 2020/11/23
     * @Param
     * @return
    **/
    fun getTimeAdd(): String {
        val sdf = SimpleDateFormat("HHmmssSSSS")
        return sdf.format(Date())
    }

    /**
     * @Author Stapxs
     * @Description 获取标准格式的日期
     * @Date 下午 05:07 2020/12/1
     * @Param
     * @return
    **/
    fun getDayList(): List<String> {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(Date()).split("-")
    }

    /**
     * @Author Stapxs
     * @Description 在字符串列表里瞎几把抽一个
     * @Date 下午 05:09 2020/12/1
     * @Param
     * @return
    **/
    fun getOne(listIn: List<String>): String {
        return listIn[(0 until listIn.count()).random()]
    }

    /**
     * @Author Stapxs
     * @Description 获取小时
     * @Date 下午 05:12 2020/12/1
     * @Param
     * @return
    **/
    fun getTimeH(): String {
        val sdf = SimpleDateFormat("HH")
        return sdf.format(Date())
    }

    /**
     * @Author Stapxs
     * @Description 合并字符串 List 的一部分并返回这部分
     * @Date 下午 07:04 2021/2/10
     * @Param
     * @return
    **/
    fun mergeStringList(list: MutableList<String>, start: Int, end: Int, add: String): String {
        if(start < 0 || end < 0 || start >= end || end > list.count() - 1) {
            return "Err > SSUserClass.kt > fun mergeStringList > 输入值不规范"
        }
        var now = 0
        var out = ""
        for(str in list) {
            if(now < start) {
                now ++
            } else if(now in start..end) {
                out += str + add
                now ++
            } else {
                break
            }
        }
        return out.substring(0, out.length - add.length)
    }

}