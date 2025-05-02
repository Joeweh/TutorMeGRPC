import api.AccountStatus
import api.AuthServiceGrpcKt
import api.Credential
import api.VerifyEmailRequest
import com.google.protobuf.Empty
import io.grpc.Status
import org.mindrot.jbcrypt.BCrypt
import util.email.SMTP
import util.email.templates.VerifyAccountTemplate
import java.security.SecureRandom
import java.sql.SQLException
import java.util.UUID
import javax.sql.DataSource
import kotlin.math.pow

class AuthService(private val db: DataSource) : AuthServiceGrpcKt.AuthServiceCoroutineImplBase() {
    private companion object {
        private const val CONFIRMATION_CODE_LENGTH = 6
    }

    private val rng: SecureRandom = SecureRandom()

    override suspend fun signIn(request: Credential): Empty {
        return Empty.getDefaultInstance()
    }

    override suspend fun signUp(request: Credential): Empty {
        try {
            db.connection.use { con ->
                con.prepareStatement("SELECT 1 FROM users WHERE email=?").use { stmt ->
                    stmt.setString(1, request.email)

                    stmt.executeQuery().use { results ->
                        if (results.next()) {
                            throw Status.INVALID_ARGUMENT
                                .withDescription("Email already in use")
                                .asException()
                        }
                    }
                }

                con.prepareStatement("INSERT INTO users VALUES (?, ?, ?, ?, ?)").use { stmt ->
                    val code = generateVerificationCode(CONFIRMATION_CODE_LENGTH)

                    stmt.setString(1, UUID.randomUUID().toString())
                    stmt.setString(2, request.getEmail())
                    stmt.setString(3, BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()))
                    stmt.setString(4, AccountStatus.PENDING_VERIFICATION.toString())
                    stmt.setString(5, code)
                    stmt.execute()

                    SMTP.sendEmail(request.email, VerifyAccountTemplate(code))

                    return Empty.getDefaultInstance()
                }
            }
        }

        catch (e: Exception) {
            throw Status.INTERNAL
                .withDescription(e.message)
                .asException()
        }
    }

    private fun generateVerificationCode(length: Int): String {
        require(length >= 1) { "Length must be > 0" }

        val rawNumber = rng.nextInt(10.0.pow(length.toDouble()).toInt())

        return String.format("%0" + length + "d", rawNumber)
    }

    override suspend fun verifyEmail(request: VerifyEmailRequest): Empty {
        try {
            db.connection.use { con ->
                con.prepareStatement("SELECT confirmation_code FROM users WHERE email=?").use { stmt ->
                    stmt.setString(1, request.email)

                    stmt.executeQuery().use { results ->
                        if (!results.next()) {
                            throw Status.INVALID_ARGUMENT
                                .withDescription("Email doesn't exist")
                                .asException()
                        }

                        val expectedCode = results.getString("confirmation_code")

                        if (request.getCode() != expectedCode) {
                            throw Status.INVALID_ARGUMENT
                                .withDescription("Invalid code")
                                .asException()
                        }
                    }
                }

                con.prepareStatement("UPDATE users SET account_status=? WHERE email=?").use { stmt ->
                    stmt.setString(1, AccountStatus.VERIFIED.toString())
                    stmt.setString(2, request.getEmail())
                    stmt.executeUpdate()

                    return Empty.getDefaultInstance()
                }
            }
        }

        catch (_: SQLException) {
            throw Status.INTERNAL
                .withDescription("SQL Error")
                .asException()
        }
    }

    override suspend fun requestAccountDeletion(request: Credential): Empty {
        try {
            db.connection.use { con ->
                con.prepareStatement("SELECT password FROM users WHERE email=?").use { stmt ->
                    stmt.setString(1, request.email)

                    stmt.executeQuery().use { results ->
                        if (!results.next()) {
                            throw Status.INVALID_ARGUMENT
                                .withDescription("Email doesn't exist")
                                .asException()
                        }

                        val hash = results.getString("password")

                        if (!BCrypt.checkpw(request.getPassword(), hash)) {
                            throw Status.INVALID_ARGUMENT
                                .withDescription("Invalid password")
                                .asException()
                        }
                    }
                }

                con.prepareStatement("UPDATE users SET account_status=?, confirmation_code=? WHERE email=?").use { stmt ->
                    val code = generateVerificationCode(CONFIRMATION_CODE_LENGTH)

                    stmt.setString(1, AccountStatus.PENDING_DELETION.toString())
                    stmt.setString(2, code)
                    stmt.setString(3, request.getEmail())

                    stmt.executeUpdate()

                    SMTP.sendEmail(request.email, VerifyAccountTemplate(code))

                    return Empty.getDefaultInstance()
                }
            }
        }

        catch (_: SQLException) {
            throw Status.INTERNAL
                .withDescription("SQL Error")
                .asException()
        }
    }

    override suspend fun confirmDeletion(request: VerifyEmailRequest): Empty {
        try {
            db.connection.use { con ->
                con.prepareStatement("SELECT confirmation_code FROM users WHERE email=?").use { stmt ->
                    stmt.setString(1, request.email)

                    stmt.executeQuery().use { results ->
                        if (!results.next()) {
                            throw Status.INVALID_ARGUMENT
                                .withDescription("Email doesn't exist")
                                .asException()
                        }

                        val expectedCode = results.getString("confirmation_code")

                        if (request.getCode() != expectedCode) {
                            throw Status.INVALID_ARGUMENT
                                .withDescription("Invalid code")
                                .asException()
                        }
                    }
                }

                con.prepareStatement("DELETE FROM users WHERE email=?").use { stmt ->
                    stmt.setString(1, request.getEmail())
                    stmt.execute()

                    return Empty.getDefaultInstance()
                }
            }
        }

        catch (_: SQLException) {
            throw Status.INTERNAL
                .withDescription("SQL Error")
                .asException()
        }
    }
}