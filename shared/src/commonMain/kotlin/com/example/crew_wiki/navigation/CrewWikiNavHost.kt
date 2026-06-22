package com.example.crew_wiki.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.crew_wiki.CrewWikiDesignTokens
import com.example.crew_wiki.data.history.RecentlyViewedStore
import com.example.crew_wiki.di.AppContainer
import com.example.crew_wiki.ui.common.CrewWikiTopBar
import com.example.crew_wiki.ui.common.ErrorScreen
import com.example.crew_wiki.ui.common.EyeNavIcon
import com.example.crew_wiki.ui.common.HistoryNavIcon
import com.example.crew_wiki.ui.common.HomeNavIcon
import com.example.crew_wiki.ui.common.LoadingScreen
import com.example.crew_wiki.ui.common.SettingsNavIcon
import com.example.crew_wiki.ui.document.DocumentDetailScreen
import com.example.crew_wiki.ui.document.DocumentEditorMode
import com.example.crew_wiki.ui.document.DocumentEditorScreen
import com.example.crew_wiki.ui.document.DocumentEditorViewModel
import com.example.crew_wiki.ui.document.DocumentDetailUiState
import com.example.crew_wiki.ui.document.DocumentDetailViewModel
import com.example.crew_wiki.ui.document.DocumentLogDetailScreen
import com.example.crew_wiki.ui.document.DocumentLogDetailViewModel
import com.example.crew_wiki.ui.document.DocumentLogsScreen
import com.example.crew_wiki.ui.document.DocumentLogsViewModel
import com.example.crew_wiki.ui.group.GroupDetailScreen
import com.example.crew_wiki.ui.group.GroupDetailUiState
import com.example.crew_wiki.ui.group.GroupDetailViewModel
import com.example.crew_wiki.ui.history.RecentEditsScreen
import com.example.crew_wiki.ui.history.RecentEditsUiState
import com.example.crew_wiki.ui.history.RecentEditsViewModel
import com.example.crew_wiki.ui.history.RecentlyViewedScreen
import com.example.crew_wiki.ui.home.HomeScreen
import com.example.crew_wiki.ui.home.HomeUiState
import com.example.crew_wiki.ui.home.HomeViewModel
import com.example.crew_wiki.ui.popular.PopularDocumentsScreen
import com.example.crew_wiki.ui.popular.PopularDocumentsViewModel
import com.example.crew_wiki.ui.popular.PopularUiState
import com.example.crew_wiki.ui.search.SearchScreen
import com.example.crew_wiki.ui.search.SearchViewModel
import com.example.crew_wiki.ui.settings.SettingsScreen
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

private data class BottomNavTab(
    val route: Any,
    val label: String,
    val routeMatcher: String,
    val icon: @Composable (androidx.compose.ui.graphics.Color) -> Unit,
)

