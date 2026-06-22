package com.example.crew_wiki.ui.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crew_wiki.data.group.GroupDocumentRepository
import com.example.crew_wiki.model.GroupDocumentDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface GroupDetailUiState {
    data object Loading : GroupDetailUiState
    data class Success(val detail: GroupDocumentDetail) : GroupDetailUiState
    data class Error(val message: String) : GroupDetailUiState
}

class GroupDetailViewModel(
    private val repository: GroupDocumentRepository,
    private val groupUUID: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow<GroupDetailUiState>(GroupDetailUiState.Loading)
    val uiState: StateFlow<GroupDetailUiState> = _uiState.asStateFlow()

    init {
        loadGroupDocument()
    }

    fun loadGroupDocument() {
        viewModelScope.launch {
            _uiState.value = GroupDetailUiState.Loading
            try {
                val detail = repository.fetchGroupDocumentByUUID(groupUUID)
                _uiState.value = GroupDetailUiState.Success(detail)
            } catch (e: Exception) {
                _uiState.value = GroupDetailUiState.Error(e.message ?: "그룹 문서를 불러올 수 없습니다.")
            }
        }
    }
}
