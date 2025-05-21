import OnebotClient from '../../client/onebotClient'

const commandRegistry = new Map<string[], (
    client: OnebotClient,
    msg: { [key: string]: any },
    args: { [key: string]: any}
) => undefined | string | { [key: string]: any }>()

export function command(name?: string, dist?: string): MethodDecorator {
    return function (_, propertyKey, descriptor) {
        const cmdName = name || (propertyKey as string)
        const fn = descriptor.value as (
    client: OnebotClient,
    msg: { [key: string]: any },
    args: { [key: string]: any}
) => undefined | string | { [key: string]: any }[];
        if (typeof fn !== 'function') {
            throw new Error(`@command 装饰器只能应用于方法，不能应用于 ${typeof fn}`)
        }
        if(!commandRegistry.has([cmdName, dist ?? ''])) {
            commandRegistry.set([cmdName, dist ?? ''], function boundHandler(
                this: any,
                client: OnebotClient,
                msg: { [key: string]: any },
                args: { [key: string]: any}
            ): undefined | string | { [key: string]: any }[] {
                return fn.call(this, client, msg, args)
            })
        }
    }
}

export function getCommand(name: string) {
    const items = Array.from(commandRegistry.entries()).filter(([key]) => key[0] === name)
    return items.length > 0 ? items[0][1] : undefined
}

export function getAllCommands() {
    return commandRegistry
}