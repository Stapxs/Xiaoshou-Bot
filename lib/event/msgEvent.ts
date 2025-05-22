import log4js from 'log4js'
import OnebotClient from '../client/onebotClient'
import { msgEvent } from '../utils/decorators/msgDec'
import shellQuote from 'shell-quote'
import parseArgs from 'yargs-parser'
import { getCommand } from '../utils/decorators/commandDec'
import CommandEvent from './commandEvent'

export default class MsgEvent {
    private logger = log4js.getLogger('message')
    private commandEvent = new CommandEvent()

    @msgEvent('message')
    @msgEvent('message_sent')
    async message(client: OnebotClient, name: string, msg: { [key: string]: any }) {
        if (msg.raw_message.startsWith('/')) {
            const rawTokens = shellQuote.parse(msg.raw_message.slice(1))
            const tokens = rawTokens.filter((t): t is string => typeof t === 'string')
            const argv = parseArgs(tokens)
            const command = argv._.join('.')
            const commandSub = argv._.slice(0, -1).join('.') + '.*'

            const cmd = getCommand(command) ?? getCommand(commandSub)
            this.logger.debug(`执行命令: ${msg.group_id ?? msg.user_id} - ${command}/${commandSub}`)
            if (cmd) {
                const says = await cmd.call(this.commandEvent, client, msg, argv)
                if (says) {
                    client.sendMsg(says, msg)
                }
            } else {
                this.logger.debug(`未注册的命令 ${command} - ${msg.raw_message}`)
            }
        }
    }

    @msgEvent('meta_event')
    metaEvent() {
        // do nothing
    }

    @msgEvent('getVersionInfo')
    versionInfo(client: OnebotClient, name: string, msg: { [key: string]: any }) {
        this.logger.info(`Bot 信息：${msg.data.app_name} ${msg.data.app_version}`)
    }
}