package com.example.crew_wiki.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.crew_wiki.CrewWikiDesignTokens
import com.example.crew_wiki.di.AppContainer
import com.example.crew_wiki.ui.common.ErrorScreen
import com.example.crew_wiki.ui.common.LoadingScreen
import com.example.crew_wiki.ui.document.DocumentDetailScreen
import com.example.crew_wiki.ui.document.DocumentDetailUiState
import com.example.crew_wiki.ui.document.DocumentDetailViewModel
import com.example.crew_wiki.ui.document.DocumentLogDetailScreen
import com.example.crew_wiki.ui.document.DocumentLogDetailViewModel
import com.example.crew_wiki.ui.document.DocumentLogsScreen
import com.example.crew_wiki.ui.document.DocumentLogsViewModel
import com.example.crew_wiki.ui.group.GroupDetailScreen
import com.example.crew_wiki.ui.group.GroupDetailViewModel
import com.example.crew_wiki.ui.popular.PopularDocumentsScreen
import com.example.crew_wiki.ui.popular.PopularDocumentsViewModel
import com.example.crew_wiki.ui.popular.PopularUiState

@Composable
fun CrewWikiNavRoot() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = CrewWikiRoute.Home,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .safeContentPadding()
            .fillMaxSize(),
    ) {
        addHomeDestination(navController)
        addPopularDestination(navController)
        addDocumentDestinations(navController)
        addGroupDestinations(navController)
        addStaticDestination<CrewWikiRoute.Statistics>("통계", "웹 화면을 대응시키기 위한 정적 목적지입니다.")
        addStaticDestination<CrewWikiRoute.Post>("문서 작성", "웹 화면을 대응시키기 위한 정적 목적지입니다.")
        addStaticDestination<CrewWikiRoute.AdminLogin>("관리자 로그인", "관리자 플로우를 별도 그래프로 분리할 후보 목적지입니다.")
        addStaticDestination<CrewWikiRoute.AdminDashboard>("관리자 대시보드", "관리자 플로우를 별도 그래프로 분리할 후보 목적지입니다.")
        addStaticDestination<CrewWikiRoute.AdminDocuments>("문서 관리", "관리자 플로우를 별도 그래프로 분리할 후보 목적지입니다.")
    }
}

// ── Popular ───────────────────────────────────────────────────────────────────

private fun NavGraphBuilder.addPopularDestination(navController: NavController) {
    composable<CrewWikiRoute.Popular> {
        val vm = viewModel<PopularDocumentsViewModel>(
            factory = ViewModelProvider.Factory {
                PopularDocumentsViewModel(AppContainer.documentRepository)
            },
        )
        val uiState by vm.uiState.collectAsState()

        when (val state = uiState) {
            is PopularUiState.Loading -> LoadingScreen()
            is PopularUiState.Error -> ErrorScreen(
                message = state.message,
                onRetry = vm::loadPopularDocuments,
            )
            is PopularUiState.Success -> PopularDocumentsScreen(
                documentsByViews = state.documentsByViews,
                documentsByEdits = state.documentsByEdits,
                onDocumentClick = { doc ->
                    navController.navigate(CrewWikiRoute.Document(doc.documentUUID))
                },
            )
        }
    }
}

// ── Document ──────────────────────────────────────────────────────────────────

