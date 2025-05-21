import OnebotClient from '../../client/onebotClient'
import { Bot } from 'mineflayer'

const mcCommandRegistry = new Map<string, (bot: Bot, client: OnebotClient, msg: { [key: string]: any }, data: {[key: string]: any}) => undefined | string | { [key: string]: any }>()

export function mcCommandEvent(name?: string): MethodDecorator {
    return function (_, propertyKey, descriptor) {
        const cmdName = name || (propertyKey as string)
        const fn = descriptor.value as (bot: Bot, client: OnebotClient, msg: { [key: string]: any }, data: {[key: string]: any}) => undefined | string | { [key: string]: any };
        if (typeof fn !== 'function') {
            throw new Error(`mcCommandEvent 装饰器只能应用于方法，不能应用于 ${typeof fn}`)
        }
        if(!mcCommandRegistry.has(cmdName)) {
            mcCommandRegistry.set(cmdName, function boundHandler(
                this: any,
                bot: Bot, 
                client: OnebotClient,
                msg: { [key: string]: any },
                data: {[key: string]: any}
            ) : undefined | string | { [key: string]: any } {
                return fn.call(this, bot, client, msg, data)
            })
        }
    }
}

export function getMcCommandEvent(name: string) {
    return mcCommandRegistry.get(name)
}

export function getAllMcCommandEvents() {
    return mcCommandRegistry
}