@Composable
fun CrewWikiNavRoot() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val coroutineScope = rememberCoroutineScope()
    var shuffleLoading by remember { mutableStateOf(false) }
    val colors = CrewWikiDesignTokens.colors
    val defaultUriHandler = LocalUriHandler.current
    val internalUriHandler = remember(navController, defaultUriHandler) {
        object : UriHandler {
            override fun openUri(uri: String) {
                if (!navController.navigateCrewWikiLink(uri)) {
                    defaultUriHandler.openUri(uri)
                }
            }
        }
    }

    val currentRoute = backStackEntry?.destination?.route ?: ""
    val bottomTabs = remember {
        listOf(
            BottomNavTab(CrewWikiRoute.Home, "홈", "Home") { tint -> HomeNavIcon(tint, Modifier.size(22.dp)) },
            BottomNavTab(CrewWikiRoute.RecentEdits, "최근 편집", "RecentEdits") { tint -> HistoryNavIcon(tint, Modifier.size(22.dp)) },
            BottomNavTab(CrewWikiRoute.RecentlyViewed, "최근 확인", "RecentlyViewed") { tint -> EyeNavIcon(tint, Modifier.size(22.dp)) },
            BottomNavTab(CrewWikiRoute.Settings, "설정", "Settings") { tint -> SettingsNavIcon(tint, Modifier.size(22.dp)) },
        )
    }
    // 하단 탭 화면에서는 뒤로가기 버튼 대신 탭 자체를 보여주고, 그 외 화면(문서 상세 등)에서만 뒤로가기 표시
    val isTopLevelTab = bottomTabs.any { currentRoute.contains(it.routeMatcher) } || currentRoute.isEmpty()

    CompositionLocalProvider(LocalUriHandler provides internalUriHandler) {
        Scaffold(
            topBar = {
                CrewWikiTopBar(
                    showBack = !isTopLevelTab,
                    onBack = { navController.popBackStack() },
                    onHomeClick = {
                        navController.navigate(CrewWikiRoute.Home) {
                            popUpTo(CrewWikiRoute.Home) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onShuffle = {
                        if (!shuffleLoading) {
                            coroutineScope.launch {
                                shuffleLoading = true
                                try {
                                    val randomDoc = AppContainer.documentApiService.getRandomDocument()
                                    navController.navigate(CrewWikiRoute.Document(randomDoc.documentUUID))
                                } catch (_: Exception) {
                                    // 실패 시 무시
                                } finally {
                                    shuffleLoading = false
                                }
                            }
                        }
                    },
                    shuffleLoading = shuffleLoading,
                    onSearch = { navController.navigate(CrewWikiRoute.Search) },
                )
            },
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    bottomTabs.forEach { tab ->
                        val selected = currentRoute.contains(tab.routeMatcher)
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(CrewWikiRoute.Home) { inclusive = false }
                                    launchSingleTop = true
                                }
                            },
                            icon = { tab.icon(if (selected) colors.primary.base else colors.grayscale.c500) },
                            label = { Text(tab.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedTextColor = colors.primary.base,
                                unselectedTextColor = colors.grayscale.c500,
                                indicatorColor = colors.primary.c50,
                            ),
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxSize(),
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = CrewWikiRoute.Home,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                addHomeDestination(navController)
                addPopularDestination(navController)
                addDocumentDestinations(navController)
                addGroupDestinations(navController)
                addSearchDestination(navController)
                addRecentEditsDestination(navController)
                addRecentlyViewedDestination(navController)
                addSettingsDestination()
            }
        }
    }
}

private fun NavController.navigateCrewWikiLink(uri: String): Boolean {
    val path = uri.toCrewWikiPath() ?: return false

    return when {
        path == "/wiki/post" -> {
            navigate(CrewWikiRoute.Post) { launchSingleTop = true }
            true
        }

        groupLogPathRegex.matches(path) -> {
            val match = groupLogPathRegex.matchEntire(path) ?: return false
            val groupId = match.groupValues[1]
            val logId = match.groupValues[2].toIntOrNull() ?: return false
            navigate(CrewWikiRoute.GroupLog(groupId, logId)) { launchSingleTop = true }
            true
        }

        groupLogsPathRegex.matches(path) -> {
            val groupId = groupLogsPathRegex.matchEntire(path)?.groupValues?.get(1) ?: return false
            navigate(CrewWikiRoute.GroupLogs(groupId)) { launchSingleTop = true }
            true
        }

        groupEditPathRegex.matches(path) -> {
            val groupId = groupEditPathRegex.matchEntire(path)?.groupValues?.get(1) ?: return false
            navigate(CrewWikiRoute.GroupEdit(groupId)) { launchSingleTop = true }
            true
        }

        groupPathRegex.matches(path) -> {
            val groupId = groupPathRegex.matchEntire(path)?.groupValues?.get(1) ?: return false
            navigate(CrewWikiRoute.GroupDetail(groupId)) { launchSingleTop = true }
            true
        }

        documentLogPathRegex.matches(path) -> {
            val match = documentLogPathRegex.matchEntire(path) ?: return false
            val documentId = match.groupValues[1]
            val logId = match.groupValues[2].toIntOrNull() ?: return false
            navigate(CrewWikiRoute.DocumentLog(documentId, logId)) { launchSingleTop = true }
            true
        }

        documentLogsPathRegex.matches(path) -> {
            val documentId = documentLogsPathRegex.matchEntire(path)?.groupValues?.get(1) ?: return false
            navigate(CrewWikiRoute.DocumentLogs(documentId)) { launchSingleTop = true }
            true
        }

        documentEditPathRegex.matches(path) -> {
            val documentId = documentEditPathRegex.matchEntire(path)?.groupValues?.get(1) ?: return false
            navigate(CrewWikiRoute.DocumentEdit(documentId)) { launchSingleTop = true }
            true
        }

        documentPathRegex.matches(path) -> {
            val documentId = documentPathRegex.matchEntire(path)?.groupValues?.get(1) ?: return false
            navigate(CrewWikiRoute.Document(documentId)) { launchSingleTop = true }
            true
        }

        else -> false
    }
}

