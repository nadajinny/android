package com.example.crew_wiki.navigation

import kotlinx.serialization.Serializable

object CrewWikiRoute {
    @Serializable
    data object Home

    @Serializable
    data object Popular

    @Serializable
    data object Statistics

    @Serializable
    data object Post

    @Serializable
    data class Document(val documentId: String)

    @Serializable
    data class DocumentEdit(val documentId: String)

    @Serializable
    data class DocumentLogs(val documentId: String)

    @Serializable
    data class DocumentLog(
        val documentId: String,
        val logId: Int,
    )

    @Serializable
    data class GroupDetail(val groupId: String)

    @Serializable
    data class GroupEdit(val groupId: String)

    @Serializable
    data class GroupLogs(val groupId: String)

    @Serializable
    data class GroupLog(
        val groupId: String,
        val logId: Int,
    )

    @Serializable
    data object RecentEdits

    @Serializable
    data object RecentlyViewed

    @Serializable
    data object Settings

    @Serializable
    data object Search

    @Serializable
    data object AdminLogin

    @Serializable
    data object AdminDashboard

    @Serializable
    data object AdminDocuments
}
