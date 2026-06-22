package com.example.crew_wiki.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crew_wiki.data.document.NetworkDocumentRepository
import com.example.crew_wiki.model.CrewWikiDocumentDetail
import com.example.crew_wiki.model.RecentDocument
import com.example.crew_wiki.network.DocumentApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** crew-wiki.site의 "대문" 문서 UUID */
private const val MAIN_DOCUMENT_UUID = "30a6c25d-4b88-11f0-99c4-0a270fc3fae1"

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val recentDocuments: List<RecentDocument>,
        val mainDocument: CrewWikiDocumentDetail?,
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(
    private val apiService: DocumentApiService,
    private val documentRepository: NetworkDocumentRepository,
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
                // 대문 문서는 없을 수도 있으므로 실패해도 홈 화면 전체는 정상 표시
                val mainDocument = try {
                    documentRepository.fetchDocumentByUUID(MAIN_DOCUMENT_UUID)
                } catch (_: Exception) {
                    null
                }
                _uiState.value = HomeUiState.Success(docs, mainDocument)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "불러오기 실패")
            }
        }
    }
}
