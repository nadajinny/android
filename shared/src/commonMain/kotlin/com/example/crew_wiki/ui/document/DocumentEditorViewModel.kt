package com.example.crew_wiki.ui.document

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crew_wiki.randomUuid
import com.example.crew_wiki.network.DocumentApiService
import com.example.crew_wiki.network.GroupApiService
import com.example.crew_wiki.network.dto.DocumentSaveRequestDto
import com.example.crew_wiki.network.dto.DocumentSearchResponseDto
import com.example.crew_wiki.network.dto.OrganizationDocumentCreateRequestDto
import com.example.crew_wiki.network.dto.OrganizationDocumentLinkRequestDto
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

sealed interface DocumentEditorMode {
    data object Post : DocumentEditorMode
    data class Edit(val documentId: String) : DocumentEditorMode
}

data class EditorOrganization(
    val uuid: String,
    val title: String,
    val isNew: Boolean,
)

data class DocumentEditorUiState(
    val screenTitle: String = "작성하기",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isTitleEditable: Boolean = true,
    val title: String = "",
    val writer: String = "",
    val contents: String = "",
    val titleError: String? = null,
    val writerError: String? = null,
    val contentsError: String? = null,
    val saveError: String? = null,
    val organizationQuery: String = "",
    val organizationSuggestions: List<DocumentSearchResponseDto> = emptyList(),
    val organizations: List<EditorOrganization> = emptyList(),
    val isSearchingOrganizations: Boolean = false,
    val isConflictDialogVisible: Boolean = false,
    val conflictContent: String = "",
)

sealed interface DocumentEditorEvent {
    data class Saved(val documentId: String) : DocumentEditorEvent
}

