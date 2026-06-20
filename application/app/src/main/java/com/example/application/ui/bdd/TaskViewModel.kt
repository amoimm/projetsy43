package com.example.application.ui.bdd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
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

    val allToDoLists = taskRepository.allToDoLists.asLiveData()

    fun insertToDoList(list: ToDoList) = viewModelScope.launch {
        taskRepository.insertToDoList(list)
    }

    fun deleteToDoList(list: ToDoList) = viewModelScope.launch {
        taskRepository.deleteToDoList(list)
    }

    // Ads logic
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
    fun getTotalImpressions(startTime: Long) = taskRepository.getTotalImpressions(startTime)
    fun getTotalUniqueUsers(startTime: Long) = taskRepository.getTotalUniqueUsers(startTime)

    // Auth logic
    fun loginUser(username: String, mdp: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            val user = taskRepository.getUserByUsername(username)
            if (user != null && user.mdp == mdp) {
                _currentUser.value = user
                onResult(user)
            } else {
                onResult(null)
            }
        }
    }

    fun registerUser(user: User, onResult: (User) -> Unit = {}) {
        viewModelScope.launch {
            val id = taskRepository.insertUser(user)
            val registeredUser = user.copy(id = id.toInt())
            _currentUser.value = registeredUser
            onResult(registeredUser)
        }
    }

    fun loginPartner(username: String, mdp: String, onResult: (Partner?) -> Unit) {
        viewModelScope.launch {
            val partner = taskRepository.getPartnerByUsername(username)
            if (partner != null && partner.mdp == mdp) {
                _currentPartner.value = partner
                onResult(partner)
            } else {
                onResult(null)
            }
        }
    }

    fun registerPartner(partner: Partner, onResult: (Partner) -> Unit = {}) {
        viewModelScope.launch {
            val id = taskRepository.insertPartner(partner)
            val registeredPartner = partner.copy(id = id.toInt())
            _currentPartner.value = registeredPartner
            onResult(registeredPartner)
        }
    }

    fun logout() {
        _currentUser.value = null
        _currentPartner.value = null
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
