package com.example.crew_wiki.ui.popular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crew_wiki.data.document.NetworkDocumentRepository
import com.example.crew_wiki.model.PopularDocument
import com.example.crew_wiki.model.PopularSortType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PopularUiState {
    data object Loading : PopularUiState
    data class Success(
        val documentsByViews: List<PopularDocument>,
        val documentsByEdits: List<PopularDocument>,
    ) : PopularUiState
    data class Error(val message: String) : PopularUiState
}

class PopularDocumentsViewModel(
    private val repository: NetworkDocumentRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<PopularUiState>(PopularUiState.Loading)
    val uiState: StateFlow<PopularUiState> = _uiState.asStateFlow()

    init {
        loadPopularDocuments()
    }

    fun loadPopularDocuments() {
        viewModelScope.launch {
            _uiState.value = PopularUiState.Loading
            try {
                val byViews = repository.fetchPopularDocuments(PopularSortType.VIEWS)
                val byEdits = repository.fetchPopularDocuments(PopularSortType.EDITS)
                _uiState.value = PopularUiState.Success(
                    documentsByViews = byViews,
                    documentsByEdits = byEdits,
                )
            } catch (e: Exception) {
                _uiState.value = PopularUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
            }
        }
    }
}
