package com.example.application.ui.bdd

import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasksStream(): Flow<List<Task>>
    fun getTaskStream(id: Int): Flow<Task>
    suspend fun insertTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun updateTaskStatus(id: Int, isDone: Boolean)
    suspend fun deleteAllTasks()

    fun getAllListsForUser(userId: Int): Flow<List<ToDoList>>
    suspend fun insertToDoList(list: ToDoList)
    suspend fun deleteToDoList(list: ToDoList)

    // Ads
    fun getAdsForPartner(partnerId: Int): Flow<List<Ad>>
    val allAds: Flow<List<Ad>>
    suspend fun insertAd(ad: Ad)
    suspend fun deleteAd(ad: Ad)

    // Ad Metrics
    suspend fun insertAdMetric(metric: AdMetric)
    fun getAdImpressions(adId: Int, startTime: Long): Flow<Int>
    fun getAdUniqueUsers(adId: Int, startTime: Long): Flow<Int>
    fun getTotalImpressions(startTime: Long): Flow<Int>
    fun getTotalUniqueUsers(startTime: Long): Flow<Int>

    // Users
    suspend fun insertUser(user: User): Long
    suspend fun updateUser(user: User)
    suspend fun getUserByUsername(username: String): User?
    fun getUserById(id: Int): Flow<User?>

    // Partners
    suspend fun insertPartner(partner: Partner): Long
    suspend fun updatePartner(partner: Partner)
    suspend fun getPartnerByUsername(username: String): Partner?
    fun getPartnerById(id: Int): Flow<Partner?>
}
