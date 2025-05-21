import fs from 'fs'
import path from 'path'
import { Database } from 'better-sqlite3'
import log4js from 'log4js'

const SCHEMA_PATH = path.join(__dirname, 'schema.sql')
const MIGRATIONS_DIR = path.join(__dirname, 'migrations')

const logger = log4js.getLogger('database')

function runMigrations(db: Database) {
  db.exec(`CREATE TABLE IF NOT EXISTS schema_version (
    version TEXT PRIMARY KEY,
    applied_at DATETIME DEFAULT CURRENT_TIMESTAMP
  )`)

  const applied = new Set(
    db.prepare('SELECT version FROM schema_version').all().map(row => (row as any).version)
  )

  const files = fs.readdirSync(MIGRATIONS_DIR).filter(f => f.endsWith('.sql')).sort()

  for (const file of files) {
    const version = file.split('_')[0]
    if (applied.has(version)) continue

    const sql = fs.readFileSync(path.join(MIGRATIONS_DIR, file), 'utf-8')
    const transaction = db.transaction(() => {
      db.exec(sql)
      db.prepare('INSERT INTO schema_version (version) VALUES (?)').run(version)
    })

    transaction()
    logger.info(`应用迁移：${file}`)
  }
}

export default function initDatabase(db: Database) {
  const schema = fs.readFileSync(SCHEMA_PATH, 'utf-8')
  db.exec(schema)
  runMigrations(db)
}