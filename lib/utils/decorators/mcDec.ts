import OnebotClient from '../../client/onebotClient'
import { Bot } from 'mineflayer'

type McCommandHandler = (
    bot: Bot,
    client: OnebotClient,
    msg: { [key: string]: any },
    data: { [key: string]: any }
) => undefined | string | { [key: string]: any }

interface McCommandMeta {
    cmdName: string
    methodName: string
    className: string
    handler: McCommandHandler
    bound: boolean
}

const mcCommandRegistry = new Map<string, McCommandMeta>()

export function mcCommandEvent(name?: string): MethodDecorator {
    return function (target, propertyKey, descriptor) {
        const cmdName = name || (propertyKey as string)
        const methodName = propertyKey as string
        const className = target.constructor.name

        const fn = descriptor.value as unknown
        if (typeof fn !== 'function') {
            throw new Error(`@mcCommandEvent 装饰器只能应用于方法，不能应用于 ${typeof fn}`)
        }
        if(!mcCommandRegistry.has(cmdName)) {
            mcCommandRegistry.set(cmdName, {
                cmdName,
                methodName,
                className,
                handler: fn as McCommandHandler,
                bound: false,
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

export function bindMcCommandHandlers(instance: any) {
    // eslint-disable-next-line no-console
    console.log(`- 为 @mcCommandEvent 绑定处理器到实例 ${instance.constructor.name}`)
    const className = instance.constructor.name

    for (const [, meta] of mcCommandRegistry.entries()) {
        if (meta.className === className && !meta.bound) {
            const method = instance[meta.methodName]
            if (typeof method === 'function') {
                meta.handler = method.bind(instance)
                meta.bound = true
            }
        }
    }
}