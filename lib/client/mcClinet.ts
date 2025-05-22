import mineflayer, { BotOptions } from 'mineflayer'
import mc from 'minecraft-protocol'
// @ts-expect-error: minecraft-protocol-forge has no types
import { autoVersionForge } from 'minecraft-protocol-forge'
import log4js from 'log4js'

export default class McClient {

    public bot: mineflayer.Bot | null = null
    public static isOp = true
    public spawn = false

    private logger = log4js.getLogger('minecraft')
    private config

    constructor(config: any) {
        // 配置初始值
        this.config = config
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
            this.config.client = mc.createClient(this.config as BotOptions)
        }
        // Forge 登录模式
        if(this.config.version == false) {
            this.config.client = this.config.client ?? mc.createClient(this.config as BotOptions)
            autoVersionForge(this.config.client)
        }
    }

    public joinAsync(): Promise<mineflayer.Bot> {
        this.logger.info('正在加入 Minecraft 服务器 ……')
        // 初始连接配置
        this.bot = mineflayer.createBot(this.config as BotOptions)
        this.bot.addListener('end', (str: string) => {
            this.logger.info('从 Minecraft 服务器断开连接：' + str)
            this.spawn = false
            this.bot = null
        })
        this.bot.addListener('error', (err: Error) => {
            this.logger.error('连接 Minecraft 服务器失败：' + err.message)
            this.spawn = false
            this.bot = null
        })
        this.bot._client.on('end', (str) => {
            this.logger.debug('client 断开连接：' + str)
        })
        this.bot._client.on('error', (err) => {
            this.logger.error('client 连接失败：' + err.message)
        })

        return new Promise((resolve, reject) => {
            if(this.bot) {
                this.bot.once('spawn', () => {
                    this.spawn = true
                    resolve(this.bot!)
                })

                this.bot.once('error', (err) => {
                    this.logger.error('连接 Minecraft 服务器失败：' + err.message)
                    reject(err)
                })
            } else {
                reject(new Error('Bot is null'))
            }
        })
    }


    public leave() {
        this.logger.info('正在退出 Minecraft 服务器 ……')
        this.bot?.quit()
    }

    public isInGame() {
        return this.bot != null && this.spawn
    }
}