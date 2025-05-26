import { bindCommandHandlers } from '../../utils/decorators/commandDec'

/**
 * ### sysCommandEvent.ts
 * 系统功能相关的命令事件定义
 */
export default class SysCommandEvent {

    constructor() {
        bindCommandHandlers(this)
    }

}