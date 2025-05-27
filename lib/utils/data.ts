
import { LRUCache } from 'lru-cache'
import OnebotClient from '../client/onebotClient'

export default class DataGetter {
    public data = {} as { [key: string]: LRUCache<string, any> | {[key: string]: any} }

    constructor(client: OnebotClient) {
        this.data.groupMembers = new LRUCache<string, any>({
            max: 1000,
            ttl: 1000 * 60 * 5,
            allowStale: false,
            fetchMethod: async (key: string) => {
                const res = await client.sendMsgSync({
                    action: 'get_group_member_list',
                    params: {
                        group_id: key,
                        no_cache: true 
                    },
                    echo: 'getGroupMemberList'
                })
                if (res && res.data) {
                    return res.data
                } else {
                    throw new Error(`获取群成员列表失败：${key}`)
                }
            }
        })
    }
}