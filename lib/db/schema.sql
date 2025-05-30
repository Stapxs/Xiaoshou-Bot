-- Bot 基础配置
CREATE TABLE IF NOT EXISTS bot_config (
  key   TEXT PRIMARY KEY,                                -- 键
  value TEXT NOT NULL                                    -- 值
);

-- 详细权限，id 可以是群 ID 或用户 ID；一个 ID 可以属于多个权限组
CREATE TABLE IF NOT EXISTS permission (
  id          TEXT NOT NULL,                            -- 群 ID 或用户 ID
  group_name TEXT NOT NULL,                             -- 权限组名称
  created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,       -- 创建时间
  PRIMARY KEY (id, group_name)                          -- 联合主键，确保每个 ID 在每个权限组中唯一
);

-- Minecraft 服务器连接配置
CREATE TABLE IF NOT EXISTS mc_config (
  group_id       TEXT PRIMARY KEY,                      -- 十位以上数字，使用 TEXT 可避免整数溢出
  auth           TEXT,                                  -- 认证方式，如 "custom"
  version        TEXT,                                  -- 版本号，如 "1.20.1"
  auth_server    TEXT,                                  -- 认证服务器地址
  session_server TEXT,                                  -- 会话服务器地址
  username       TEXT NOT NULL,                         -- 用户邮箱
  password       TEXT NOT NULL,                         -- 密码
  name           TEXT NOT NULL,                         -- 显示名称（如游戏昵称）
  host           TEXT NOT NULL,                         -- 服务器 IP 地址
  port           INTEGER NOT NULL DEFAULT 25565,        -- 服务器端口
  created_at     DATETIME DEFAULT CURRENT_TIMESTAMP     -- 创建时间
);