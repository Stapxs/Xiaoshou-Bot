import { promises as dns } from 'dns'
import log4js from 'log4js'

const logger = log4js.getLogger('http')

export async function resolveSrvToIPs(serviceDomain: string): Promise<{[key: string]:any}[]> {
    try {
        // 解析 SRV 记录
        const srvRecords = await dns.resolveSrv(serviceDomain)
        const ips: {[key: string]: any}[] = []

        for (const record of srvRecords) {
            const { name, port } = record

            try {
                const addresses = await dns.resolve(name);
                for (const ip of addresses) {
                    ips.push({
                        port,
                        ip
                    })
                }
            } catch (err) {
                logger.warn(`解析失败 ${name}:`, err)
            }
        }

        return ips;
    } catch (err) {
        logger.error('解析 SRV 记录失败:', err)
        return []
    }
}