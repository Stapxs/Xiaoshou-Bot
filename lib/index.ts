import Cilent from "./cilent"
import Config from "./system/config"

// 获取设置参数
let configFile = process.argv[0].substring(0, process.argv[0].indexOf('node_modules')) + 'config.json'
process.argv.forEach((str) => {
    if(str.startsWith('--config=')) {
        configFile = str.split('=')[1]
    }
})
export const config = new Config(configFile)

// 构建 Mineflayer Bot
const cilent = new Cilent(config)

setTimeout(() => {
    cilent.join()
}, 3000)
