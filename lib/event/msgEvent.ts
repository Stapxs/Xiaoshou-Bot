import log4js from 'log4js'
import OnebotClient from '../client/onebotClient'
import { bindMsgHandlers, msg } from '../utils/decorators/msgDec'
import { runCommand } from '../utils/msg'

export default class MsgEvent {
    private logger = log4js.getLogger('message')

    constructor() {
        bindMsgHandlers(this)
    }

    @msg('message')
    @msg('message_sent')
    async message(client: OnebotClient, name: string, msg: { [key: string]: any }) {
        if (msg.raw_message.startsWith('/')) {
            runCommand(client, msg.raw_message.slice(1), msg)
        }
    }
    @msg('meta_event')
    metaEvent() {
        // do nothing
    }

    @msg('getVersionInfo')
    versionInfo(client: OnebotClient, name: string, msg: { [key: string]: any }) {
        this.logger.info(`Bot 信息：${msg.data.app_name} ${msg.data.app_version}`)
    }
    @msg('getLoginInfo')
    loginInfo(client: OnebotClient, name: string, msg: { [key: string]: any }) {
        this.logger.info(`登录信息：${msg.data.user_id} - ${msg.data.nickname}`)
    }
    @msg('clientSendMsg')
    clientSendMsg(client: OnebotClient, name: string, msg: { [key: string]: any }) {
        this.logger.debug(`发送消息：${msg.data.message_id}`)
    }
}