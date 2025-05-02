import api.AuthServiceGrpc
import api.AuthServiceGrpc.AuthServiceStub
import api.Credential
import api.VerifyEmailRequest
import com.google.protobuf.Empty
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

private const val confirmationCode = "303221"

fun main() {
    val channel = ManagedChannelBuilder.forAddress("localhost", 8080)
        .usePlaintext()
        .build()

    val stub = AuthServiceGrpc.newStub(channel)


    signUp(stub, CountDownLatch(1))

//    verifyEmail(stub, CountDownLatch(1))
//
//    requestAccountDeletion(stub, CountDownLatch(1));
//
//    confirmAccountDeletion(stub, CountDownLatch(1));

    channel.shutdown()
    channel.awaitTermination(5, TimeUnit.SECONDS)
}

private fun signUp(stub: AuthServiceStub, latch: CountDownLatch) {
    val credential = Credential.newBuilder()
        .setEmail("test@gmail.com")
        .setPassword("test")
        .build()

    stub.signUp(credential, object : StreamObserver<Empty?> {
        override fun onNext(value: Empty?) {
            println("Sign up response received")
        }

        override fun onError(t: Throwable) {
            System.err.println("Error while signing up")
            System.err.println(t.message)
            latch.countDown()
        }

        override fun onCompleted() {
            println("Completed sign up")
            latch.countDown()
        }
    })

    if (!latch.await(10, TimeUnit.SECONDS)) {
        System.err.println("Timeout waiting for response")
    }
}

private fun verifyEmail(stub: AuthServiceStub, latch: CountDownLatch) {
    val request = VerifyEmailRequest.newBuilder()
        .setEmail("test@gmail.com")
        .setCode(confirmationCode)
        .build()

    stub.verifyEmail(request, object : StreamObserver<Empty?> {
        override fun onNext(value: Empty?) {
            println("Verify email response received")
        }

        override fun onError(t: Throwable) {
            System.err.println(t.message)
            latch.countDown()
        }

        override fun onCompleted() {
            println("Completed verification")
            latch.countDown()
        }
    })

    if (!latch.await(10, TimeUnit.SECONDS)) {
        System.err.println("Timeout waiting for response")
    }
}

private fun requestAccountDeletion(stub: AuthServiceStub, latch: CountDownLatch) {
    val credential = Credential.newBuilder()
        .setEmail("test@gmail.com")
        .setPassword("test")
        .build()

    stub.requestAccountDeletion(credential, object : StreamObserver<Empty?> {
        override fun onNext(value: Empty?) {
            println("Account deletion response received")
        }

        override fun onError(t: Throwable) {
            System.err.println(t.message)
            latch.countDown()
        }

        override fun onCompleted() {
            println("Completed account deletion request")
            latch.countDown()
        }
    })

    if (!latch.await(10, TimeUnit.SECONDS)) {
        System.err.println("Timeout waiting for response")
    }
}

private fun confirmAccountDeletion(stub: AuthServiceStub, latch: CountDownLatch) {
    val request = VerifyEmailRequest.newBuilder()
        .setEmail("test@gmail.com")
        .setCode(confirmationCode)
        .build()

    stub.confirmDeletion(request, object : StreamObserver<Empty?> {
        override fun onNext(value: Empty?) {
            println("Account deletion response received")
        }

        override fun onError(t: Throwable) {
            System.err.println(t.message)
            latch.countDown()
        }

        override fun onCompleted() {
            println("Completed deletion")
            latch.countDown()
        }
    })

    if (!latch.await(10, TimeUnit.SECONDS)) {
        System.err.println("Timeout waiting for response")
    }
}