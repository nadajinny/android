package com.example.crew_wiki.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crew_wiki.model.RecentDocument
import com.example.crew_wiki.network.DocumentApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val recentDocuments: List<RecentDocument>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(
    private val apiService: DocumentApiService,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadRecentDocuments()
    }

    fun loadRecentDocuments() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                // generateTime DESC 정렬로 최근 편집 문서 20개 조회
                val page = apiService.getDocuments(
                    pageNumber = 0,
                    pageSize = 20,
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
                _uiState.value = HomeUiState.Success(docs)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "불러오기 실패")
            }
        }
    }
}
