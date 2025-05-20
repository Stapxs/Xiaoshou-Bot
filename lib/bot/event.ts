import { Bot } from "mineflayer"
import * as MessagereUtil from '../utils/msg'
import log4js from 'log4js'
import { config } from ".."
import Cilent from "../cilent"

const logger = log4js.getLogger('event')

export default function regEvents(bot: Bot) {
    bot.addListener('message', (jsonMsg: any, position) => {
        logger.level = config.getConfig().logLevel
        if(jsonMsg.text != undefined && jsonMsg.text != '') {
            logger.debug(MessagereUtil.replaceMsg(jsonMsg.text))
        } else {
            logger.debug(jsonMsg.toString())
        }
        const message = jsonMsg.toString()
        // 特殊判定
        if(jsonMsg.translate == 'commands.op.failed') {
            Cilent.isOp = false
        }
        let backInfo = {} as any
        // switch(jsonMsg.translate) {
        //     case 'chat.type.text':
        //     case '<%s> %s': 
        //     case '[%s] %s': backInfo = events.playerChat(jsonMsg); break
        // }
    })
}