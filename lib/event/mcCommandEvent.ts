import OnebotClient from '../client/onebotClient'
import { bindMcCommandHandlers, mcCommandEvent } from '../utils/decorators/mcDec'
import { Bot } from 'mineflayer'

export default class McCommandEvent {

    constructor() {
        bindMcCommandHandlers(this)
    }

    @mcCommandEvent('command.unknown.command')
    async unknownCommand(bot: Bot) {
        const commandBack = await bot.awaitMessage(/<--\[HERE\]$/)
        return '执行指令失败：' + commandBack + '\n\n> 可能是指令错误或没有执行权限。'
    }

    @mcCommandEvent('commands.scoreboard.objectives.list.success')
    scoreboardList(bot: Bot, client: OnebotClient, msg: { [key: string]: any }, data: {[key: string]: any}) {
        const allList = data.json.with[1].extra
        const squareBrackets = allList.filter((item: any) => {
            return item.translate == 'chat.square_brackets'
        })
        let result = '> 计分板列表\n\n'
        for(const item of squareBrackets) {
            const name = item.with[0].hoverEvent.contents.text
            const displayName = item.with[0].text
            result += `  ${name} - ${displayName}\n`
        }
        result += '\n 共有 ' + squareBrackets.length + ' 个计分板\n'
        result += ':: 使用 /mc scoreboard <name> 来查看对应计分板内容'

        return result
    }

    @mcCommandEvent('commands.scoreboard.objectives.display.set')
    @mcCommandEvent('commands.scoreboard.objectives.display.alreadySet')
    scoreboardDisplay(bot: Bot, client: OnebotClient, msg: { [key: string]: any }) {
        const scoreboard = bot.scoreboards[msg.raw_message.split(' ')[2]]
        let result = '> 计分板数据：'+ scoreboard.title + '\n\n'
        if (scoreboard) {
            const list = [] as { [key: string]: any }[]
            for(const item of Object.values(scoreboard.itemsMap)) {
                const name = item.displayName
                const value = item.value
                list.push({ name, value })
            }
            list.sort((a, b) => {
                return b.value - a.value
            })
            const topList = list.slice(0, 20)
            for(const item of topList) {
                result += `  ${item.name} - ${item.value}\n`
            }
        }
        // 取消设置 sidebar
        bot.chat('/scoreboard objectives setdisplay sidebar')
        return result
    }
}