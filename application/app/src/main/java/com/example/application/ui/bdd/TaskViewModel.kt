package com.example.application.ui.bdd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.application.saveLoggedInPartnerId
import com.example.application.saveLoggedInUserId
import com.example.application.clearLoggedInSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _currentPartner = MutableStateFlow<Partner?>(null)
    val currentPartner: StateFlow<Partner?> = _currentPartner.asStateFlow()

    private val _isSessionRestored = MutableStateFlow(false)
    val isSessionRestored: StateFlow<Boolean> = _isSessionRestored.asStateFlow()

    fun restoreSession(userId: Int, partnerId: Int) {
        viewModelScope.launch {
            if (userId != -1) {
                taskRepository.getUserById(userId).collect { user ->
                    if (user != null && _currentUser.value == null) {
                        _currentUser.value = user
                        _isSessionRestored.value = true
                    }
                }
            } else if (partnerId != -1) {
                taskRepository.getPartnerById(partnerId).collect { partner ->
                    if (partner != null && _currentPartner.value == null) {
                        _currentPartner.value = partner
                        _isSessionRestored.value = true
                    }
                }
            }
        }
    }

    val taskUiState: StateFlow<List<Task>> = taskRepository.getAllTasksStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addTask(task: Task) {
        viewModelScope.launch {
            taskRepository.insertTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }

    fun updateTaskStatus(id: Int, isDone: Boolean) {
        viewModelScope.launch {
            taskRepository.updateTaskStatus(id, isDone)
        }
    }

    fun getAllToDoListsForUser(userId: Int) = taskRepository.getAllListsForUser(userId).asLiveData()

    fun getAllCommunityToDoLists(userId: Int) = taskRepository.getAllListsExceptUser(userId).asLiveData()

    suspend fun getUsernameById(userId: Int): String? = taskRepository.getUsernameById(userId)

    fun insertToDoList(list: ToDoList) = viewModelScope.launch {
        taskRepository.insertToDoList(list)
    }

    fun deleteToDoList(list: ToDoList) = viewModelScope.launch {
        taskRepository.deleteToDoList(list)
    }

    // Ads logic
    fun getAdsForPartner(partnerId: Int) = taskRepository.getAdsForPartner(partnerId).asLiveData()
    val allAds = taskRepository.allAds.asLiveData()

    fun insertAd(ad: Ad) = viewModelScope.launch {
        taskRepository.insertAd(ad)
    }

    fun deleteAd(ad: Ad) = viewModelScope.launch {
        taskRepository.deleteAd(ad)
    }

    // Ad Metrics
    fun insertAdMetric(metric: AdMetric) = viewModelScope.launch {
        taskRepository.insertAdMetric(metric)
    }

    fun getAdImpressions(adId: Int, startTime: Long) = taskRepository.getAdImpressions(adId, startTime)
    fun getAdUniqueUsers(adId: Int, startTime: Long) = taskRepository.getAdUniqueUsers(adId, startTime)
    fun getPartnerTotalImpressions(partnerId: Int, startTime: Long) = taskRepository.getPartnerTotalImpressions(partnerId, startTime)
    fun getPartnerTotalUniqueUsers(partnerId: Int, startTime: Long) = taskRepository.getPartnerTotalUniqueUsers(partnerId, startTime)
    fun getTotalImpressions(startTime: Long) = taskRepository.getTotalImpressions(startTime)
    fun getTotalUniqueUsers(startTime: Long) = taskRepository.getTotalUniqueUsers(startTime)

    // Auth logic
    fun loginUser(username: String, mdp: String, context: android.content.Context, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            val user = taskRepository.getUserByUsername(username)
            if (user != null && user.mdp == mdp) {
                context.saveLoggedInUserId(user.id)
                _currentUser.value = user
                onResult(user)
            } else {
                onResult(null)
            }
        }
    }

    fun registerUser(user: User, context: android.content.Context, onResult: (User) -> Unit = {}) {
        viewModelScope.launch {
            val id = taskRepository.insertUser(user)
            val registeredUser = user.copy(id = id.toInt())
            context.saveLoggedInUserId(registeredUser.id)
            _currentUser.value = registeredUser
            onResult(registeredUser)
        }
    }

    fun loginPartner(username: String, mdp: String, context: android.content.Context, onResult: (Partner?) -> Unit) {
        viewModelScope.launch {
            val partner = taskRepository.getPartnerByUsername(username)
            if (partner != null && partner.mdp == mdp) {
                context.saveLoggedInPartnerId(partner.id)
                _currentPartner.value = partner
                onResult(partner)
            } else {
                onResult(null)
            }
        }
    }

    fun registerPartner(partner: Partner, context: android.content.Context, onResult: (Partner) -> Unit = {}) {
        viewModelScope.launch {
            val id = taskRepository.insertPartner(partner)
            val registeredPartner = partner.copy(id = id.toInt())
            context.saveLoggedInPartnerId(registeredPartner.id)
            _currentPartner.value = registeredPartner
            onResult(registeredPartner)
        }
    }

    fun logout(context: android.content.Context) {
        viewModelScope.launch {
            context.clearLoggedInSession()
            _currentUser.value = null
            _currentPartner.value = null
            _isSessionRestored.value = false
        }
    }

    fun updateCurrentUser(user: User) {
        viewModelScope.launch {
            taskRepository.updateUser(user)
            _currentUser.value = user
        }
    }

    fun updateCurrentPartner(partner: Partner) {
        viewModelScope.launch {
            taskRepository.updatePartner(partner)
            _currentPartner.value = partner
        }
    }
}
