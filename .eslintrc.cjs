/* eslint-env node */
module.exports = {
    parserOptions: {
        'parser': '@typescript-eslint/parser',
        'ecmaVersion': 2020,
        'sourceType': 'module'
    },
    plugins: [
        '@typescript-eslint',
    ],
    // 继承的规则配置
    extends: [
        'eslint:recommended',
        'plugin:@typescript-eslint/recommended',
    ],
    rules: {
        // === 基础规则 ===
        // 忽略使用 any 类型的错误
        '@typescript-eslint/no-explicit-any': 'off',
        // debugger
        'no-debugger': 'warn',
        // console
        'no-console': 'warn',
        // prefer-const
        'prefer-const': 'warn',
        // 变量未使用
        '@typescript-eslint/no-unused-vars': 'warn',
        // 优先使用箭头函数
        'prefer-arrow-callback': 'warn',
        // 引号
        'quotes': ['warn', 'single'],
        // 三元表达式
        'multiline-ternary': ['warn', 'never'],
    },
}
