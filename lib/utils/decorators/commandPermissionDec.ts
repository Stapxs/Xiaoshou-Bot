import { getKeyByCommand } from './commandDec'

const commandPermissionMap = new Map<string, string>()

/**
 * 权限装饰器
 * ### 角色名称
 * - `master`：机器人主人
 * - `owner`：群主（仅在群组中生效）
 * - `admin`：群管理员（仅在群组中生效）
 * - `private`：私聊
 * - `group`：群组
 * ### 权限组
 * 功能请参考 SysCommandEvent 中的权限组指令，在代码中定义的权限组名称将自动创建到数据库中。
 * @param role 角色名称或权限组名称
 * @returns MethodDecorator
 */
export function permission(role: string): MethodDecorator {
    return function (target, propertyKey, descriptor) {
        const roleName = role.toLowerCase()
        const methodName = propertyKey as string
        const className = target.constructor.name

        const fn = descriptor.value as unknown
        if (typeof fn !== 'function') {
            throw new Error(`@permission 装饰器只能用于方法，不能用于 ${typeof fn} 类型`);
        }
        if (!commandPermissionMap.has(methodName + '|' + className)) {
            commandPermissionMap.set(methodName + '|' + className, roleName);
        }
    }
}

// 获取权限
export function getPermission(cmdName: string) {
    const key = getKeyByCommand(cmdName)
    if(key) {
        return commandPermissionMap.get(key) || ''
    }
    return ''
}