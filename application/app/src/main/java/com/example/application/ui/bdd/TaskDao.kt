package com.example.application.ui.bdd

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT * from tasks WHERE id = :id")
    fun getTask(id: Int): Flow<Task>

    @Query("SELECT * from tasks ORDER BY id DESC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

    @Query("UPDATE tasks SET isDone = :isDone WHERE id = :id")
    suspend fun updateTaskStatus(id: Int, isDone: Boolean)

    // ToDoLists
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToDoList(toDoList: ToDoList)

    @Query("SELECT * FROM todo_lists WHERE userId = :userId")
    fun getAllListsForUser(userId: Int): Flow<List<ToDoList>>

    @Query("SELECT * FROM todo_lists WHERE userId != :userId")
    fun getAllListsExceptUser(userId: Int): Flow<List<ToDoList>>

    @Query("SELECT username FROM users WHERE id = :userId")
    suspend fun getUsernameById(userId: Int): String?

    @Delete
    suspend fun deleteToDoList(toDoList: ToDoList)

    // Ads
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAd(ad: Ad)

    @Query("SELECT * FROM ads WHERE partnerId = :partnerId ORDER BY triggerLocation ASC")
    fun getAdsForPartner(partnerId: Int): Flow<List<Ad>>

    @Query("SELECT * FROM ads ORDER BY triggerLocation ASC")
    fun getAllAds(): Flow<List<Ad>>

    @Delete
    suspend fun deleteAd(ad: Ad)

    // Ad Metrics
    @Insert
    suspend fun insertAdMetric(metric: AdMetric)

    @Query("SELECT COUNT(*) FROM ad_metrics WHERE adId = :adId AND timestamp >= :startTime")
    fun getAdImpressions(adId: Int, startTime: Long): Flow<Int>

    @Query("SELECT COUNT(DISTINCT userId) FROM ad_metrics WHERE adId = :adId AND timestamp >= :startTime")
    fun getAdUniqueUsers(adId: Int, startTime: Long): Flow<Int>

    @Query("""
        SELECT COUNT(*) FROM ad_metrics 
        INNER JOIN ads ON ad_metrics.adId = ads.id 
        WHERE ads.partnerId = :partnerId AND ad_metrics.timestamp >= :startTime
    """)
    fun getPartnerTotalImpressions(partnerId: Int, startTime: Long): Flow<Int>

    @Query("""
        SELECT COUNT(DISTINCT userId) FROM ad_metrics 
        INNER JOIN ads ON ad_metrics.adId = ads.id 
        WHERE ads.partnerId = :partnerId AND ad_metrics.timestamp >= :startTime
    """)
    fun getPartnerTotalUniqueUsers(partnerId: Int, startTime: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM ad_metrics WHERE timestamp >= :startTime")
    fun getTotalImpressions(startTime: Long): Flow<Int>

    @Query("SELECT COUNT(DISTINCT userId) FROM ad_metrics WHERE timestamp >= :startTime")
    fun getTotalUniqueUsers(startTime: Long): Flow<Int>

    // Users
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: Int): Flow<User?>
    
    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    // Partners
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPartner(partner: Partner): Long

    @Update
    suspend fun updatePartner(partner: Partner)

    @Query("SELECT * FROM partners WHERE username = :username LIMIT 1")
    suspend fun getPartnerByUsername(username: String): Partner?

    @Query("SELECT * FROM partners WHERE id = :id LIMIT 1")
    fun getPartnerById(id: Int): Flow<Partner?>
}
