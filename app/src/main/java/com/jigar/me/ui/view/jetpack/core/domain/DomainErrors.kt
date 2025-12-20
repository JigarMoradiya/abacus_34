package com.jigar.me.ui.view.jetpack.core.domain

object NoLoggedInUser : Throwable() {
    private fun readResolve(): Any = NoLoggedInUser
}


object NotFoundCommonError: Throwable() {
    private fun readResolve(): Any = NotFoundCommonError
}