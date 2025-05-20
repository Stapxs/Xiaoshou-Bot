import mineflayer, { BotOptions } from 'mineflayer'
import mc from 'minecraft-protocol'
import Config from './system/config'
import log4js from 'log4js'
import regEvents from './bot/event'

export default class Cilent {

    public bot: mineflayer.Bot | null = null
    public static isOp = true
    public spawn = false

    private cilentLogger = log4js.getLogger('cilent')
    private botLogger = log4js.getLogger('bot')
    private config

    constructor(config: Config) {
        this.cilentLogger.level = config.get('logLevel')
        this.botLogger.level = config.get('logLevel')
        this.cilentLogger.info('正在初始化 mineflayer 服务 ……')
        // 配置初始值
        this.config = config.getConfig()
        this.config.username = this.config.username ?? 'Player'
        this.config.version = this.config.version ?? false
        this.config.plugins = this.config.plugins ?? {}
        this.config.hideErrors = this.config.hideErrors ?? false
        this.config.logErrors = this.config.logErrors ?? true
        this.config.loadInternalPlugins = this.config.loadInternalPlugins ?? true
        this.config.client = this.config.client ?? null
        this.config.brand = this.config.brand ?? 'vanilla'
        this.config.respawn = this.config.respawn ?? true
        // 判断登录类型
        const authInfo = this.config.auth
        if(authInfo && authInfo == 'custom') {
            // 追加第三方登录设置
            this.config.auth = 'mojang'
            this.config.cilent = mc.createClient(this.config as BotOptions)
        }
    }

    public join() {
        this.botLogger.info('正在加入 Minecraft 服务器 ……')
        // 初始连接配置
        this.bot = mineflayer.createBot(this.config as BotOptions)
        this.bot.addListener('end', (str: string) => {
            // PS：它貌似有时候会误报 colsed
            if(str != 'socketClosed') {
                this.botLogger.info('从 Minecraft 服务器断开连接：' + str)
                this.spawn = false
                this.bot = null
            }
        })
        this.bot.once('spawn', () => {
            this.spawn = true
            if (this.bot) {
                this.botLogger.info('加载世界完成')
                this.bot.waitForChunksToLoad()

                // 测试是否有 OP 权限
                this.bot.chat('/op ' + this.bot.username)
                this.bot.chat('/list')

                this.bot.addListener('death', () => {
                    console.log("死了啦哎呦 ——")
                })
                regEvents(this.bot)
            }
        })
    }

    public leave() {
        this.botLogger.info('正在退出 Minecraft 服务器 ……')
        this.bot?.quit()
    }
}