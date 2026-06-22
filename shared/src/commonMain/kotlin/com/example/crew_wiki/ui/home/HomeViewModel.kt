package com.example.crew_wiki.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crew_wiki.data.document.NetworkDocumentRepository
import com.example.crew_wiki.model.CrewWikiDocumentDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** crew-wiki.site의 "대문" 문서 UUID */
private const val MAIN_DOCUMENT_UUID = "30a6c25d-4b88-11f0-99c4-0a270fc3fae1"

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val mainDocument: CrewWikiDocumentDetail?) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(
    private val documentRepository: NetworkDocumentRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadMainDocument()
    }

    fun loadMainDocument() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val mainDocument = documentRepository.fetchDocumentByUUID(MAIN_DOCUMENT_UUID)
                _uiState.value = HomeUiState.Success(mainDocument)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "불러오기 실패")
            }
        }
    }
}
