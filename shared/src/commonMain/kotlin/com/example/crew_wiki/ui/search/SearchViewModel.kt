package com.example.crew_wiki.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crew_wiki.network.DocumentApiService
import com.example.crew_wiki.network.dto.DocumentSearchResponseDto
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Loading : SearchUiState
    data class Success(val results: List<DocumentSearchResponseDto>) : SearchUiState
    data class Error(val message: String) : SearchUiState
}

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val apiService: DocumentApiService,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        _query
            .debounce(300L)
            .distinctUntilChanged()
            .onEach { q ->
                if (q.isBlank()) {
                    _uiState.value = SearchUiState.Idle
                } else {
                    search(q)
                }
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChange(query: String) {
        _query.value = query
    }

    private fun search(keyword: String) {
        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            _uiState.value = try {
                val results = apiService.searchDocuments(keyword)
                SearchUiState.Success(results)
            } catch (e: Exception) {
                SearchUiState.Error(e.message ?: "검색 실패")
            }
        }
    }
}
