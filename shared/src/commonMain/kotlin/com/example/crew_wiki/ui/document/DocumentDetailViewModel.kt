package com.example.crew_wiki.ui.document

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crew_wiki.data.document.NetworkDocumentRepository
import com.example.crew_wiki.model.CrewWikiDocumentDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DocumentDetailUiState {
    data object Loading : DocumentDetailUiState
    data class Success(val detail: CrewWikiDocumentDetail) : DocumentDetailUiState
    data object NotFound : DocumentDetailUiState
    data class Error(val message: String) : DocumentDetailUiState
}

class DocumentDetailViewModel(
    private val repository: NetworkDocumentRepository,
    private val documentUUID: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DocumentDetailUiState>(DocumentDetailUiState.Loading)
    val uiState: StateFlow<DocumentDetailUiState> = _uiState.asStateFlow()

    init {
        loadDocument()
    }

    fun loadDocument() {
        viewModelScope.launch {
            _uiState.value = DocumentDetailUiState.Loading
            try {
                val detail = repository.fetchDocumentByUUID(documentUUID)
                _uiState.value = DocumentDetailUiState.Success(detail)
            } catch (e: Exception) {
                _uiState.value = DocumentDetailUiState.Error(e.message ?: "문서를 불러올 수 없습니다.")
            }
        }
    }
}
