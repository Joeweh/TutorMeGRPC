package util.email

interface EmailTemplate {
    val subject: String

    fun compose(): String
}