/**
 * 替换字符串中的颜色代码为对应的 ANSI 颜色码
 * @param msg 需要替换的字符串
 * @returns 替换后的字符串
 */
export function replaceMsg(msg: string) {
    const ansiMap: Record<string, string> = {
        '0': '\x1b[30m', '1': '\x1b[34m', '2': '\x1b[32m', '3': '\x1b[36m',
        '4': '\x1b[31m', '5': '\x1b[35m', '6': '\x1b[33m', '7': '\x1b[37m',
        '8': '\x1b[90m', '9': '\x1b[94m', 'a': '\x1b[92m', 'b': '\x1b[96m',
        'c': '\x1b[91m', 'd': '\x1b[95m', 'e': '\x1b[93m', 'f': '\x1b[97m',
        'k': '\x1b[8m', 'l': '\x1b[1m', 'm': '\x1b[9m', 'n': '\x1b[4m',
        'o': '\x1b[3m', 'r': '\x1b[0m',
    }
    return msg.replace(/§([0-9a-frk-or])/gi, (_, code) => ansiMap[code.toLowerCase()] || '') + '\x1b[0m';
}