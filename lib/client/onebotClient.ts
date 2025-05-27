import WebSocket from 'ws'
import log4js from 'log4js'

interface OnebotClientConfig {
    address: string
    token: string
}
interface OnebotClientEvents {
    onOpen?: () => void
    onMessage?: (data: string) => void
    onError?: (error: Error) => void
    onClose?: (code: number, reason: string) => void
}
export default class OnebotClient {
    private config: OnebotClientConfig
    private client: WebSocket | undefined
    private pending = new Map()
    private logger = log4js.getLogger('websocket')

    private events: Required<OnebotClientEvents> = {
        onOpen: () => this.logger.info('onOpen'),
        onMessage: (data: string) => this.logger.debug('onMessage: ' + data),
        onError: (error: Error) => this.logger.error('onError: ' + error),
        onClose: (code: number, reason: string) => this.logger.warn(`onClose: ${code} ${reason}`),
    }

    constructor(config: OnebotClientConfig, events: OnebotClientEvents = {}) {
        this.config = config
        Object.assign(this.events, events)
    }

    public connect() {
        const url = `${this.config.address}?access_token=${this.config.token}`
        this.client = new WebSocket(url, { timeout: 5000 })

        this.client.on('open', () => this.events.onOpen())
        this.client.on('message', (data) => {
            let isResolved = false
            try {
                const json = JSON.parse(data.toString())
                const echo = json.echo
                if (echo && this.pending.has(echo)) {
                    isResolved = true
                    const { resolve } = this.pending.get(echo)
                    this.pending.delete(echo)
                    resolve(json)
                }
            } catch (err) {
                this.logger.error('Message parse error:', err)
            }
            if(!isResolved)
                this.events.onMessage(typeof data === 'string' ? data : data.toString())
        })
        this.client.on('error', (error: Error) => {
            this.events.onError(error)
        })
        this.client.on('close', (code: number, reason: Buffer) => {
            this.events.onClose(code, reason.toString())
            this.client = undefined
        })
    }

    public close(code?: number, reason?: string) {
        if (this.client) {
            this.client.close(code, reason)
            this.client = undefined
        }
    }

    public send(data: string) {
        if (this.isConnected()) {
            try {
                this.client!.send(data)
            } catch (err) {
                this.logger.error('Send error:', err)
                this.close()
            }
        } else {
            this.logger.warn('Cannot send: WebSocket is not open.')
        }
    }

    public isConnected(): boolean {
        return this.client?.readyState === WebSocket.OPEN
    }

    // ============================================================

    /**
     * 发送消息到群组或私聊
     * @param msg 消息内容，可以是字符串或消息对象
     * @param from 消息发送者信息，包含 `user_id` 和 `group_id` 等字段
     * @returns Promise<void>
     */
    public async sendChatMsg(msg: { [key: string]: any } | string, from: { [key: string]: any } | null) {
        if(from === null) {
            const commandLogger = log4js.getLogger('command')
            commandLogger.info('\n' + msg)
            return
        }
        let action = 'send_msg'
        if(typeof msg === 'object' &&
            msg !== null &&
            typeof (msg as any).then === 'function' &&
            typeof (msg as any).catch === 'function') {
            msg = await msg
        }
        if(typeof msg === 'string') {
            if(msg.length > 100 || msg.split('\n').length > 7) {
                // 文本过长，转为伪造合并转发发送
                msg = { messages: [{ type: 'node',
                    data: {
                        user_id: from.self_id,
                        content: {type: 'text', data: { text: msg }}
                    }}]}
                action = 'send_group_forward_msg'
            } else {
                msg = { message: [{ type: 'text', data: { text: msg } }]}
            }
        } else {
            msg = { message: msg }
        }
        const data = {
            action: action,
            params: {
                group_id: from.group_id,
                user_id: from.user_id,
                ...msg
            },
            echo: 'clientSendMsg'
        }
        const finalStr = JSON.stringify(data)
        this.send(finalStr)
    }

    /**
     * 发送同步请求
     * @param payload 请求负载
     * @returns Promise<any>
     */
    public sendMsgSync(payload: { [key: string]: any }): Promise<any> {
        return new Promise((resolve, reject) => {
            const echo = payload.echo

            if (this.client?.readyState !== WebSocket.OPEN) {
                reject(new Error('WebSocket is not open'))
                return
            }

            this.pending.set(echo, { resolve, reject })
            this.send(JSON.stringify(payload))

            setTimeout(() => {
                if (this.pending.has(echo)) {
                    this.pending.delete(echo)
                    reject(new Error('Timeout waiting for response'))
                }
            }, 5000)
        })
    }
}