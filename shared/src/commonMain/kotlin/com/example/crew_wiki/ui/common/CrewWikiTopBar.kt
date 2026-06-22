package com.example.crew_wiki.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crew_wiki.CrewWikiDesignTokens
import crewwiki.shared.generated.resources.Res
import crewwiki.shared.generated.resources.crew_wiki_icon
import org.jetbrains.compose.resources.painterResource

/**
 * crew-wiki-next WikiHeader를 Android로 포팅한 상단 앱바.
 *
 * - 배경: primary.base (teal #25B4B9)
 * - 좌측: [뒤로가기] (서브 화면) | 로고 박스 + "크루위키" 텍스트
 * - 우측: 셔플 아이콘 + 검색 아이콘
 */
@Composable
fun CrewWikiTopBar(
    showBack: Boolean,
    onBack: () -> Unit,
    onHomeClick: () -> Unit,
    onShuffle: () -> Unit,
    shuffleLoading: Boolean,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = CrewWikiDesignTokens.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.primary.base)
            .windowInsetsPadding(WindowInsets.statusBars)
            .height(56.dp)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // ── 좌측 ──
        if (showBack) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = ArrowBackIcon,
                    contentDescription = "뒤로가기",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp),
                )
            }
        } else {
            Spacer(modifier = Modifier.width(8.dp))
        }

        Row(
            modifier = Modifier.clickable(onClick = onHomeClick),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(Res.drawable.crew_wiki_icon),
                contentDescription = "크루위키 로고",
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(6.dp)),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "크루위키",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = MaterialTheme.typography.displaySmall.fontFamily,
                ),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // ── 우측 ──
        // 셔플(랜덤) 아이콘
        IconButton(
            onClick = onShuffle,
            enabled = !shuffleLoading,
        ) {
            if (shuffleLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp,
                )
            } else {
                Icon(
                    imageVector = ShuffleIcon,
                    contentDescription = "랜덤 문서",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp),
                )
            }
        }

        // 검색 아이콘
        IconButton(onClick = onSearch) {
            Icon(
                imageVector = SearchIcon,
                contentDescription = "검색",
                tint = Color.White,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}
