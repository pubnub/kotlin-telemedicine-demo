package com.pubnub.demo.telemedicine.service

import com.pubnub.demo.telemedicine.initialization.data.User
import com.pubnub.demo.telemedicine.network.mapper.UserMapper.toUi
import com.pubnub.demo.telemedicine.repository.UserRepository
import com.pubnub.framework.ui.component.login.IncorrectPasswordException
import com.pubnub.framework.ui.component.login.UserNotFoundException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class LoginService constructor(
    private val repository: UserRepository,
) {
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    @Throws(UserNotFoundException::class, IncorrectPasswordException::class)
    suspend fun login(login: String, password: String, isDoctor: Boolean): User =
        withContext(ioDispatcher) {
            if (isDoctor) repository.getDoctor(login).first().toUi()
            else repository.getPatient(login).first().toUi()
        }
}
