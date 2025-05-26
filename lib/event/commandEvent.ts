import McClient from '../client/mcClinet'
import { db } from '../index'
import OnebotClient from '../client/onebotClient'
import log4js from 'log4js'
import * as MessagereUtil from '../utils/msg'
import { bindCommandHandlers, command, getAllCommands } from '../utils/decorators/commandDec'
import { getMcCommandEvent } from '../utils/decorators/mcDec'
import McCommandEvent from './mcCommandEvent'
import yargs from 'yargs'
import { resolveSrvToIPs } from '../utils/http'
import { permission } from '../utils/decorators/commandPermissionDec'

export default class CommandEvent {
    private _ = new McCommandEvent()
    
    private logger = log4js.getLogger('command')
    private mcClientMap: { [key: string]: McClient } = {}

    constructor() {
        bindCommandHandlers(this)
    }

    @command('hi', '测试命令')
    @permission('master')
    async hiCommand() {
        return '你好人类，这是晓狩！'
    }

    @command('mc')
    async mc() {
        const commandList = Array.from(getAllCommands().keys())
            .filter(key => key[0]?.startsWith('mc.'))
            .reduce((map, key) => {
                if(key[1] !== '')
                    map.set(key[0].split('.')[1], key[1])
                return map
            }, new Map<string, string>())

        const cli = yargs()
            .help(false)
            .version(false)
            .scriptName('mc')
            .usage('$0 <command> [options]')

        Array.from(commandList.entries()).forEach(([name, desc]) => {
            cli.command(name, desc, (yargs) => {
                return yargs
            })
        })

        const data = await cli.getHelp()
        return data
    }

    @command('mc.player', '获取玩家列表')
    async mcPlayer(client: OnebotClient, msg: { [key: string]: any }) {
        const id = msg.group_id
        if(!id) {
            return 'mc 指令只能在群组中使用'
        }
        const mcConfig = this.loadMcConfig(id)
        if (!mcConfig) {
            return '请先配置 Minecraft 服务器'
        }
        const bot = await this.joinMcWorld(this.mcClientMap[id], client, msg, mcConfig)
        if (bot) {
            const playerList = bot.players
            let result = '> 玩家列表\n\n'
            for (const name in playerList) {
                const player = playerList[name]
                result += `  ${player.displayName}\n`
            }
            result += '\n 共有 ' + Object.keys(playerList).length + ' 个玩家在线'
            return result
        }
        return '获取玩家列表失败'
    }

    @command('mc.scoreboard', '获取计分板数据')
    @command('mc.scoreboard.*')
    async mcScoreboard(client: OnebotClient, msg: { [key: string]: any }, args: { [key: string]: any }) {
        if(!args._[2]) {
            return '请指定计分板名称或使用 list 来获取计分板列表'
        }
        const id = msg.group_id
        if(!id) {
            return 'mc 指令只能在群组中使用'
        }
        const mcConfig = this.loadMcConfig(id)
        if (!mcConfig) {
            return '请先配置 Minecraft 服务器'
        }
        const bot = await this.joinMcWorld(this.mcClientMap[id], client, msg, mcConfig)
        if (bot) {
            if (args._[2] === 'list') {
                bot.chat('/scoreboard objectives list')
            } else {
                bot.chat('/scoreboard objectives setdisplay sidebar ' + args._[2])
            }
        }
    }

