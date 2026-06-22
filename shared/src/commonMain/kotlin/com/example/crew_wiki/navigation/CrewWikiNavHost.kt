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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.crew_wiki.CrewWikiDesignTokens
import com.example.crew_wiki.ui.document.DocumentDetailScreen

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
        addStaticDestination<CrewWikiRoute.Popular>("인기 문서", "웹 화면을 대응시키기 위한 정적 목적지입니다.")
        addStaticDestination<CrewWikiRoute.Statistics>("통계", "웹 화면을 대응시키기 위한 정적 목적지입니다.")
        addStaticDestination<CrewWikiRoute.Post>("문서 작성", "웹 화면을 대응시키기 위한 정적 목적지입니다.")
        addStaticDestination<CrewWikiRoute.AdminLogin>("관리자 로그인", "관리자 플로우를 별도 그래프로 분리할 후보 목적지입니다.")
        addStaticDestination<CrewWikiRoute.AdminDashboard>("관리자 대시보드", "관리자 플로우를 별도 그래프로 분리할 후보 목적지입니다.")
        addStaticDestination<CrewWikiRoute.AdminDocuments>("문서 관리", "관리자 플로우를 별도 그래프로 분리할 후보 목적지입니다.")
        addDocumentDestinations()
        addGroupDestinations()
    }
}

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
                RouteCard(
                    title = label,
                    onClick = onClick,
                )
            }
        }
    }
}

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

private fun NavGraphBuilder.addDocumentDestinations() {
    composable<CrewWikiRoute.Document> { backStackEntry ->
        val route = backStackEntry.toRoute<CrewWikiRoute.Document>()
        DocumentDetailScreen(documentId = route.documentId)
    }
    composable<CrewWikiRoute.DocumentEdit> { backStackEntry ->
        val route = backStackEntry.toRoute<CrewWikiRoute.DocumentEdit>()
        PlaceholderScreen(
            title = "문서 수정",
            route = "wiki/document/${route.documentId}/edit",
            description = "조회 화면과 같은 식별자를 재사용하는 편이 자연스럽습니다.",
        )
    }
    composable<CrewWikiRoute.DocumentLogs> { backStackEntry ->
        val route = backStackEntry.toRoute<CrewWikiRoute.DocumentLogs>()
        PlaceholderScreen(
            title = "문서 로그",
            route = "wiki/document/${route.documentId}/logs",
            description = "로그 목록은 문서 상세의 하위 흐름으로 두는 구조입니다.",
        )
    }
    composable<CrewWikiRoute.DocumentLog> { backStackEntry ->
        val route = backStackEntry.toRoute<CrewWikiRoute.DocumentLog>()
        PlaceholderScreen(
            title = "문서 로그 상세",
            route = "wiki/document/${route.documentId}/log/${route.logId}",
            description = "문서 ID와 로그 ID를 함께 넘기는 상세 로그 목적지입니다.",
        )
    }
}

private fun NavGraphBuilder.addGroupDestinations() {
    composable<CrewWikiRoute.GroupDetail> { backStackEntry ->
        val route = backStackEntry.toRoute<CrewWikiRoute.GroupDetail>()
        PlaceholderScreen(
            title = "그룹 상세",
            route = "wiki/group/${route.groupId}",
            description = "웹의 그룹 타임라인 화면에 대응하는 목적지입니다.",
        )
    }
    composable<CrewWikiRoute.GroupEdit> { backStackEntry ->
        val route = backStackEntry.toRoute<CrewWikiRoute.GroupEdit>()
        PlaceholderScreen(
            title = "그룹 수정",
            route = "wiki/group/${route.groupId}/edit",
            description = "그룹 편집은 그룹 상세와 같은 식별자를 공유합니다.",
        )
    }
    composable<CrewWikiRoute.GroupLogs> { backStackEntry ->
        val route = backStackEntry.toRoute<CrewWikiRoute.GroupLogs>()
        PlaceholderScreen(
            title = "그룹 로그",
            route = "wiki/group/${route.groupId}/logs",
            description = "그룹 로그 목록 목적지입니다.",
        )
    }
    composable<CrewWikiRoute.GroupLog> { backStackEntry ->
        val route = backStackEntry.toRoute<CrewWikiRoute.GroupLog>()
        PlaceholderScreen(
            title = "그룹 로그 상세",
            route = "wiki/group/${route.groupId}/log/${route.logId}",
            description = "그룹 로그 상세 목적지입니다.",
        )
    }
}

@Composable
private fun RouteCard(
    title: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, CrewWikiDesignTokens.colors.primary.c100),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "이 목적지로 이동",
                style = MaterialTheme.typography.bodyMedium,
                color = CrewWikiDesignTokens.colors.grayscale.c500,
            )
        }
    }
}

@Composable
private fun RouteChip(
    label: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .wrapContentHeight()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = CrewWikiDesignTokens.colors.primary.c50,
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            style = MaterialTheme.typography.labelLarge,
            color = CrewWikiDesignTokens.colors.primary.c800,
        )
    }
}

@Composable
private fun PlaceholderScreen(
    title: String,
    route: String,
    description: String,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
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
                        color = CrewWikiDesignTokens.colors.primary.base,
                    )
                    Text(
                        text = route,
                        style = MaterialTheme.typography.bodyMedium,
                        color = CrewWikiDesignTokens.colors.grayscale.c600,
                    )
                }
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

private inline fun <reified T : Any> routeName(): String = T::class.simpleName ?: "route"
