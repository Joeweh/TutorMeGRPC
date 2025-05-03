package util.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

object DB {
    val dataSource: DataSource = init()

    private fun init(): DataSource {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = requireNotNull(System.getenv("SQL_URL"))
            username = requireNotNull(System.getenv("SQL_UNAME"))
            password = requireNotNull(System.getenv("SQL_PW"))
            driverClassName = "com.mysql.cj.jdbc.Driver"
            maximumPoolSize = 10
            minimumIdle = 5
            idleTimeout = 300_000 // 5 minutes
            maxLifetime = 1_800_000 // 30 minutes
            keepaliveTime = 300_000 // 5 minutes
            connectionTimeout = 15_000 // 30 seconds
            connectionTestQuery = "SELECT 1"
            leakDetectionThreshold = 20_000
        }

        return HikariDataSource(hikariConfig)
    }
}