private fun String.toCrewWikiPath(): String? {
    val normalized = substringBefore('#').substringBefore('?')
    return when {
        normalized.startsWith("/wiki") -> normalized
        normalized.startsWith("https://crew-wiki.site/wiki") -> normalized.removePrefix("https://crew-wiki.site")
        normalized.startsWith("http://crew-wiki.site/wiki") -> normalized.removePrefix("http://crew-wiki.site")
        normalized.startsWith("https://www.crew-wiki.site/wiki") -> normalized.removePrefix("https://www.crew-wiki.site")
        normalized.startsWith("http://www.crew-wiki.site/wiki") -> normalized.removePrefix("http://www.crew-wiki.site")
        else -> null
    }
}

private val documentPathRegex = Regex("^/wiki/([A-Za-z0-9-]+)$")
private val documentEditPathRegex = Regex("^/wiki/([A-Za-z0-9-]+)/edit$")
private val documentLogsPathRegex = Regex("^/wiki/([A-Za-z0-9-]+)/logs$")
private val documentLogPathRegex = Regex("^/wiki/([A-Za-z0-9-]+)/log/(\\d+)$")
private val groupPathRegex = Regex("^/wiki/groups/([A-Za-z0-9-]+)$")
private val groupEditPathRegex = Regex("^/wiki/groups/([A-Za-z0-9-]+)/edit$")
private val groupLogsPathRegex = Regex("^/wiki/groups/([A-Za-z0-9-]+)/logs$")
private val groupLogPathRegex = Regex("^/wiki/groups/([A-Za-z0-9-]+)/log/(\\d+)$")

// ── Home ──────────────────────────────────────────────────────────────────────

