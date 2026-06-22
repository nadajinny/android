package com.example.crew_wiki.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crew_wiki.CrewWikiDesignTokens
import com.example.crew_wiki.network.dto.DocumentSearchResponseDto
import com.example.crew_wiki.ui.common.SearchIcon

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onResultClick: (uuid: String, documentType: String) -> Unit,
) {
    val query by viewModel.query.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val colors = CrewWikiDesignTokens.colors
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.white),
    ) {
        // 검색창
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.primary.base)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                if (query.isEmpty()) {
                    Text(
                        text = "문서 검색",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 14.sp,
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = viewModel::onQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White,
                        fontSize = 14.sp,
                    ),
                    cursorBrush = SolidColor(Color.White),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = { keyboard?.hide() },
                    ),
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = SearchIcon,
                contentDescription = "검색",
                tint = Color.White,
                modifier = Modifier.size(24.dp),
            )
        }

        // 결과 영역
        when (val state = uiState) {
            is SearchUiState.Idle -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "검색어를 입력하세요",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.grayscale.lightText,
                        fontSize = 14.sp,
                    )
                }
            }

            is SearchUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = colors.primary.base)
                }
            }

            is SearchUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.error.base,
                        fontSize = 14.sp,
                    )
                }
            }

            is SearchUiState.Success -> {
                if (state.results.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "검색 결과가 없습니다",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.grayscale.lightText,
                            fontSize = 14.sp,
                        )
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.results) { doc ->
                            SearchResultItem(
                                doc = doc,
                                onClick = { onResultClick(doc.uuid, doc.documentType) },
                            )
                            HorizontalDivider(color = colors.grayscale.border)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    doc: DocumentSearchResponseDto,
    onClick: () -> Unit,
) {
    val colors = CrewWikiDesignTokens.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = doc.title,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.grayscale.text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (doc.documentType == "ORGANIZATION") "조직 문서" else "일반 문서",
                style = MaterialTheme.typography.bodySmall,
                color = colors.grayscale.lightText,
                fontSize = 12.sp,
            )
        }
    }
}