private fun NavGraphBuilder.addDocumentDestinations(navController: NavController) {
    // 문서 상세
    composable<CrewWikiRoute.Document> { backStackEntry ->
        val route = backStackEntry.toRoute<CrewWikiRoute.Document>()
        val vm = viewModel<DocumentDetailViewModel>(
            key = route.documentId,
            factory = ViewModelProvider.Factory {
                DocumentDetailViewModel(AppContainer.documentRepository, route.documentId)
            },
        )
        val uiState by vm.uiState.collectAsState()

        when (val state = uiState) {
            is DocumentDetailUiState.Loading -> LoadingScreen()
            is DocumentDetailUiState.NotFound -> PlaceholderScreen(
                title = "문서를 찾을 수 없습니다",
                route = "wiki/${route.documentId}",
                description = "해당 UUID의 문서가 존재하지 않습니다.",
            )
            is DocumentDetailUiState.Error -> ErrorScreen(
                message = state.message,
                onRetry = vm::loadDocument,
            )
            is DocumentDetailUiState.Success -> DocumentDetailScreen(
                documentDetail = state.detail,
                onEditClick = { navController.navigate(CrewWikiRoute.DocumentEdit(route.documentId)) },
                onLogsClick = { navController.navigate(CrewWikiRoute.DocumentLogs(route.documentId)) },
                onWriteClick = { navController.navigate(CrewWikiRoute.Post) },
            )
        }
    }

    // 문서 수정 (Placeholder)
    composable<CrewWikiRoute.DocumentEdit> { backStackEntry ->
        val route = backStackEntry.toRoute<CrewWikiRoute.DocumentEdit>()
        PlaceholderScreen(
            title = "문서 수정",
            route = "wiki/${route.documentId}/edit",
            description = "마크다운 에디터 화면입니다. 웹 TuiEditor에 대응합니다.",
        )
    }

    // 편집 기록 목록
    composable<CrewWikiRoute.DocumentLogs> { backStackEntry ->
        val route = backStackEntry.toRoute<CrewWikiRoute.DocumentLogs>()
        val vm = viewModel<DocumentLogsViewModel>(
            key = route.documentId,
            factory = ViewModelProvider.Factory {
                DocumentLogsViewModel(AppContainer.documentRepository, route.documentId)
            },
        )
        DocumentLogsScreen(
            viewModel = vm,
            onLogClick = { logId ->
                navController.navigate(CrewWikiRoute.DocumentLog(route.documentId, logId.toInt()))
            },
        )
    }

    // 편집 기록 상세
    composable<CrewWikiRoute.DocumentLog> { backStackEntry ->
        val route = backStackEntry.toRoute<CrewWikiRoute.DocumentLog>()
        val vm = viewModel<DocumentLogDetailViewModel>(
            key = route.logId.toString(),
            factory = ViewModelProvider.Factory {
                DocumentLogDetailViewModel(AppContainer.documentRepository, route.logId.toLong())
            },
        )
        DocumentLogDetailScreen(viewModel = vm)
    }
}

// ── Group ─────────────────────────────────────────────────────────────────────

private fun NavGraphBuilder.addGroupDestinations(navController: NavController) {
    // 그룹 상세
    composable<CrewWikiRoute.GroupDetail> { backStackEntry ->
        val route = backStackEntry.toRoute<CrewWikiRoute.GroupDetail>()
        val vm = viewModel<GroupDetailViewModel>(
            key = route.groupId,
            factory = ViewModelProvider.Factory {
                GroupDetailViewModel(AppContainer.groupDocumentRepository, route.groupId)
            },
        )
        GroupDetailScreen(
            viewModel = vm,
            onCrewDocumentClick = { uuid ->
                navController.navigate(CrewWikiRoute.Document(uuid))
            },
            onLogsClick = {
                navController.navigate(CrewWikiRoute.GroupLogs(route.groupId))
            },
        )
    }

    // 그룹 수정 (Placeholder)
    composable<CrewWikiRoute.GroupEdit> { backStackEntry ->
        val route = backStackEntry.toRoute<CrewWikiRoute.GroupEdit>()
        PlaceholderScreen(
            title = "그룹 수정",
            route = "wiki/groups/${route.groupId}/edit",
            description = "그룹 문서 편집 화면입니다.",
        )
    }

    // 그룹 편집 기록 목록
    composable<CrewWikiRoute.GroupLogs> { backStackEntry ->
        val route = backStackEntry.toRoute<CrewWikiRoute.GroupLogs>()
        val vm = viewModel<DocumentLogsViewModel>(
            key = "group-logs-${route.groupId}",
            factory = ViewModelProvider.Factory {
                DocumentLogsViewModel(AppContainer.documentRepository, route.groupId)
            },
        )
        DocumentLogsScreen(
            viewModel = vm,
            onLogClick = { logId ->
                navController.navigate(CrewWikiRoute.GroupLog(route.groupId, logId.toInt()))
            },
        )
    }

    // 그룹 편집 기록 상세
    composable<CrewWikiRoute.GroupLog> { backStackEntry ->
        val route = backStackEntry.toRoute<CrewWikiRoute.GroupLog>()
        val vm = viewModel<DocumentLogDetailViewModel>(
            key = "group-log-${route.logId}",
            factory = ViewModelProvider.Factory {
                DocumentLogDetailViewModel(AppContainer.documentRepository, route.logId.toLong())
            },
        )
        DocumentLogDetailScreen(viewModel = vm)
    }
}

