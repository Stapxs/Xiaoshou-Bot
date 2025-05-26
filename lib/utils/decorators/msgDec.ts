import OnebotClient from '../../client/onebotClient'

type MsgHandler = (
    client: OnebotClient,
    name: string,
    msg: { [key: string]: any },
    echoList?: string[]
) => void

interface MsgMeta {
    cmdName: string
    methodName: string
    className: string
    handler: MsgHandler
    bound: boolean
}

const msgRegistry = new Map<string, MsgMeta>()

export function msg(name?: string): MethodDecorator {
    return function (target, propertyKey, descriptor) {
        const cmdName = name || (propertyKey as string)
        const methodName = propertyKey as string
        const className = target.constructor.name

        const fn = descriptor.value as unknown
        if (typeof fn !== 'function') {
            throw new Error(`@msg 装饰器只能应用于方法，不能应用于 ${typeof fn}`)
        }
        if(!msgRegistry.has(cmdName)) {
            msgRegistry.set(cmdName, {
                cmdName,
                methodName,
                className,
                handler: fn as MsgHandler,
                bound: false,
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

export function bindMsgHandlers(instance: any) {
    // eslint-disable-next-line no-console
    console.log(`- 为 @msg 绑定处理器到实例 ${instance.constructor.name}`)
    const className = instance.constructor.name

    for (const [, meta] of msgRegistry.entries()) {
        if (meta.className === className && !meta.bound) {
            const method = instance[meta.methodName]
            if (typeof method === 'function') {
                meta.handler = method.bind(instance)
                meta.bound = true
            }
        }
    }
}