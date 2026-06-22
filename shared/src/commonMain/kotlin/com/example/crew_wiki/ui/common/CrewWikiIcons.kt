package com.example.crew_wiki.ui.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/** crew-wiki-next RandomButton의 SVG 경로를 Compose ImageVector로 변환 */
val ShuffleIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "Shuffle",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 36f,
        viewportHeight = 36f,
    ).apply {
        path(
            fill = SolidColor(Color.White),
            fillAlpha = 1f,
            pathFillType = PathFillType.NonZero,
        ) {
            // crew-wiki-next RandomButton mobile SVG path (36x36)
            moveTo(23.7665f, 9.0503f)
            curveTo(23.4829f, 8.8938f, 23.1547f, 9.1199f, 23.1273f, 9.4908f)
            curveTo(23.1071f, 9.7609f, 23.0868f, 10.0985f, 23.0726f, 10.4919f)
            lineTo(22.6472f, 10.4919f)
            curveTo(21.7065f, 10.4917f, 20.7843f, 10.7688f, 19.984f, 11.292f)
            curveTo(19.1838f, 11.8153f, 18.5371f, 12.5641f, 18.1164f, 13.4544f)
            lineTo(17.1034f, 15.5981f)
            lineTo(16.0905f, 13.4544f)
            curveTo(15.6699f, 12.5642f, 15.0233f, 11.8156f, 14.2233f, 11.2923f)
            curveTo(13.4232f, 10.7691f, 12.5012f, 10.4919f, 11.5607f, 10.4919f)
            lineTo(10.0129f, 10.4919f)
            curveTo(9.7443f, 10.4919f, 9.4866f, 10.6048f, 9.2967f, 10.8058f)
            curveTo(9.1067f, 11.0068f, 9.0f, 11.2794f, 9.0f, 11.5637f)
            curveTo(9.0f, 11.848f, 9.1067f, 12.1206f, 9.2967f, 12.3216f)
            curveTo(9.4866f, 12.5226f, 9.7443f, 12.6355f, 10.0129f, 12.6355f)
            lineTo(11.5607f, 12.6355f)
            curveTo(12.1248f, 12.6358f, 12.6777f, 12.8021f, 13.1575f, 13.116f)
            curveTo(13.6373f, 13.4298f, 14.025f, 13.8788f, 14.2774f, 14.4126f)
            lineTo(15.971f, 17.9947f)
            lineTo(14.2794f, 21.5768f)
            curveTo(14.0268f, 22.1111f, 13.6386f, 22.5604f, 13.1582f, 22.8743f)
            curveTo(12.6778f, 23.1882f, 12.1243f, 23.3542f, 11.5597f, 23.3539f)
            lineTo(10.0129f, 23.3539f)
            curveTo(9.7443f, 23.3539f, 9.4866f, 23.4668f, 9.2967f, 23.6678f)
            curveTo(9.1067f, 23.8688f, 9.0f, 24.1414f, 9.0f, 24.4257f)
            curveTo(9.0f, 24.71f, 9.1067f, 24.9826f, 9.2967f, 25.1836f)
            curveTo(9.4866f, 25.3846f, 9.7443f, 25.4975f, 10.0129f, 25.4975f)
            lineTo(11.5607f, 25.4975f)
            curveTo(12.5012f, 25.4975f, 13.4232f, 25.2203f, 14.2233f, 24.6971f)
            curveTo(15.0233f, 24.1738f, 15.6699f, 23.4252f, 16.0905f, 22.535f)
            lineTo(17.1034f, 20.3913f)
            lineTo(18.1164f, 22.535f)
            curveTo(18.537f, 23.4252f, 19.1836f, 24.1738f, 19.9836f, 24.6971f)
            curveTo(20.7837f, 25.2203f, 21.7056f, 25.4975f, 22.6462f, 25.4975f)
            lineTo(23.0726f, 25.4975f)
            curveTo(23.0888f, 25.922f, 23.1111f, 26.2821f, 23.1334f, 26.5608f)
            curveTo(23.1598f, 26.9081f, 23.4596f, 27.0999f, 23.7381f, 26.9467f)
            curveTo(24.1109f, 26.7409f, 24.6559f, 26.4193f, 25.3082f, 25.9713f)
            curveTo(25.8228f, 25.6187f, 26.3217f, 25.241f, 26.8033f, 24.8394f)
            curveTo(27.0545f, 24.6294f, 27.0656f, 24.2242f, 26.8266f, 24.0227f)
            curveTo(26.3384f, 23.6136f, 25.832f, 23.2294f, 25.3092f, 22.8715f)
            curveTo(24.8094f, 22.5265f, 24.2946f, 22.2064f, 23.7665f, 21.9123f)
            curveTo(23.4829f, 21.7558f, 23.1547f, 21.9819f, 23.1273f, 22.3528f)
            curveTo(23.1071f, 22.6229f, 23.0868f, 22.9605f, 23.0726f, 23.3539f)
            lineTo(22.6472f, 23.3539f)
            curveTo(22.0828f, 23.354f, 21.5295f, 23.1879f, 21.0493f, 22.874f)
            curveTo(20.5691f, 22.5601f, 20.181f, 22.1109f, 19.9285f, 21.5768f)
            lineTo(18.2359f, 17.9947f)
            lineTo(19.9275f, 14.4126f)
            curveTo(20.1801f, 13.8783f, 20.5683f, 13.429f, 21.0487f, 13.1151f)
            curveTo(21.5291f, 12.8012f, 22.0826f, 12.6352f, 22.6472f, 12.6355f)
            lineTo(23.0737f, 12.6355f)
            curveTo(23.0899f, 13.06f, 23.1121f, 13.4201f, 23.1344f, 13.6988f)
            curveTo(23.1608f, 14.0461f, 23.4606f, 14.2379f, 23.7392f, 14.0847f)
            curveTo(24.1119f, 13.8789f, 24.6569f, 13.5573f, 25.3092f, 13.1093f)
            curveTo(25.8238f, 12.7567f, 26.3227f, 12.379f, 26.8043f, 11.9774f)
            curveTo(27.0555f, 11.7674f, 27.0666f, 11.3622f, 26.8276f, 11.1607f)
            curveTo(26.3394f, 10.7517f, 25.833f, 10.3675f, 25.3102f, 10.0095f)
            curveTo(24.8104f, 9.6645f, 24.2956f, 9.3444f, 23.7675f, 9.0503f)
            close()
        }
    }.build()
}

