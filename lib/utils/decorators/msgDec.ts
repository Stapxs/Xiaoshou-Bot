import OnebotClient from '../../client/onebotClient'

const msgRegistry = new Map<string, (
    client: OnebotClient,
    name: string,
    msg: { [key: string]: any },
    echoList?: string[]
) => void>()

export function msgEvent(name?: string): MethodDecorator {
    return function (_, propertyKey, descriptor) {
        const cmdName = name || (propertyKey as string)
        const fn = descriptor.value as (
            client: OnebotClient,
            name: string,
            msg: { [key: string]: any },
            echoList?: string[]
        ) => void;
        if (typeof fn !== 'function') {
            throw new Error(`msgEvent 装饰器只能应用于方法，不能应用于 ${typeof fn}`)
        }
        if(!msgRegistry.has(cmdName)) {
            msgRegistry.set(cmdName, function boundHandler(
                this: any,
                client: OnebotClient,
                name: string,
                msg: { [key: string]: any },
                echoList?: string[]
            ) {
                return fn.call(this, client, name, msg, echoList)
            })
        }
    }
}

export function getMsgEvent(name: string) {
    return msgRegistry.get(name)
}

export function getAllMsgEvents() {
    return msgRegistry
}