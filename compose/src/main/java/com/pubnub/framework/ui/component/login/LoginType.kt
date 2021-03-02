package com.pubnub.framework.ui.component.login

enum class LoginType(var value: Int) {

    LoginPassword(0),
    Login(1),
    EmailPassword(2),
    Email(3),
    ;
}

fun LoginType.passwordVisible(): Boolean =
    this in arrayOf(LoginType.LoginPassword, LoginType.EmailPassword)

fun LoginType.isLogin(): Boolean =
    this in arrayOf(LoginType.Login, LoginType.LoginPassword)

fun LoginType.isEmail(): Boolean =
    this in arrayOf(LoginType.Login, LoginType.LoginPassword)