// ── Home ──────────────────────────────────────────────────────────────────────

private fun NavGraphBuilder.addHomeDestination(navController: NavController) {
    composable<CrewWikiRoute.Home> {
        val sampleDocumentId = "sample-document"
        val sampleGroupId = "sample-group"

        val quickLinks = listOf(
            "인기 문서" to { navController.navigate(CrewWikiRoute.Popular) },
            "통계" to { navController.navigate(CrewWikiRoute.Statistics) },
            "문서 작성" to { navController.navigate(CrewWikiRoute.Post) },
            "관리자 로그인" to { navController.navigate(CrewWikiRoute.AdminLogin) },
        )

        val featureLinks = listOf(
            "문서 상세" to { navController.navigate(CrewWikiRoute.Document(sampleDocumentId)) },
            "문서 수정" to { navController.navigate(CrewWikiRoute.DocumentEdit(sampleDocumentId)) },
            "문서 로그" to { navController.navigate(CrewWikiRoute.DocumentLogs(sampleDocumentId)) },
            "문서 로그 상세" to { navController.navigate(CrewWikiRoute.DocumentLog(sampleDocumentId, 1)) },
            "그룹 상세" to { navController.navigate(CrewWikiRoute.GroupDetail(sampleGroupId)) },
            "그룹 수정" to { navController.navigate(CrewWikiRoute.GroupEdit(sampleGroupId)) },
            "그룹 로그" to { navController.navigate(CrewWikiRoute.GroupLogs(sampleGroupId)) },
            "그룹 로그 상세" to { navController.navigate(CrewWikiRoute.GroupLog(sampleGroupId, 1)) },
            "관리자 대시보드" to { navController.navigate(CrewWikiRoute.AdminDashboard) },
            "문서 관리" to { navController.navigate(CrewWikiRoute.AdminDocuments) },
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "크루위키 라우팅 구조",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = "웹 기준의 주요 흐름을 KMP 공용 NavHost로 먼저 정리했습니다.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = CrewWikiDesignTokens.colors.grayscale.c600,
                    )
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    quickLinks.forEach { (label, onClick) ->
                        RouteChip(label = label, onClick = onClick)
                    }
                }
            }
            items(featureLinks) { (label, onClick) ->
                RouteCard(title = label, onClick = onClick)
            }
        }
    }
}

// ── 공통 컴포넌트 ──────────────────────────────────────────────────────────────

private inline fun <reified T : Any> NavGraphBuilder.addStaticDestination(
    title: String,
    description: String,
) {
    composable<T> {
        PlaceholderScreen(
            title = title,
            route = routeName<T>(),
            description = description,
        )
    }
}

@Composable
private fun RouteCard(title: String, onClick: () -> Unit) {
    val colors = CrewWikiDesignTokens.colors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, colors.primary.c100),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
            Text(text = "이 목적지로 이동", style = MaterialTheme.typography.bodyMedium, color = colors.grayscale.c500)
        }
    }
}

@Composable
private fun RouteChip(label: String, onClick: () -> Unit) {
    val colors = CrewWikiDesignTokens.colors
    Card(
        modifier = Modifier
            .wrapContentHeight()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = colors.primary.c50),
        shape = MaterialTheme.shapes.medium,
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            style = MaterialTheme.typography.labelLarge,
            color = colors.primary.c800,
        )
    }
}

@Composable
private fun PlaceholderScreen(title: String, route: String, description: String) {
    val colors = CrewWikiDesignTokens.colors
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = title, style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onBackground)
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = MaterialTheme.shapes.large,
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Route",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary.base,
                    )
                    Text(
                        text = route,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.grayscale.c600,
                    )
                }
                Text(text = description, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

private inline fun <reified T : Any> routeName(): String = T::class.simpleName ?: "route"
