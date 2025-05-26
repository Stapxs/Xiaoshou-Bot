-- 此版本为 bot_config 表添加了 master，需要重新配置
DROP TABLE IF EXISTS bot_config;

CREATE TABLE IF NOT EXISTS bot_config (
  key   TEXT PRIMARY KEY,                                -- 键
  value TEXT NOT NULL                                    -- 值
);