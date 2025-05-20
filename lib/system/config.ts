import fs from 'fs'

/**
 * 配置文件工具类
 */
export default class Config {

    private path
    private config: {[key: string]: any} = {}

    constructor(path: string) {
        this.path = path
        try {
            this.config = JSON.parse(fs.readFileSync(path, 'utf8'))
        } catch (err) {
            console.error(err)
        }
    }

    getConfig() {
        return this.config
    }
    get(name: string): any {
        return this.config[name]
    }
    set(name: string, value: any) {
        this.config[name] = value
    }
    setSave(name: string, value: any) {
        this.config[name] = value
        try {
            fs.writeFileSync(this.path, JSON.stringify(this.config))
        } catch {
            return false
        }
        return true
    }
}