@OptIn(FlowPreview::class)
class DocumentEditorViewModel(
    private val documentApiService: DocumentApiService,
    private val groupApiService: GroupApiService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DocumentEditorUiState())
    val uiState: StateFlow<DocumentEditorUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<DocumentEditorEvent>()
    val events: SharedFlow<DocumentEditorEvent> = _events.asSharedFlow()

    private val organizationQuery = MutableStateFlow("")

    private var mode: DocumentEditorMode? = null
    private var currentDocumentId: String? = null
    private var originalVersion: Long = 0
    private var conflictVersion: Long = -1
    private var originalOrganizations: List<EditorOrganization> = emptyList()

    init {
        organizationQuery
            .debounce(250L)
            .distinctUntilChanged()
            .onEach { query ->
                if (query.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        organizationSuggestions = emptyList(),
                        isSearchingOrganizations = false,
                    )
                } else {
                    searchOrganizations(query)
                }
            }
            .launchIn(viewModelScope)
    }

    fun load(targetMode: DocumentEditorMode) {
        when (targetMode) {
            DocumentEditorMode.Post -> {
                if (mode == DocumentEditorMode.Post && _uiState.value.contents.isNotBlank()) {
                    return
                }
            }

            is DocumentEditorMode.Edit -> {
                if (mode == targetMode && currentDocumentId == targetMode.documentId) {
                    return
                }
            }
        }

        mode = targetMode
        when (targetMode) {
            DocumentEditorMode.Post -> initializePost()
            is DocumentEditorMode.Edit -> loadDocument(targetMode.documentId)
        }
    }

    fun onTitleChange(value: String) {
        if (!_uiState.value.isTitleEditable) return

        val limited = value.take(TITLE_MAX_LENGTH)
        val error = if (value.length > TITLE_MAX_LENGTH) TITLE_LENGTH_ERROR else null
        _uiState.value = _uiState.value.copy(
            title = limited,
            titleError = error,
            saveError = null,
        )
    }

    fun onWriterChange(value: String) {
        val onlyKorean = value.filter { it.isKoreanCharacter() }
        val limited = onlyKorean.take(WRITER_MAX_LENGTH)
        val error = when {
            onlyKorean.length != value.length -> WRITER_KOREAN_ONLY_ERROR
            onlyKorean.length > WRITER_MAX_LENGTH -> WRITER_MAX_LENGTH_ERROR
            value.length > WRITER_MAX_LENGTH -> WRITER_MAX_LENGTH_ERROR
            else -> null
        }
        _uiState.value = _uiState.value.copy(
            writer = limited,
            writerError = error,
            saveError = null,
        )
    }

    fun onContentsChange(value: String) {
        _uiState.value = _uiState.value.copy(
            contents = value,
            contentsError = if (value.isBlank()) CONTENT_REQUIRED_ERROR else null,
            saveError = null,
        )
    }

    fun onOrganizationQueryChange(value: String) {
        _uiState.value = _uiState.value.copy(
            organizationQuery = value,
            saveError = null,
        )
        organizationQuery.value = value
    }

    fun addOrganizationFromSuggestion(suggestion: DocumentSearchResponseDto) {
        if (suggestion.documentType != ORGANIZATION_TYPE) return
        if (_uiState.value.organizations.any { it.uuid == suggestion.uuid }) return

        _uiState.value = _uiState.value.copy(
            organizations = _uiState.value.organizations + EditorOrganization(
                uuid = suggestion.uuid,
                title = suggestion.title,
                isNew = false,
            ),
            organizationQuery = "",
            organizationSuggestions = emptyList(),
            isSearchingOrganizations = false,
            saveError = null,
        )
        organizationQuery.value = ""
    }

    fun addOrganizationFromQuery() {
        val query = _uiState.value.organizationQuery.trim()
        if (query.isBlank()) return

        viewModelScope.launch {
            try {
                val exactMatch = documentApiService.searchDocuments(query)
                    .firstOrNull { it.documentType == ORGANIZATION_TYPE && it.title == query }

                if (exactMatch != null) {
                    addOrganizationFromSuggestion(exactMatch)
                } else if (_uiState.value.organizations.none { it.title == query }) {
                    _uiState.value = _uiState.value.copy(
                        organizations = _uiState.value.organizations + EditorOrganization(
                            uuid = randomUuid(),
                            title = query,
                            isNew = true,
                        ),
                        organizationQuery = "",
                        organizationSuggestions = emptyList(),
                        saveError = null,
                    )
                    organizationQuery.value = ""
                } else {
                    _uiState.value = _uiState.value.copy(
                        organizationQuery = "",
                        organizationSuggestions = emptyList(),
                    )
                    organizationQuery.value = ""
                }
            } catch (_: Exception) {
                if (_uiState.value.organizations.none { it.title == query }) {
                    _uiState.value = _uiState.value.copy(
                        organizations = _uiState.value.organizations + EditorOrganization(
                            uuid = randomUuid(),
                            title = query,
                            isNew = true,
                        ),
                        organizationQuery = "",
                        organizationSuggestions = emptyList(),
                        saveError = null,
                    )
                    organizationQuery.value = ""
                }
            }
        }
    }

    fun removeOrganization(uuid: String) {
        _uiState.value = _uiState.value.copy(
            organizations = _uiState.value.organizations.filterNot { it.uuid == uuid },
            saveError = null,
        )
    }

    fun dismissConflictDialog() {
        _uiState.value = _uiState.value.copy(isConflictDialogVisible = false)
    }

    fun onConflictContentChange(value: String) {
        _uiState.value = _uiState.value.copy(conflictContent = value)
    }

    fun save() {
        viewModelScope.launch {
            if (!validateFields()) return@launch

            _uiState.value = _uiState.value.copy(
                isSaving = true,
                saveError = null,
            )

            try {
                when (val currentMode = mode) {
                    null -> return@launch
                    DocumentEditorMode.Post -> {
                        if (hasDuplicateTitle(_uiState.value.title.trim())) {
                            _uiState.value = _uiState.value.copy(
                                isSaving = false,
                                titleError = DUPLICATE_TITLE_ERROR,
                            )
                            return@launch
                        }

                        val documentId = saveDocument(_uiState.value.contents)
                        _events.emit(DocumentEditorEvent.Saved(documentId))
                        _uiState.value = _uiState.value.copy(isSaving = false)
                    }

                    is DocumentEditorMode.Edit -> {
                        val latest = documentApiService.getDocumentByUUID(currentMode.documentId)
                        if (latest.latestVersion != originalVersion) {
                            conflictVersion = latest.latestVersion
                            _uiState.value = _uiState.value.copy(
                                isSaving = false,
                                isConflictDialogVisible = true,
                                conflictContent = createConflictText(
                                    remoteContent = latest.contents,
                                    localContent = _uiState.value.contents,
                                ),
                                saveError = CONFLICT_ERROR,
                            )
                            return@launch
                        }

                        val documentId = saveDocument(_uiState.value.contents)
                        _events.emit(DocumentEditorEvent.Saved(documentId))
                        _uiState.value = _uiState.value.copy(isSaving = false)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveError = e.message ?: SAVE_ERROR,
                )
            }
        }
    }

    fun resolveConflict() {
        viewModelScope.launch {
            val current = _uiState.value
            if (current.conflictContent.containsConflictMarker()) {
                _uiState.value = current.copy(saveError = CONFLICT_RESOLVE_ERROR)
                return@launch
            }

            val documentId = currentDocumentId ?: return@launch
            _uiState.value = current.copy(isSaving = true, saveError = null)

            try {
                val latest = documentApiService.getDocumentByUUID(documentId)
                if (latest.latestVersion != conflictVersion) {
                    conflictVersion = latest.latestVersion
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        isConflictDialogVisible = true,
                        conflictContent = createConflictText(
                            remoteContent = latest.contents,
                            localContent = current.conflictContent,
                        ),
                        saveError = CONFLICT_CHANGED_ERROR,
                    )
                    return@launch
                }

                val savedId = saveDocument(current.conflictContent)
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    isConflictDialogVisible = false,
                    conflictContent = "",
                )
                _events.emit(DocumentEditorEvent.Saved(savedId))
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveError = e.message ?: SAVE_ERROR,
                )
            }
        }
    }

    private fun initializePost() {
        currentDocumentId = null
        originalVersion = 0
        conflictVersion = -1
        originalOrganizations = emptyList()
        _uiState.value = DocumentEditorUiState(
            screenTitle = "작성하기",
            isTitleEditable = true,
            contents = DEFAULT_EDITOR_VALUE,
        )
        organizationQuery.value = ""
    }

    private fun loadDocument(documentId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, saveError = null)
            try {
                val document = documentApiService.getDocumentByUUID(documentId)
                currentDocumentId = documentId
                originalVersion = document.latestVersion
                originalOrganizations = document.organizationDocumentResponses.map {
                    EditorOrganization(
                        uuid = it.organizationDocumentUuid,
                        title = it.title,
                        isNew = false,
                    )
                }
                conflictVersion = -1
                _uiState.value = DocumentEditorUiState(
                    screenTitle = "편집하기",
                    isLoading = false,
                    isTitleEditable = false,
                    title = document.title,
                    writer = document.writer,
                    contents = document.contents,
                    organizations = originalOrganizations,
                )
                organizationQuery.value = ""
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    saveError = e.message ?: LOAD_ERROR,
                )
            }
        }
    }

    private suspend fun searchOrganizations(query: String) {
        _uiState.value = _uiState.value.copy(isSearchingOrganizations = true)
        _uiState.value = try {
            val suggestions = documentApiService.searchDocuments(query)
                .filter { it.documentType == ORGANIZATION_TYPE }
                .filterNot { suggestion ->
                    _uiState.value.organizations.any { it.uuid == suggestion.uuid }
                }
            _uiState.value.copy(
                organizationSuggestions = suggestions,
                isSearchingOrganizations = false,
            )
        } catch (_: Exception) {
            _uiState.value.copy(
                organizationSuggestions = emptyList(),
                isSearchingOrganizations = false,
            )
        }
    }

    private suspend fun hasDuplicateTitle(title: String): Boolean =
        documentApiService.searchDocuments(title)
            .any { it.title.trim() == title }

    private fun validateFields(): Boolean {
        val state = _uiState.value
        val titleError = when {
            state.title.isBlank() -> TITLE_REQUIRED_ERROR
            state.title.length > TITLE_MAX_LENGTH -> TITLE_LENGTH_ERROR
            else -> state.titleError
        }
        val writerError = when {
            state.writer.isBlank() -> WRITER_REQUIRED_ERROR
            else -> state.writerError
        }
        val contentsError = if (state.contents.isBlank()) CONTENT_REQUIRED_ERROR else null

        _uiState.value = state.copy(
            titleError = titleError,
            writerError = writerError,
            contentsError = contentsError,
        )

        return titleError == null && writerError == null && contentsError == null
    }

    private suspend fun saveDocument(contents: String): String {
        val state = _uiState.value
        val request = DocumentSaveRequestDto(
            title = state.title.trim(),
            contents = contents,
            writer = state.writer.trim(),
            documentBytes = contents.encodeToByteArray().size.toLong(),
            uuid = currentDocumentId ?: randomUuid(),
        )

        val currentMode = mode ?: error("Editor mode is not initialized")
        val savedDocument = when (currentMode) {
            DocumentEditorMode.Post -> documentApiService.postDocument(request)
            is DocumentEditorMode.Edit -> documentApiService.putDocument(request)
        }

        when (currentMode) {
            DocumentEditorMode.Post -> syncOrganizationsOnCreate(savedDocument.documentUUID, state.writer.trim())
            is DocumentEditorMode.Edit -> syncOrganizationsOnEdit(savedDocument.documentUUID, state.writer.trim())
        }

        return savedDocument.documentUUID
    }

    private suspend fun syncOrganizationsOnCreate(documentId: String, writer: String) {
        val organizations = _uiState.value.organizations
        organizations.filter { it.isNew }.forEach { organization ->
            groupApiService.postOrganizationDocument(
                OrganizationDocumentCreateRequestDto(
                    title = organization.title,
                    contents = DEFAULT_ORGANIZATION_EDITOR_VALUE,
                    writer = writer,
                    documentBytes = 0,
                    crewDocumentUuid = documentId,
                    organizationDocumentUuid = organization.uuid,
                ),
            )
        }

        organizations.filterNot { it.isNew }.forEach { organization ->
            groupApiService.linkOrganizationDocument(
                OrganizationDocumentLinkRequestDto(
                    crewDocumentUuid = documentId,
                    organizationDocumentUuid = organization.uuid,
                ),
            )
        }
    }

    private suspend fun syncOrganizationsOnEdit(documentId: String, writer: String) {
        val currentOrganizations = _uiState.value.organizations
        val newOrganizations = currentOrganizations.filter { it.isNew }
        val newlyLinkedOrganizations = currentOrganizations.filter { organization ->
            !organization.isNew && originalOrganizations.none { it.uuid == organization.uuid }
        }
        val deletedOrganizations = originalOrganizations.filter { original ->
            currentOrganizations.none { it.uuid == original.uuid }
        }

        newOrganizations.forEach { organization ->
            groupApiService.postOrganizationDocument(
                OrganizationDocumentCreateRequestDto(
                    title = organization.title,
                    contents = DEFAULT_ORGANIZATION_EDITOR_VALUE,
                    writer = writer,
                    documentBytes = 0,
                    crewDocumentUuid = documentId,
                    organizationDocumentUuid = organization.uuid,
                ),
            )
        }

        newlyLinkedOrganizations.forEach { organization ->
            groupApiService.linkOrganizationDocument(
                OrganizationDocumentLinkRequestDto(
                    crewDocumentUuid = documentId,
                    organizationDocumentUuid = organization.uuid,
                ),
            )
        }

        deletedOrganizations.forEach { organization ->
            documentApiService.deleteOrganizationFromDocument(documentId, organization.uuid)
        }
    }

    private fun createConflictText(remoteContent: String, localContent: String): String = buildString {
        appendLine("≪≪≪≪≪≪≪ 내 버전")
        append(localContent)
        if (!localContent.endsWith("\n")) appendLine()
        appendLine("-=-=-=-=-=-=-=-=")
        append(remoteContent)
        if (!remoteContent.endsWith("\n")) appendLine()
        append("≫≫≫≫≫≫≫ 최신 버전")
    }

    private fun Char.isKoreanCharacter(): Boolean =
        this in 'ㄱ'..'ㅎ' || this in '가'..'힣'

    private fun String.containsConflictMarker(): Boolean =
        contains("≪≪≪≪≪≪≪") || contains("≫≫≫≫≫≫≫") || contains("-=-=-=-=-=-=-=-=")

    companion object {
        private const val ORGANIZATION_TYPE = "ORGANIZATION"
        private const val TITLE_MAX_LENGTH = 12
        private const val WRITER_MAX_LENGTH = 4
        private const val TITLE_LENGTH_ERROR = "제목은 12자가 최대에요"
        private const val DUPLICATE_TITLE_ERROR = "이미 있는 문서입니다"
        private const val WRITER_KOREAN_ONLY_ERROR = "닉네임은 한글만 입력할 수 있어요"
        private const val WRITER_MAX_LENGTH_ERROR = "닉네임은 4자가 최대에요"
        private const val TITLE_REQUIRED_ERROR = "문서의 제목을 입력해 주세요"
        private const val WRITER_REQUIRED_ERROR = "편집자를 입력해 주세요"
        private const val CONTENT_REQUIRED_ERROR = "본문을 입력해 주세요"
        private const val SAVE_ERROR = "저장에 실패했습니다. 잠시 후 다시 시도해 주세요."
        private const val LOAD_ERROR = "문서를 불러오지 못했습니다."
        private const val CONFLICT_ERROR = "다른 사용자가 먼저 편집했습니다. 병합 후 다시 저장해 주세요."
        private const val CONFLICT_RESOLVE_ERROR = "충돌 마커를 제거한 뒤 저장해 주세요."
        private const val CONFLICT_CHANGED_ERROR = "병합 중 새로운 변경사항이 생겼습니다. 다시 확인해 주세요."
        private const val DEFAULT_EDITOR_VALUE = """# 프로필
<table>
  <tr>
    <th>닉네임</th>
    <td></td>
  </tr>
  <tr>
    <th>생일</th>
    <td></td>
  </tr>
  <tr>
    <th>소속/기수</th>
    <td></td>
  </tr>
  <tr>
    <th>MBTI</th>
    <td></td>
  </tr>
</table>"""
        private const val DEFAULT_ORGANIZATION_EDITOR_VALUE = """# 그룹 정보
## 결성일

## 그룹 설명

# 구성원
"""
    }
}
