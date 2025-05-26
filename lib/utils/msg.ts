import shellQuote from 'shell-quote'
import parseArgs from 'yargs-parser'
import { getCommand } from './decorators/commandDec'
import log4js from 'log4js'
import OnebotClient from '../client/onebotClient'
import CommandEvent from '../event/commandEvent'
import SysCommandEvent from '../event/system/sysCommandEvent'
import { getPermission } from './decorators/commandPermissionDec'
import { botConfig } from '..'

new SysCommandEvent()
new CommandEvent()

/**
 * ### mc messages
 * 替换字符串中的颜色代码为对应的 ANSI 颜色码
 * @param msg 需要替换的字符串
 * @returns 替换后的字符串
 */
export function replaceMsg(msg: string) {
    const ansiMap: Record<string, string> = {
        '0': '\x1b[30m', '1': '\x1b[34m', '2': '\x1b[32m', '3': '\x1b[36m',
        '4': '\x1b[31m', '5': '\x1b[35m', '6': '\x1b[33m', '7': '\x1b[37m',
        '8': '\x1b[90m', '9': '\x1b[94m', 'a': '\x1b[92m', 'b': '\x1b[96m',
        'c': '\x1b[91m', 'd': '\x1b[95m', 'e': '\x1b[93m', 'f': '\x1b[97m',
        'k': '\x1b[8m', 'l': '\x1b[1m', 'm': '\x1b[9m', 'n': '\x1b[4m',
        'o': '\x1b[3m', 'r': '\x1b[0m',
    }
    return msg.replace(/§([0-9a-frk-or])/gi, (_, code) => ansiMap[code.toLowerCase()] || '') + '\x1b[0m';
}

/**
 * ### onebot messages
 * 执行命令
 * @param client OnebotClient 实例
 * @param str 命令字符串
 * @param msg 消息对象
 */
export async function runCommand(client: OnebotClient, str: string, msg: { [key: string]: any } | null) {
    const logger = log4js.getLogger('message')
    try {
        const rawTokens = shellQuote.parse(str)
        const tokens = rawTokens.filter((t): t is string => typeof t === 'string')
        const argv = parseArgs(tokens)
        const command = argv._.join('.')
        const commandSub = argv._.slice(0, -1).join('.') + '.*'

        // 获取命令和权限
        const cmd = getCommand(command) || getCommand(commandSub)
        const permission = getPermission(command) || getPermission(commandSub)

        // 检查权限
        const senderId = msg?.sender?.user_id
        let allowed = true
        switch (permission) {
            case 'master': {
                const masterId = botConfig.find((item) => {
                    return item.key === 'admin'
                })?.value
                if(senderId == undefined || senderId !== Number(masterId)) {
                    allowed = false
                }
                break
            }
            case 'admin': {
                // PS：admin 权限只在群组中生效
            }
        }

        // 执行命令
        if(!allowed) {
            client.sendChatMsg('你没有权限执行此命令，命令需求: ' + permission, msg)
        } else {
            if(msg) {
                logger.debug(`执行命令: ${msg.group_id ?? msg.user_id} - ${command}/${commandSub}`)
            } else {
                logger.debug(`执行命令: command - ${command}/${commandSub}`)
            }
            if (cmd) {
                const says = await cmd.handler(client, msg, argv)
                if (says) {
                    client.sendChatMsg(says, msg)
                }
            } else {
                if(msg) {
                    logger.debug(`未注册的命令 ${command} - ${msg.raw_message}`)
                } else {
                    logger.debug(`未注册的命令 ${command} - ${str}`)
                }
            }
        }
    } catch (err) {
        logger.error(`执行命令失败: ${str}\n`, err)
    }
}