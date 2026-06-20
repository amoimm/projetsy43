package com.example.application.ui.bdd

import kotlinx.coroutines.flow.Flow

class OfflineTaskRepository(private val taskDao: TaskDao) : TaskRepository {
    override fun getAllTasksStream(): Flow<List<Task>> = taskDao.getAllTasks()

    override fun getTaskStream(id: Int): Flow<Task> = taskDao.getTask(id)

    override suspend fun insertTask(task: Task) = taskDao.insert(task)

    override suspend fun deleteTask(task: Task) = taskDao.delete(task)

    override suspend fun updateTask(task: Task) = taskDao.update(task)

    override suspend fun updateTaskStatus(id: Int, isDone: Boolean) = taskDao.updateTaskStatus(id, isDone)

    override suspend fun deleteAllTasks() = taskDao.deleteAllTasks()

    override fun getAllListsForUser(userId: Int): Flow<List<ToDoList>> = taskDao.getAllListsForUser(userId)

    override suspend fun insertToDoList(list: ToDoList) = taskDao.insertToDoList(list)

    override suspend fun deleteToDoList(list: ToDoList) = taskDao.deleteToDoList(list)

    // Ads
    override fun getAdsForPartner(partnerId: Int): Flow<List<Ad>> = taskDao.getAdsForPartner(partnerId)

    override val allAds: Flow<List<Ad>> = taskDao.getAllAds()
    
    override suspend fun insertAd(ad: Ad) = taskDao.insertAd(ad)
    
    override suspend fun deleteAd(ad: Ad) = taskDao.deleteAd(ad)

    // Ad Metrics
    override suspend fun insertAdMetric(metric: AdMetric) = taskDao.insertAdMetric(metric)

    override fun getAdImpressions(adId: Int, startTime: Long): Flow<Int> = taskDao.getAdImpressions(adId, startTime)

    override fun getAdUniqueUsers(adId: Int, startTime: Long): Flow<Int> = taskDao.getAdUniqueUsers(adId, startTime)

    override fun getPartnerTotalImpressions(partnerId: Int, startTime: Long): Flow<Int> = taskDao.getPartnerTotalImpressions(partnerId, startTime)

    override fun getPartnerTotalUniqueUsers(partnerId: Int, startTime: Long): Flow<Int> = taskDao.getPartnerTotalUniqueUsers(partnerId, startTime)

    override fun getTotalImpressions(startTime: Long): Flow<Int> = taskDao.getTotalImpressions(startTime)

    override fun getTotalUniqueUsers(startTime: Long): Flow<Int> = taskDao.getTotalUniqueUsers(startTime)

    // Users
    override suspend fun insertUser(user: User) = taskDao.insertUser(user)
    override suspend fun updateUser(user: User) = taskDao.updateUser(user)
    override suspend fun getUserByUsername(username: String): User? = taskDao.getUserByUsername(username)
    override fun getUserById(id: Int): Flow<User?> = taskDao.getUserById(id)

    // Partners
    override suspend fun insertPartner(partner: Partner) = taskDao.insertPartner(partner)
    override suspend fun updatePartner(partner: Partner) = taskDao.updatePartner(partner)
    override suspend fun getPartnerByUsername(username: String): Partner? = taskDao.getPartnerByUsername(username)
    override fun getPartnerById(id: Int): Flow<Partner?> = taskDao.getPartnerById(id)
}