private fun NavGraphBuilder.addHomeDestination(navController: NavController) {
    composable<CrewWikiRoute.Home> {
        val vm = viewModel<HomeViewModel>(
            factory = vmFactory { HomeViewModel(AppContainer.documentRepository) },
        )
        val uiState by vm.uiState.collectAsState()

        when (val state = uiState) {
            is HomeUiState.Loading -> LoadingScreen()
            is HomeUiState.Error -> ErrorScreen(
                message = state.message,
                onRetry = vm::loadMainDocument,
            )
            is HomeUiState.Success -> HomeScreen(
                mainDocument = state.mainDocument,
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

    // 문서 수정
    composable<CrewWikiRoute.DocumentEdit> { backStackEntry ->
        val route = backStackEntry.toRoute<CrewWikiRoute.DocumentEdit>()
        val vm = viewModel<DocumentEditorViewModel>(
            key = "edit-${route.documentId}",
            factory = vmFactory {
                DocumentEditorViewModel(
                    AppContainer.documentApiService,
                    AppContainer.groupApiService,
                )
            },
        )
        DocumentEditorScreen(
            viewModel = vm,
            mode = DocumentEditorMode.Edit(route.documentId),
            onBackClick = { navController.popBackStack() },
            onSaved = { documentId ->
                navController.navigate(CrewWikiRoute.Document(documentId)) {
                    popUpTo(CrewWikiRoute.DocumentEdit(route.documentId)) { inclusive = true }
                }
            },
        )
    }

    // 문서 작성
    composable<CrewWikiRoute.Post> {
        val vm = viewModel<DocumentEditorViewModel>(
            key = "post",
            factory = vmFactory {
                DocumentEditorViewModel(
                    AppContainer.documentApiService,
                    AppContainer.groupApiService,
                )
            },
        )
        DocumentEditorScreen(
            viewModel = vm,
            mode = DocumentEditorMode.Post,
            onBackClick = { navController.popBackStack() },
            onSaved = { documentId ->
                navController.navigate(CrewWikiRoute.Document(documentId)) {
                    popUpTo(CrewWikiRoute.Post) { inclusive = true }
                }
            },
        )
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

// ── Search ────────────────────────────────────────────────────────────────────

private fun NavGraphBuilder.addSearchDestination(navController: NavController) {
    composable<CrewWikiRoute.Search> {
        val vm = viewModel<SearchViewModel>(
            factory = vmFactory { SearchViewModel(AppContainer.documentApiService) },
        )
        SearchScreen(
            viewModel = vm,
            onResultClick = { uuid, documentType ->
                if (documentType == "ORGANIZATION") {
                    navController.navigate(CrewWikiRoute.GroupDetail(uuid))
                } else {
                    navController.navigate(CrewWikiRoute.Document(uuid))
                }
            },
        )
    }
}

// ── 최근 편집 ─────────────────────────────────────────────────────────────────

private fun NavGraphBuilder.addRecentEditsDestination(navController: NavController) {
    composable<CrewWikiRoute.RecentEdits> {
        val vm = viewModel<RecentEditsViewModel>(
            factory = vmFactory { RecentEditsViewModel(AppContainer.documentApiService) },
        )
        val uiState by vm.uiState.collectAsState()

        when (val state = uiState) {
            is RecentEditsUiState.Loading -> LoadingScreen()
            is RecentEditsUiState.Error -> ErrorScreen(
                message = state.message,
                onRetry = vm::loadRecentEdits,
            )
            is RecentEditsUiState.Success -> RecentEditsScreen(
                documents = state.documents,
                onDocumentClick = { doc ->
                    if (doc.documentType == "ORGANIZATION") {
                        navController.navigate(CrewWikiRoute.GroupDetail(doc.uuid))
                    } else {
                        navController.navigate(CrewWikiRoute.Document(doc.uuid))
                    }
                },
            )
        }
    }
}

// ── 내가 최근에 확인한 문서 ─────────────────────────────────────────────────────

private fun NavGraphBuilder.addRecentlyViewedDestination(navController: NavController) {
    composable<CrewWikiRoute.RecentlyViewed> {
        val documents by RecentlyViewedStore.viewedDocuments.collectAsState()
        RecentlyViewedScreen(
            documents = documents,
            onDocumentClick = { doc ->
                if (doc.documentType == "ORGANIZATION") {
                    navController.navigate(CrewWikiRoute.GroupDetail(doc.uuid))
                } else {
                    navController.navigate(CrewWikiRoute.Document(doc.uuid))
                }
            },
        )
    }
}

// ── 설정 ──────────────────────────────────────────────────────────────────────

private fun NavGraphBuilder.addSettingsDestination() {
    composable<CrewWikiRoute.Settings> {
        SettingsScreen()
    }
}

// ── 헬퍼 ──────────────────────────────────────────────────────────────────────

private fun <VM : ViewModel> vmFactory(create: () -> VM): ViewModelProvider.Factory =
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T =
            create() as T
    }
