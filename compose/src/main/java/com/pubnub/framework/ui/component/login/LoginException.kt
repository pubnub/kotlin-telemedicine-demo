package com.pubnub.framework.ui.component.login

abstract class LoginException(message: String) : RuntimeException(message)

class UserNotFoundException(message: String) : LoginException("User '$message' not found")
class IncorrectPasswordException : LoginException("Incorrect password")