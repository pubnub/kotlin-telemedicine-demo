package com.pubnub.demo.telemedicine.repository

import com.pubnub.demo.telemedicine.data.user.UserDao
import com.pubnub.demo.telemedicine.data.user.UserData
import com.pubnub.framework.ui.component.login.UserNotFoundException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(private val userDao: UserDao) : UserRepository {

    @Throws(UserNotFoundException::class)
    override fun getUser(login: String): Flow<UserData> =
        userDao.findByUsername(login).map { it ?: throw UserNotFoundException(login) }

    @Throws(UserNotFoundException::class)
    override fun getDoctor(login: String): Flow<UserData> =
        getUser(login).map {
            if (it.isDoctor()) it else throw UserNotFoundException(login)
        }

    @Throws(UserNotFoundException::class)
    override fun getPatient(login: String): Flow<UserData> =
        getUser(login).map {
            if (!it.isDoctor()) it else throw UserNotFoundException(login)
        }

    override suspend fun getUserByUuid(uuid: String): UserData? =
        userDao.get(uuid).first()

    override fun getContacts(uuid: String): Flow<List<UserData>> =
        userDao.getAll().map { list -> list.filter { it.id != uuid } }

    override suspend fun has(uuid: String): Boolean =
        getUserByUuid(uuid) != null

    override fun getDoctorByCase(id: String): Flow<List<UserData>> =
        userDao.getDoctorsByCase(id)

    override fun getPatientByCase(id: String): Flow<UserData?> =
        userDao.getPatientByCase(id)

    override suspend fun getRandomDoctor(): UserData =
        userDao.getAllDoctors().filter { it.isNotEmpty() }.first().random()

    override suspend fun getRandomPatient(): UserData =
        userDao.getAllPatients().filter { it.isNotEmpty() }.first().random()

    override suspend fun add(user: UserData): UserData {
        userDao.insert(user)

        return user
    }

    override suspend fun update(user: UserData): UserData {
        userDao.update(user)

        return user
    }

    override suspend fun insertOrUpdate(user: UserData): UserData =
        if (has(user.id)) update(user)
        else add(user)
}
