package com.pubnub.demo.telemedicine.repository

import com.pubnub.demo.telemedicine.data.user.UserData
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUser(login: String): Flow<UserData>
    fun getDoctor(login: String): Flow<UserData>
    fun getPatient(login: String): Flow<UserData>
    fun getContacts(uuid: String): Flow<List<UserData>>

    suspend fun getUserByUuid(uuid: String): UserData?
    suspend fun has(uuid: String): Boolean
    fun getDoctorByCase(id: String): Flow<List<UserData>>
    fun getPatientByCase(id: String): Flow<UserData?>
    suspend fun getRandomDoctor(): UserData
    suspend fun getRandomPatient(): UserData
    suspend fun add(user: UserData): UserData
    suspend fun update(user: UserData): UserData
    suspend fun insertOrUpdate(user: UserData): UserData
}