    @command('mc.config', '配置 Minecraft 服务器连接参数')
    @command('mc.config.*')
    async mcConfig(_: OnebotClient, msg: { [key: string]: any }, args: { [key: string]: any }) {
        let id = args._[2]
        if (!id || args.help) {
            const cli = yargs()
                .help(false)
                .version(false)
                .scriptName('mc')
                .usage('$0 config <group_id> [options]')
                .command(
                    'config <group_id>',
                    '配置 Minecraft 服务器连接参数',
                    (yargs) => {
                        return yargs.positional('id',
                            { describe: '生效群号', type: 'string', demandOption: true })
                    }
                )
                .option('auth', {
                    describe: '验证方式 (custom / mojang)', type: 'string', default: 'mojang', demandOption: true
                })
                .option('version', {
                    describe: '版本，默认自动检查；你可以填写 "forge" 来进入 forge 服务器特殊模式', type: 'string'
                })
                .option('auth_server', {
                    describe: 'Yggdrasil auth server 地址（custom 认证时使用）', type: 'string'
                })
                .option('session_server', {
                    describe: 'Yggdrasil session server 地址（custom 认证时使用）', type: 'string'
                })
                .option('username', {
                    describe: '账号（用户名或邮箱）', type: 'string', demandOption: true
                })
                .option('password', {
                    describe: '密码', type: 'string', demandOption: true
                })
                .option('name', {
                    describe: '游戏内昵称', type: 'string'
                })
                .option('host', {
                    describe: '服务器地址', type: 'string', demandOption: true
                })
                .option('port', {
                    describe: '服务器端口', type: 'number', default: 25565
                })
                .epilog('为了防止密码泄露，本命令只支持在私聊使用。注意：本功能只支持原版、 Forge 服务器以及部分插件服务器，其他服务器可能无法使用。')
            const data = await cli.getHelp()
            return data
        } else {
            id = id.toString()
        }
        if (msg.group_id) {
            return 'mc config 命令只能在私聊中使用'
        }
        const mcConfig = this.loadMcConfig(id)
        const mustKey = ['username', 'password', 'name', 'host']
        // 检查 args 是否包含所有必须的键
        for (const key of mustKey) {
            if (!(key in args)) {
                return '缺少必要的参数：' + key
            }
        }
        const config = 'INSERT INTO mc_config (group_id, auth, version, auth_server, session_server, username, password, name, host, port) VALUES (@group_id, @auth, @version, @auth_server, @session_server, @username, @password, @name, @host, @port)'
        if (mcConfig && mcConfig.length != 0) {
            db.prepare('DELETE FROM mc_config WHERE group_id = ?').run(id)
        }
        try {
            db.prepare(config).run({
                group_id: id,
                port: args.port ?? 25565,
                ...args
            })
        } catch (e) {
            this.logger.error('配置数据库失败：' + e)
            return '保存配置失败，请检查参数'
        }
        return '配置成功！'
    }

    // ===========================================

    /**
     * 加载配置
     * @param id 群组 ID
     * @returns 配置对象
     */
    private loadMcConfig(id: string) {
        // 读取配置
        const mcConfig = db.prepare('SELECT * FROM mc_config WHERE group_id = ?').all(id.toString())

        const mcFConfig = mcConfig[0] as { [key: string]: any }
        // 将所有类似 auth_server 的参数转换为 authServer
        for (const key in mcFConfig) {
            if (key.includes('_')) {
                const newKey = key.replace(/_([a-z])/g, (match, letter) => letter.toUpperCase())
                mcFConfig[newKey] = mcFConfig[key]
                delete mcFConfig[key]
            }
        }

        return mcFConfig
    }

    private async joinMcWorld(mcClient: McClient, client: OnebotClient, msg: { [key: string]: any }, mcConfig: { [key: string]: any }) {
        const logger = log4js.getLogger('minecraft')
        let needJoin = false
        if (!mcClient) {
            const ips = await resolveSrvToIPs('_minecraft._tcp.' + mcConfig.host)
            if(ips.length > 0) {
                mcConfig.host = ips[0].ip
                mcConfig.port = ips[0].port
            }
            if(mcConfig.version == 'forge') {
                mcConfig.version = false
            }
            mcClient = new McClient(mcConfig)
            this.mcClientMap[msg.group_id] = mcClient
            needJoin = true
        } else if (!mcClient.isInGame()) {
            needJoin = true
        }
        if (needJoin) {
            const bot = await mcClient.joinAsync()
            if (bot) {
                bot.waitForChunksToLoad()
                bot.addListener('message', (jsonMsg: any) => {
                    if (jsonMsg.text != undefined && jsonMsg.text != '') {
                        logger.debug(MessagereUtil.replaceMsg(jsonMsg.text))
                    } else {
                        logger.debug(jsonMsg.toString())
                    }

                    try {
                        let type = jsonMsg.translate
                        if (!type && jsonMsg.json.extra && jsonMsg.json.extra.length > 0) {
                            type = jsonMsg.json.extra[0].translate
                        }

                        const cmd = getMcCommandEvent(type)
                        if (cmd) {
                            const says = cmd.handler(bot, client, msg, jsonMsg)
                            if (says) {
                                client.sendChatMsg(says, msg)
                            }
                        } else if(type) {
                            logger.debug('未注册的 Minecraft 指令类型：' + type + '\n' + JSON.stringify(jsonMsg))
                        }
                    } catch (e) {
                        logger.error(e, '处理 Minecraft 消息错误：' + JSON.stringify(jsonMsg))
                    }
                })
                bot.addListener('death', () => {
                    this.logger.error('Bot 意外死亡，请手动上线检查')
                    mcClient?.leave()
                })
            }
        }
        return mcClient.bot
    }
}