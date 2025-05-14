import api.auth.Credential
import api.auth.VerifyEmailRequest
import api.auth.AuthServiceGrpcKt

import io.grpc.ManagedChannelBuilder
import io.grpc.StatusException


private const val confirmationCode = "303221"

suspend fun main() {
    val channel = ManagedChannelBuilder.forAddress("localhost", 8080)
        .usePlaintext()
        .build()

    val stub = AuthServiceGrpcKt.AuthServiceCoroutineStub(channel)


    signUp(stub)

//    verifyEmail(stub)
//
//    requestAccountDeletion(stub);
//
//    confirmAccountDeletion(stub);

    channel.shutdown()
    //channel.awaitTermination(5, TimeUnit.SECONDS)
}

private suspend fun signUp(stub: AuthServiceGrpcKt.AuthServiceCoroutineStub) {
    try {
        val credential = Credential.newBuilder()
            .setEmail("test@gmail.com")
            .setPassword("test")
            .build()

        stub.signUp(credential)

        println("Sign up successful!")
    }

    catch (e: StatusException) {
        System.err.println("Error signing up")
        System.err.println(e.status)
        System.err.println(e.message)
    }
}

private suspend fun verifyEmail(stub: AuthServiceGrpcKt.AuthServiceCoroutineStub) {
    try {
        val request = VerifyEmailRequest.newBuilder()
            .setEmail("test@gmail.com")
            .setCode(confirmationCode)
            .build()

        stub.verifyEmail(request)

        println("Verify email successful!")
    }

    catch (e: StatusException) {
        System.err.println("Error verifying email")
        System.err.println(e.status)
        System.err.println(e.message)
    }
}

private suspend fun requestAccountDeletion(stub: AuthServiceGrpcKt.AuthServiceCoroutineStub) {
    try {
        val credential = Credential.newBuilder()
            .setEmail("test@gmail.com")
            .setPassword("test")
            .build()

        stub.requestAccountDeletion(credential)

        println("Request account deletion successful!")
    }

    catch (e: StatusException) {
        System.err.println("Error requesting account deletion")
        System.err.println(e.status)
        System.err.println(e.message)
    }
}

private suspend fun confirmAccountDeletion(stub: AuthServiceGrpcKt.AuthServiceCoroutineStub) {
    try {
        val request = VerifyEmailRequest.newBuilder()
            .setEmail("test@gmail.com")
            .setCode(confirmationCode)
            .build()

        stub.confirmDeletion(request)

        println("Confirmation deletion successful!")
    }

    catch (e: StatusException) {
        System.err.println("Error confirming account deletion")
        System.err.println(e.status)
        System.err.println(e.message)
    }
}