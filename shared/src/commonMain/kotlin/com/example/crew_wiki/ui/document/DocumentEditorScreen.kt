package com.example.crew_wiki.ui.document

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.crew_wiki.CrewWikiDesignTokens
import com.example.crew_wiki.ui.common.CrewWikiActionButton
import com.example.crew_wiki.ui.common.CrewWikiActionButtonStyle
import com.example.crew_wiki.ui.common.CrewWikiSurfaceSection
import com.example.crew_wiki.ui.common.CrewWikiTagChip
import com.example.crew_wiki.ui.common.LoadingScreen

@Composable
fun DocumentEditorScreen(
    viewModel: DocumentEditorViewModel,
    mode: DocumentEditorMode,
    onBackClick: () -> Unit,
    onSaved: (documentId: String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(mode) {
        viewModel.load(mode)
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is DocumentEditorEvent.Saved -> onSaved(event.documentId)
            }
        }
    }

    if (uiState.isLoading) {
        LoadingScreen()
        return
    }

    DocumentEditorContent(
        uiState = uiState,
        onTitleChange = viewModel::onTitleChange,
        onWriterChange = viewModel::onWriterChange,
        onContentsChange = viewModel::onContentsChange,
        onOrganizationQueryChange = viewModel::onOrganizationQueryChange,
        onAddOrganization = viewModel::addOrganizationFromQuery,
        onSelectOrganization = viewModel::addOrganizationFromSuggestion,
        onRemoveOrganization = viewModel::removeOrganization,
        onCancel = onBackClick,
        onSave = viewModel::save,
        onDismissConflict = viewModel::dismissConflictDialog,
        onConflictContentChange = viewModel::onConflictContentChange,
        onResolveConflict = viewModel::resolveConflict,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DocumentEditorContent(
    uiState: DocumentEditorUiState,
    onTitleChange: (String) -> Unit,
    onWriterChange: (String) -> Unit,
    onContentsChange: (String) -> Unit,
    onOrganizationQueryChange: (String) -> Unit,
    onAddOrganization: () -> Unit,
    onSelectOrganization: (com.example.crew_wiki.network.dto.DocumentSearchResponseDto) -> Unit,
    onRemoveOrganization: (String) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    onDismissConflict: () -> Unit,
    onConflictContentChange: (String) -> Unit,
    onResolveConflict: () -> Unit,
) {
    val colors = CrewWikiDesignTokens.colors
    val spacing = CrewWikiDesignTokens.spacing

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        item {
            CrewWikiSurfaceSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(spacing.lg),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = uiState.screenTitle,
                            style = MaterialTheme.typography.headlineMedium,
                            color = colors.grayscale.c800,
                            fontWeight = FontWeight.Bold,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                            CrewWikiActionButton(
                                text = "취소하기",
                                onClick = onCancel,
                                style = CrewWikiActionButtonStyle.Tertiary,
                            )
                            CrewWikiActionButton(
                                text = "작성완료",
                                onClick = onSave,
                                style = CrewWikiActionButtonStyle.Primary,
                            )
                        }
                    }

                    if (uiState.saveError != null) {
                        Text(
                            text = uiState.saveError,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.error.base,
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(spacing.md),
                        ) {
                            EditorField(
                                value = uiState.title,
                                onValueChange = onTitleChange,
                                label = "제목",
                                placeholder = "문서의 제목을 입력해 주세요",
                                enabled = uiState.isTitleEditable,
                                modifier = Modifier.weight(1f),
                                minLines = 1,
                                maxLines = 1,
                            )
                            EditorField(
                                value = uiState.writer,
                                onValueChange = onWriterChange,
                                label = "편집자",
                                placeholder = "편집자",
                                modifier = Modifier.weight(0.42f),
                                minLines = 1,
                                maxLines = 1,
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(spacing.md),
                        ) {
                            Text(
                                text = uiState.titleError.orEmpty(),
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.error.base,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                text = uiState.writerError.orEmpty(),
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.error.base,
                                modifier = Modifier.weight(0.42f),
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                        Text(
                            text = "소속",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.grayscale.c800,
                            fontWeight = FontWeight.Medium,
                        )
                        if (uiState.organizations.isNotEmpty()) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                                verticalArrangement = Arrangement.spacedBy(spacing.sm),
                            ) {
                                uiState.organizations.forEach { organization ->
                                    CrewWikiTagChip(
                                        text = "${organization.title} ×",
                                        onClick = { onRemoveOrganization(organization.uuid) },
                                    )
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                EditorField(
                                    value = uiState.organizationQuery,
                                    onValueChange = onOrganizationQueryChange,
                                    label = "소속 추가",
                                    placeholder = "소속을 추가해 주세요",
                                    minLines = 1,
                                    maxLines = 1,
                                )
                                if (uiState.isSearchingOrganizations) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp),
                                        horizontalArrangement = Arrangement.Center,
                                    ) {
                                        CircularProgressIndicator(
                                            color = colors.primary.base,
                                            modifier = Modifier.height(18.dp),
                                            strokeWidth = 2.dp,
                                        )
                                    }
                                } else if (uiState.organizationSuggestions.isNotEmpty()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp)
                                            .background(
                                                color = colors.white,
                                                shape = RoundedCornerShape(12.dp),
                                            ),
                                    ) {
                                        uiState.organizationSuggestions.forEachIndexed { index, suggestion ->
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable { onSelectOrganization(suggestion) }
                                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                            ) {
                                                Text(
                                                    text = suggestion.title,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = colors.grayscale.c800,
                                                )
                                                Text(
                                                    text = "조직 문서",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = colors.grayscale.lightText,
                                                )
                                            }
                                            if (index != uiState.organizationSuggestions.lastIndex) {
                                                HorizontalDivider(color = colors.grayscale.border)
                                            }
                                        }
                                    }
                                }
                            }
                            CrewWikiActionButton(
                                text = "추가하기",
                                onClick = onAddOrganization,
                                style = CrewWikiActionButtonStyle.Primary,
                                modifier = Modifier.padding(top = 28.dp),
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                        Text(
                            text = "본문",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.grayscale.c800,
                            fontWeight = FontWeight.Medium,
                        )
                        EditorField(
                            value = uiState.contents,
                            onValueChange = onContentsChange,
                            label = "본문",
                            placeholder = "마크다운으로 문서를 작성해 주세요",
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 18,
                            maxLines = 24,
                        )
                        if (uiState.contentsError != null) {
                            Text(
                                text = uiState.contentsError,
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.error.base,
                            )
                        }
                    }
                }
            }
        }
    }

    if (uiState.isConflictDialogVisible) {
        AlertDialog(
            onDismissRequest = onDismissConflict,
            title = {
                Text(
                    text = "문서 충돌 해결",
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.grayscale.c800,
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    Text(
                        text = "다른 사용자가 문서를 수정했습니다. 아래 내용을 병합한 뒤 충돌 마커를 모두 제거하고 저장해 주세요.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.grayscale.c700,
                    )
                    OutlinedTextField(
                        value = uiState.conflictContent,
                        onValueChange = onConflictContentChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp),
                        textStyle = MaterialTheme.typography.bodyMedium,
                        colors = editorFieldColors(),
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onResolveConflict) {
                    Text("충돌 해결 완료")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissConflict) {
                    Text("취소")
                }
            },
        )
    }
}

@Composable
private fun EditorField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
) {
    val colors = CrewWikiDesignTokens.colors

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = colors.grayscale.c700,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            textStyle = MaterialTheme.typography.bodyMedium,
            placeholder = {
                Text(
                    text = placeholder,
                    color = colors.grayscale.lightText,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            minLines = minLines,
            maxLines = maxLines,
            colors = editorFieldColors(),
        )
    }
}

@Composable
private fun editorFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = CrewWikiDesignTokens.colors.primary.base,
    unfocusedBorderColor = CrewWikiDesignTokens.colors.grayscale.border,
    disabledBorderColor = CrewWikiDesignTokens.colors.grayscale.border,
    focusedTextColor = CrewWikiDesignTokens.colors.grayscale.text,
    unfocusedTextColor = CrewWikiDesignTokens.colors.grayscale.text,
    disabledTextColor = CrewWikiDesignTokens.colors.grayscale.c500,
    focusedLabelColor = CrewWikiDesignTokens.colors.primary.base,
    cursorColor = CrewWikiDesignTokens.colors.primary.base,
)
