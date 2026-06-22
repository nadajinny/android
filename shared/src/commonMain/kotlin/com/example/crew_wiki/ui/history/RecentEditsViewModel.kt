package com.example.crew_wiki.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crew_wiki.model.RecentDocument
import com.example.crew_wiki.network.DocumentApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface RecentEditsUiState {
    data object Loading : RecentEditsUiState
    data class Success(val documents: List<RecentDocument>) : RecentEditsUiState
    data class Error(val message: String) : RecentEditsUiState
}

class RecentEditsViewModel(
    private val apiService: DocumentApiService,
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecentEditsUiState>(RecentEditsUiState.Loading)
    val uiState: StateFlow<RecentEditsUiState> = _uiState.asStateFlow()

    init {
        loadRecentEdits()
    }

    fun loadRecentEdits() {
        viewModelScope.launch {
            _uiState.value = RecentEditsUiState.Loading
            try {
                val page = apiService.getDocuments(
                    pageNumber = 0,
                    pageSize = 50,
                    sort = "generateTime",
                    sortDirection = "DESC",
                )
                val docs = page.data.map { dto ->
                    RecentDocument(
                        uuid = dto.uuid,
                        title = dto.title,
                        generateTime = dto.generateTime,
                        documentType = dto.documentType,
                    )
                }
                _uiState.value = RecentEditsUiState.Success(docs)
            } catch (e: Exception) {
                _uiState.value = RecentEditsUiState.Error(e.message ?: "불러오기 실패")
            }
        }
    }
}