/** 검색 아이콘 (돋보기) */
val SearchIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "Search",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(fill = SolidColor(Color.White)) {
            moveTo(15.5f, 14f)
            lineTo(14.71f, 14f)
            lineTo(14.43f, 13.73f)
            curveTo(15.41f, 12.59f, 16f, 11.11f, 16f, 9.5f)
            curveTo(16f, 5.91f, 13.09f, 3f, 9.5f, 3f)
            curveTo(5.91f, 3f, 3f, 5.91f, 3f, 9.5f)
            curveTo(3f, 13.09f, 5.91f, 16f, 9.5f, 16f)
            curveTo(11.11f, 16f, 12.59f, 15.41f, 13.73f, 14.43f)
            lineTo(14f, 14.71f)
            lineTo(14f, 15.5f)
            lineTo(19f, 20.49f)
            lineTo(20.49f, 19f)
            close()
            moveTo(9.5f, 14f)
            curveTo(7.01f, 14f, 5f, 11.99f, 5f, 9.5f)
            curveTo(5f, 7.01f, 7.01f, 5f, 9.5f, 5f)
            curveTo(11.99f, 5f, 14f, 7.01f, 14f, 9.5f)
            curveTo(14f, 11.99f, 11.99f, 14f, 9.5f, 14f)
            close()
        }
    }.build()
}

/** 뒤로가기 화살표 */
val ArrowBackIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "ArrowBack",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(fill = SolidColor(Color.White)) {
            moveTo(20f, 11f)
            lineTo(7.83f, 11f)
            lineTo(13.42f, 5.41f)
            lineTo(12f, 4f)
            lineTo(4f, 12f)
            lineTo(12f, 20f)
            lineTo(13.41f, 18.59f)
            lineTo(7.83f, 13f)
            lineTo(20f, 13f)
            close()
        }
    }.build()
}
