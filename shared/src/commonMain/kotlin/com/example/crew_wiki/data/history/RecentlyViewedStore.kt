package com.example.crew_wiki.data.history

import com.example.crew_wiki.model.RecentDocument
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 앱 세션 동안 사용자가 확인한 문서를 최신순으로 기록한다.
 * 앱 재시작 시 초기화되는 메모리 저장소.
 */
object RecentlyViewedStore {
    private const val MAX_SIZE = 50

    private val _viewedDocuments = MutableStateFlow<List<RecentDocument>>(emptyList())
    val viewedDocuments: StateFlow<List<RecentDocument>> = _viewedDocuments.asStateFlow()

    fun record(document: RecentDocument) {
        _viewedDocuments.value = listOf(document) +
            _viewedDocuments.value.filterNot { it.uuid == document.uuid }
                .take(MAX_SIZE - 1)
    }
}
