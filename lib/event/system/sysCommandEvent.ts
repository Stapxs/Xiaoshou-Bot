import { db } from '../..'
import OnebotClient from '../../client/onebotClient'
import { bindCommandHandlers, command } from '../../utils/decorators/commandDec'
import { getAlowPermissions, permission } from '../../utils/decorators/commandPermissionDec'

/**
 * ### sysCommandEvent.ts
 * 系统功能相关的命令事件定义
 */
export default class SysCommandEvent {

    constructor() {
        bindCommandHandlers(this)
    }

    @command('permission.add', '查看当前用户权限')
    @command('permission.add.*')
    @permission('master')
    @permission('group')
    addPermissionCommand(client: OnebotClient, msg: { [key: string]: any }, args: { [key: string]: any }) {
        const allAllowedPermissionGroup = getAlowPermissions()
        const id = msg.group_id
        if(!args._[2] || !id) {
            return '请指定要添加的权限组名称'
        } else {
            const permissionGroup = String(args._[2])
            if(!allAllowedPermissionGroup.includes(permissionGroup)) {
                return `权限组 ${permissionGroup} 不可用`
            }
            const back = db.prepare('INSERT INTO permission (id, group_name) VALUES (?, ?)').run(String(id), permissionGroup)
            if (back.changes > 0) {
                return `已将群组 ${id} 添加到权限组 ${permissionGroup}`
            } else {
                return `添加权限组 ${permissionGroup} 失败，可能是因为该群组已存在于此权限组中`
            }
        }
    }

}