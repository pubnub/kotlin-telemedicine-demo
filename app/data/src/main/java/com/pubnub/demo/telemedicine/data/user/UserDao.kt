package com.pubnub.demo.telemedicine.data.user

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE id LIKE :id LIMIT 1")
    fun get(id: String): Flow<UserData?>

    @Query("SELECT * FROM user")
    fun getAll(): Flow<List<UserData>>

    @Query("SELECT * FROM user WHERE (SELECT CaseData.doctors FROM CaseData WHERE id = :id) LIKE '%' || id || '%'")
    fun getDoctorsByCase(id: String): Flow<List<UserData>>

    @Query("SELECT * FROM user WHERE (SELECT CaseData.patientId FROM CaseData WHERE id = :id) LIKE id")
    fun getPatientByCase(id: String): Flow<UserData?>

    @Query("SELECT * FROM user WHERE custom_isDoctor LIKE 1")
    fun getAllDoctors(): Flow<List<UserData>>

    @Query("SELECT * FROM user WHERE custom_isDoctor LIKE 0")
    fun getAllPatients(): Flow<List<UserData>>

    @Query("SELECT * FROM user WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): Flow<UserData?>

    @Query("SELECT * FROM user WHERE custom_username LIKE :username LIMIT 1")
    fun findByUsername(username: String): Flow<UserData?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(vararg userData: UserData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg userData: UserData)

    @Delete
    fun delete(userData: UserData)
}
