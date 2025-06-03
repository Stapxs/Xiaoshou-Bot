import OnebotClient from './client/onebotClient'
import MsgEvent from './event/msgEvent'
import log4js from 'log4js'
import database from 'better-sqlite3'
import fs from 'fs'
import initDatabase from './db/initDb'
import path from 'path'
import readline from 'readline'
import { getMsgEvent } from './utils/decorators/msgDec'
import { runCommand } from './utils/msg'
import DataGetter from './utils/data'

// 初始化数据库 ========================
const dbFile = path.join(__dirname, '../database.db')
// 检查数据库文件是否存在
if (!fs.existsSync(dbFile)) {
    fs.writeFileSync(dbFile, '', { encoding: 'utf-8' })
}

export let data = {} as DataGetter
export const db = new database(dbFile)
initDatabase(db)

export const config = {} as { [key: string]: any }
// 读取设置 ========================
/* eslint-disable */
export const botConfig = db.prepare('SELECT * FROM bot_config').all() as {
    key: string,
    value: string
}[]
if (botConfig.length == 0) {
    // 进行控制台询问
    console.log('> 在数据库中没有找到有效的配置项，请进行配置')
    console.log('')

    const rl = readline.createInterface({
        input: process.stdin,
        output: process.stdout,
        terminal: false
    })
    rl.question('websocket 服务器地址：', (address) => {
        rl.question('token：', (token) => {
            rl.question('日志等级（debug/info/warn/error）：', (log) => {
                rl.question('主管理员账号：', (admin) => {
                    db.prepare('INSERT INTO bot_config (key, value) VALUES (?, ?)').run('address', address)
                    db.prepare('INSERT INTO bot_config (key, value) VALUES (?, ?)').run('token', token)
                    db.prepare('INSERT INTO bot_config (key, value) VALUES (?, ?)').run('log', log)
                    db.prepare('INSERT INTO bot_config (key, value) VALUES (?, ?)').run('admin', admin)
                    rl.close()
                    console.log('配置完成！请重新启动程序')
                    process.exit(0)
                })
            })
        })
    })

} else {
    for (const index in botConfig) {
        const key = botConfig[index].key
        const value = botConfig[index].value
        config[key] = value
    }

    log4js.configure({
        appenders: {
            console: { type: 'console' },
            terminal: {
                type: 'console',
                layout: {
                    type: 'pattern',
                    pattern: '\x1b[35m[%d{ISO8601}] [%p] %c - %m\x1b[0m'
                }
            }
        },
        categories: {
            default: {
                appenders: ['console'],
                level: config.log
            },
            terminal: {
                appenders: ['terminal'],
                level: config.log
            }
        }
    })
    /* eslint-disable */

    // 注册命令
    new MsgEvent()

    const logger = log4js.getLogger('index')
    const commandLogger = log4js.getLogger('terminal')
    logger.info('当前数据库文件：' + dbFile)
    logger.info('当前日志等级：' + log4js.getLogger('default').level)
    logger.info('如需重新配置，请删除数据库文件中的 bot_config 表')

    const rlCommand = readline.createInterface({
        input: process.stdin,
        output: process.stdout,
        terminal: false
    })
    commandLogger.info('命令行模式已启动，使用 "exit" 退出程序')
    commandLogger.info('输入以 "/" 开头的命令与晓狩交互，命令行消息将使用紫色 command 类型日志输出')
    rlCommand.on('line', async (line) => {
        const str = line.trim()
        // 清理上一行的输出
        readline.moveCursor(process.stdout, 0, -1)
        readline.clearLine(process.stdout, 0)
        readline.cursorTo(process.stdout, 0)

        if(str === 'exit') {
            process.exit(0)
        }

        if (str.startsWith('/')) {
            console.log(`$ ${str.slice(1)}`)
            runCommand(client, str.slice(1), null)
        } else {
            console.log(`${str}`)
        }
    })

    let reConnect = true
    const client = new OnebotClient({ address: config.address, token: config.token },
        {
            onOpen: () => {
                logger.info('websocket 已连接')
                client.send(JSON.stringify({
                    action: 'get_version_info',
                    echo: 'get_version_info'
                }))
                client.send(JSON.stringify({
                    action: 'get_login_info',
                    echo: 'get_login_info'
                }))
            },
            onMessage: (data) => {
                let msg = undefined as { [key: string]: any } | undefined
                let echoList = undefined
                let head = undefined
            
                try {
                    msg = JSON.parse(data)
                    if (msg) {
                        if (msg.echo !== undefined) {
                            echoList = msg.echo.split('_')
                            head = echoList[0]
                        } else {
                            let type = msg.post_type
                            if (type == 'notice') {
                                // 通知类型，如果没有 sub_type 则使用 notice_type
                                type = msg.sub_type ?? msg.notice_type
                            }
                            head = type
                        }
                        const cmd = getMsgEvent(head)
                        if (cmd) {
                            cmd.handler(client, head, msg, echoList)
                        } else {
                            logger.debug(`未注册的消息类型 ${head} - ${data}`)
                        }
                    }
                } catch (e) {
                    logger.error(e, `处理消息或通知 ${head} 错误 - ${data}`)
                }
            },
            onError: (err) => {
                logger.error('WebSocket 连接错误：' + err)
                reConnect = false
                logger.error('Websocker 连接出现错误，将不再尝试重新连接')
            },
            onClose: (code, reason) => {
                switch (code) {
                case 1000: logger.info('Onebot 服务器正常关闭连接：' + reason); break
                case 1006: {
                    logger.error('Onebot 服务器异常关闭连接：' + reason)
                    if(reConnect) {
                        logger.info('尝试重新连接 Onebot 服务器...')
                        client.connect()
                    }
                    break
                }
                default: logger.error('Onebot 服务器关闭连接：' + reason)
            }
        }
    })
    data = new DataGetter(client)

    client.connect()
}
