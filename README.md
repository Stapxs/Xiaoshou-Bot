# 晓狩
你好……人类，这是晓狩。很高兴认识你！

## 这是啥
这是个基于 `Mirai Core` 的一个用于 QQ 的个人日常机器人。下面是自我介绍（？

~~~
你好，有什么要帮助的么，晓狩能帮你搞定！
或者陪你玩也行（小声），唔唔有什么问题也能问晓狩啦 ——
唔？不要看着我我我还没成年呢，才不会告诉你晓狩有多大 —— 
问女孩子年龄是不礼貌的（大声），欸这个问题么晓狩是2005年的哦 —— 欸！
哼！不理你！

晓狩……晓狩也刚刚认识你们呢，这个世界真的真的很精彩呢！
有一群有意思的伙伴们，可以一起聊天，一起玩耍的说 ——
反正，反正很高兴认识你！
~~~

## 获取 jar 包
<br>

![GitHub release (latest by date including pre-releases)](https://img.shields.io/github/v/release/Stapxs/Xiaoshou-Bot?include_prereleases&style=for-the-badge)
~~~
为了让你们看得整洁一点，晓狩才没有把乱七八糟的东西上传上来哦 ——
要用自己研究去（大声
~~~
因为没有上传 `build` 文件夹，所以你可以在 [Release](https://github.com/Stapxs/Xiaoshou-Bot/releases) 下载当前的 Release 版本，如果想要使用最新的代码，可以 clone 并且自行运行 Gradle 打包。

## 配置文件
~~~
看不懂看不懂，不要乱说话 ——
~~~

在当前版本，你需要预先在 jar 同目录初始化 `Options.ini` 文件，结构如下：
- [ 必须 ] qqID:QQ 账户
- [ 必须 ] qqPassword:QQ 密码
- [ 必须 ] masterID:最高权限主人的 QQ
- botAPILink:一个用于记录 Bot 运行状况的 API 链接，GET 传参一个 JSON
- groupList:名称,群号,群号
- groupList:名称,群号,群号
- [ 必须 ] nightTrigger:晚安功能的触发关键字（逗号隔开）
- [ 必须 ] goNight:多次触发晚安功能的回复（逗号隔开）

同时你需要在 jar 同目录初始化 `Options/nightSays.ini` 空文件（因为我忘记自动创建了），这个文件用于自行添加的存储晚安问候的句子。

接下来你需要将仓库里的 `Options/Commands.sconf` 下载到相同目录内，这个文件是命令权限表，你可以按照表上的结构自行编写：
- 命令类型 | 命令名称 | 命令类型权限组（逗号隔开） | 命令帮助

如果命令在此处删去，那么将在所有群组无权执行，`命令帮助` 填写 hidden 将在命令列表内隐藏，不影响执行，`命令权限组` 则是在 `Options.ini` 中的 groupList 名称。（此功能将在不久的将来更改）

在运行时会在 jar 同目录自动生成 `LogNow.log` 日志文件，用于输出当前的运行日志，而 `mirai` 的输出依旧在控制台输出。

## 能干啥
~~~
晓狩还在努力中！冲呀 ——
晓狩还在努力的观……学习人类喜欢的东西，努力的加入人类！
什么我说观什么？我才没说呢，肯定是你听错了……
你你你……我我我去看书了（
~~~
具体的功能列表可以在 `Options/Commands.sconf` 看见，懒得打字了（大声

## Bug Reply
~~~
你说什么东西，晓狩怎么看不明白？
emmmmm 我再去学学……
~~~
有 Bug 就直接往 [Issus](https://github.com/Stapxs/Xiaoshou-Bot/issues) 丢好了，不过会有人用晓狩么<span style="font-size:9px;">（超小声</span>

## 计画
~~~
写日记写日记……
欸，才不给你看（
真的要看么 —— 我藏起来咯，自己找去（逃
~~~
- [X] 摸鱼
- [X] 炸鱼
- [ ] 完整的 Wiki 获取功能
- [ ] 丘丘人语言翻译
- [ ] lib 图片库上传 / 获取

## API 和 开源项目
~~~
晓狩才不会白拿鱼干不说呢！
~~~
以下是 Xiaoshou 已存在功能用到的全部 API ，如果你是 API 作者并且 `不希望` 在晓狩中用到，那么可以发起 [Issus](https://github.com/Stapxs/Xiaoshou-Bot/issues) 请求删除。

- [Pixiv 图片下载](https://pixiv.cat)
- [Cat 猫猫图片库](https://thiscatdoesnotexist.com)
- [Dog 狗砸图片库](https://dog.ceo)
- [Fox 狐狸图片库](https://foxrudor.de)