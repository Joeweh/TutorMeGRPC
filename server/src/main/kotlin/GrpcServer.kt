import io.grpc.ServerBuilder
import util.db.DB

fun main() {
    println("Loading Server. . .")

    val server = ServerBuilder
        .forPort(System.getenv("PORT")?.toInt() ?: 8080)
        .addService(AuthService(DB.dataSource))
        .build()

    // Add shutdown hook to close HikariDataSource
    Runtime.getRuntime().addShutdownHook(Thread {
        println("Shutting down server and closing DB pool. . .")
        server.shutdown()
        (DB.dataSource as? AutoCloseable)?.close()
        println("Shutdown complete!")
    })

    server.start()

    println("Server Started!")

    server.awaitTermination()
}