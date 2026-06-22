package com.example.crew_wiki.ui.document

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crew_wiki.data.document.NetworkDocumentRepository
import com.example.crew_wiki.model.DocumentLogDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DocumentLogDetailUiState {
    data object Loading : DocumentLogDetailUiState
    data class Success(val log: DocumentLogDetail) : DocumentLogDetailUiState
    data class Error(val message: String) : DocumentLogDetailUiState
}

class DocumentLogDetailViewModel(
    private val repository: NetworkDocumentRepository,
    private val logId: Long,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DocumentLogDetailUiState>(DocumentLogDetailUiState.Loading)
    val uiState: StateFlow<DocumentLogDetailUiState> = _uiState.asStateFlow()

    init {
        loadLog()
    }

    fun loadLog() {
        viewModelScope.launch {
            _uiState.value = DocumentLogDetailUiState.Loading
            try {
                val log = repository.fetchDocumentLog(logId)
                _uiState.value = DocumentLogDetailUiState.Success(log)
            } catch (e: Exception) {
                _uiState.value = DocumentLogDetailUiState.Error(e.message ?: "로그를 불러올 수 없습니다.")
            }
        }
    }
}
