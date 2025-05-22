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
        this.client.on('message', (data) =>
            this.events.onMessage(typeof data === 'string' ? data : data.toString())
        )
        this.client.on('error', (error: Error) => {
            this.events.onError(error)
            this.close()
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

    public async sendMsg(msg: { [key: string]: any } | string, from: { [key: string]: any }) {
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
            }
        }
        const finalStr = JSON.stringify(data)
        this.send(finalStr)
    }
}