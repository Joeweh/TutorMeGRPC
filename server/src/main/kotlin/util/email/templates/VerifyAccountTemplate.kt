package util.email.templates

import kotlinx.css.CssBuilder
import kotlinx.css.fontFamily
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.stream.createHTML
import kotlinx.html.style
import kotlinx.html.unsafe
import util.email.EmailTemplate

class VerifyAccountTemplate(private val code: String) : EmailTemplate {
    override val subject: String = "Account Verification"

    override fun compose(): String {
        val styles = CssBuilder().apply {
            rule(".header") {
                fontFamily = "Arial, Helvetica, sans-serif"
            }
        }

        return createHTML().html {
            head {
                style {
                    unsafe {
                        +styles.toString()
                    }
                }
            }

            body {
                h1("header") {
                    + "Code: $code"
                }
            }
        }
    }
}