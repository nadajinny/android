package com.example.crew_wiki.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
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
import com.example.crew_wiki.ui.group.GroupDetailUiState
import com.example.crew_wiki.ui.group.GroupDetailViewModel
import com.example.crew_wiki.ui.home.HomeScreen
import com.example.crew_wiki.ui.home.HomeUiState
import com.example.crew_wiki.ui.home.HomeViewModel
import com.example.crew_wiki.ui.popular.PopularDocumentsScreen
import com.example.crew_wiki.ui.popular.PopularDocumentsViewModel
import com.example.crew_wiki.ui.popular.PopularUiState
import kotlin.reflect.KClass

@Composable
fun CrewWikiNavRoot() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = CrewWikiRoute.Home,
        modifier = androidx.compose.ui.Modifier
            .background(MaterialTheme.colorScheme.background)
            .safeContentPadding()
            .fillMaxSize(),
    ) {
        addHomeDestination(navController)
        addPopularDestination(navController)
        addDocumentDestinations(navController)
        addGroupDestinations(navController)
    }
}

// ── Home ──────────────────────────────────────────────────────────────────────

private fun NavGraphBuilder.addHomeDestination(navController: NavController) {
    composable<CrewWikiRoute.Home> {
        val vm = viewModel<HomeViewModel>(
            factory = vmFactory { HomeViewModel(AppContainer.documentApiService) },
        )
        val uiState by vm.uiState.collectAsState()

        when (val state = uiState) {
            is HomeUiState.Loading -> LoadingScreen()
            is HomeUiState.Error -> ErrorScreen(
                message = state.message,
                onRetry = vm::loadRecentDocuments,
            )
            is HomeUiState.Success -> HomeScreen(
                recentDocuments = state.recentDocuments,
                onDocumentClick = { doc ->
                    if (doc.documentType == "ORGANIZATION") {
                        navController.navigate(CrewWikiRoute.GroupDetail(doc.uuid))
                    } else {
                        navController.navigate(CrewWikiRoute.Document(doc.uuid))
                    }
                },
                onPopularClick = { navController.navigate(CrewWikiRoute.Popular) },
            )
        }
    }
}

// ── Popular ───────────────────────────────────────────────────────────────────

private fun NavGraphBuilder.addPopularDestination(navController: NavController) {
    composable<CrewWikiRoute.Popular> {
        val vm = viewModel<PopularDocumentsViewModel>(
            factory = vmFactory { PopularDocumentsViewModel(AppContainer.documentRepository) },
        )
        val uiState by vm.uiState.collectAsState()

        when (val state = uiState) {
            is PopularUiState.Loading -> LoadingScreen()
            is PopularUiState.Error -> ErrorScreen(
                message = state.message,
                onRetry = vm::loadPopularDocuments,
            )
            is PopularUiState.Success -> PopularDocumentsScreen(
                documents = state.documents,
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
            factory = vmFactory {
                DocumentDetailViewModel(AppContainer.documentRepository, route.documentId)
            },
        )
        val uiState by vm.uiState.collectAsState()

        when (val state = uiState) {
            is DocumentDetailUiState.Loading -> LoadingScreen()
            is DocumentDetailUiState.NotFound -> ErrorScreen(
                message = "문서를 찾을 수 없습니다.",
                onRetry = vm::loadDocument,
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
                onOrganizationClick = { uuid ->
                    navController.navigate(CrewWikiRoute.GroupDetail(uuid))
                },
            )
        }
    }

    // 편집 기록 목록
    composable<CrewWikiRoute.DocumentLogs> { backStackEntry ->
        val route = backStackEntry.toRoute<CrewWikiRoute.DocumentLogs>()
        val vm = viewModel<DocumentLogsViewModel>(
            key = route.documentId,
            factory = vmFactory {
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
            factory = vmFactory {
                DocumentLogDetailViewModel(AppContainer.documentRepository, route.logId.toLong())
            },
        )
        DocumentLogDetailScreen(viewModel = vm)
    }

    // 문서 수정 (placeholder)
    composable<CrewWikiRoute.DocumentEdit> {
        // TODO: 에디터 화면
        LoadingScreen()
    }

    // 문서 작성 (placeholder)
    composable<CrewWikiRoute.Post> {
        LoadingScreen()
    }
}

// ── Group ─────────────────────────────────────────────────────────────────────

private fun NavGraphBuilder.addGroupDestinations(navController: NavController) {
    composable<CrewWikiRoute.GroupDetail> { backStackEntry ->
        val route = backStackEntry.toRoute<CrewWikiRoute.GroupDetail>()
        val vm = viewModel<GroupDetailViewModel>(
            key = route.groupId,
            factory = vmFactory {
                GroupDetailViewModel(AppContainer.groupDocumentRepository, route.groupId)
            },
        )
        val uiState by vm.uiState.collectAsState()

        when (val state = uiState) {
            is GroupDetailUiState.Loading -> LoadingScreen()
            is GroupDetailUiState.Error -> ErrorScreen(
                message = state.message,
                onRetry = vm::loadGroupDocument,
            )
            is GroupDetailUiState.Success -> GroupDetailScreen(
                detail = state.detail,
                onCrewDocumentClick = { uuid ->
                    navController.navigate(CrewWikiRoute.Document(uuid))
                },
                onLogsClick = {
                    navController.navigate(CrewWikiRoute.GroupLogs(route.groupId))
                },
            )
        }
    }

    composable<CrewWikiRoute.GroupLogs> { backStackEntry ->
        val route = backStackEntry.toRoute<CrewWikiRoute.GroupLogs>()
        val vm = viewModel<DocumentLogsViewModel>(
            key = "group-${route.groupId}",
            factory = vmFactory {
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

    composable<CrewWikiRoute.GroupLog> { backStackEntry ->
        val route = backStackEntry.toRoute<CrewWikiRoute.GroupLog>()
        val vm = viewModel<DocumentLogDetailViewModel>(
            key = "group-log-${route.logId}",
            factory = vmFactory {
                DocumentLogDetailViewModel(AppContainer.documentRepository, route.logId.toLong())
            },
        )
        DocumentLogDetailScreen(viewModel = vm)
    }

    composable<CrewWikiRoute.GroupEdit> {
        LoadingScreen()
    }
}

// ── 헬퍼 ──────────────────────────────────────────────────────────────────────

private fun <VM : ViewModel> vmFactory(create: () -> VM): ViewModelProvider.Factory =
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T =
            create() as T
    }
