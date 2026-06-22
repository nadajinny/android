package com.example.crew_wiki.ui.document

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crew_wiki.data.document.NetworkDocumentRepository
import com.example.crew_wiki.model.DocumentLogSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DocumentLogsUiState {
    data object Loading : DocumentLogsUiState
    data class Success(
        val logs: List<DocumentLogSummary>,
        val currentPage: Int,
        val totalPage: Int,
        val isLoadingMore: Boolean,
    ) : DocumentLogsUiState
    data class Error(val message: String) : DocumentLogsUiState
}

class DocumentLogsViewModel(
    private val repository: NetworkDocumentRepository,
    private val documentUUID: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DocumentLogsUiState>(DocumentLogsUiState.Loading)
    val uiState: StateFlow<DocumentLogsUiState> = _uiState.asStateFlow()

    private val loadedLogs = mutableListOf<DocumentLogSummary>()
    private var currentPage = 0

    init {
        loadLogs()
    }

    fun loadLogs() {
        viewModelScope.launch {
            _uiState.value = DocumentLogsUiState.Loading
            loadedLogs.clear()
            currentPage = 0
            fetchPage(0)
        }
    }

    fun loadNextPage() {
        val state = _uiState.value as? DocumentLogsUiState.Success ?: return
        if (state.isLoadingMore || currentPage + 1 >= state.totalPage) return

        viewModelScope.launch {
            _uiState.value = state.copy(isLoadingMore = true)
            fetchPage(currentPage + 1)
        }
    }

    private suspend fun fetchPage(page: Int) {
        try {
            val (logs, totalPage) = repository.fetchDocumentLogsByUUID(documentUUID, page, 10)
            loadedLogs.addAll(logs)
            currentPage = page
            _uiState.value = DocumentLogsUiState.Success(
                logs = loadedLogs.toList(),
                currentPage = currentPage,
                totalPage = totalPage,
                isLoadingMore = false,
            )
        } catch (e: Exception) {
            _uiState.value = DocumentLogsUiState.Error(e.message ?: "편집 기록을 불러올 수 없습니다.")
        }
    }
}
