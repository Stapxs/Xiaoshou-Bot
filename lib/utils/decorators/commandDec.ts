import OnebotClient from '../../client/onebotClient'

type CommandHandler = (
    client: OnebotClient,
    msg: { [key: string]: any } | null,
    args: { [key: string]: any }
) => string | { [key: string]: any } | undefined

interface CommandMeta {
    cmdName: string
    dist?: string
    methodName: string
    className: string
    handler: CommandHandler
    bound: boolean
}

const commandRegistry = new Map<string, CommandMeta>();

/**
 * 命令装饰器
 * @param name 命令名称
 * @param dist 命令描述，用于自动生成 help。可缺省
 * @returns MethodDecorator
 */
export function command(name?: string, dist?: string): MethodDecorator {
    return function (target, propertyKey, descriptor) {
        const cmdName = name || (propertyKey as string)
        const methodName = propertyKey as string
        const className = target.constructor.name
        const key = makeKey(cmdName, dist)

        const fn = descriptor.value as unknown
        if (typeof fn !== 'function') {
            throw new Error(`@command 装饰器只能用于方法，不能用于 ${typeof fn} 类型`)
        }

        if (!commandRegistry.has(key)) {
            commandRegistry.set(key, {
                cmdName,
                dist,
                methodName,
                className,
                handler: fn as CommandHandler,
                bound: false,
            })
        }
    }
}

export function getCommand(name: string) {
    for (const [key, handler] of commandRegistry.entries()) {
        if (key.startsWith(name + '|')) {
            return handler
        }
    }
    return undefined
}

export function getKeyByCommand(name: string) {
    for (const key of commandRegistry.keys()) {
        if (key.startsWith(name + '|')) {
            const value = commandRegistry.get(key)
            if(value?.methodName && value?.className) {
                return value.methodName + '|' + value.className
            } else {
                return undefined
            }
        }
    }
    return undefined
}

export function getAllCommands(): Map<string, string> {
    const commands = new Map<string, string>();
    for (const [key] of commandRegistry.entries()) {
        const [name, dist] = key.split('|')
        commands.set(name, dist)
    }
    return commands
}

export function bindCommandHandlers(instance: any) {
    // eslint-disable-next-line no-console
    console.log(`- 为 @command 绑定处理器到实例 ${instance.constructor.name}`)
    const className = instance.constructor.name

    for (const [, meta] of commandRegistry.entries()) {
        if (meta.className === className && !meta.bound) {
            const method = instance[meta.methodName]
            if (typeof method === 'function') {
                meta.handler = method.bind(instance)
                meta.bound = true
            }
        }
    }
}

// ===================

function makeKey(name: string, dist: string = ''): string {
    return `${name}|${dist}`
}