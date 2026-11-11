package com.yozmpeople.dontspill.common

import androidx.compose.ui.tooling.preview.Preview

// Pleos 화면 프리셋 크기(dp). portrait는 window 1개, landscape는 window 2개 크기다.

@Preview(name = "S-17 portrait", group = "portrait", widthDp = 840, heightDp = 1184)
@Preview(name = "S-14.6 portrait", group = "portrait", widthDp = 672, heightDp = 948)
@Preview(name = "L-12.9 portrait", group = "portrait", widthDp = 628, heightDp = 855)
@Preview(name = "W-Standard portrait", group = "portrait", widthDp = 800, heightDp = 810)
@Preview(name = "W-Extended portrait", group = "portrait", widthDp = 800, heightDp = 1400)
@Preview(name = "WF portrait", group = "portrait", widthDp = 648, heightDp = 800)
annotation class PleosPortraitPreviews

@Preview(name = "S-17 landscape", group = "landscape", widthDp = 1700, heightDp = 1184)
@Preview(name = "S-14.6 landscape", group = "landscape", widthDp = 1360, heightDp = 948)
@Preview(name = "L-12.9 landscape", group = "landscape", widthDp = 1274, heightDp = 855)
@Preview(name = "W-Standard landscape", group = "landscape", widthDp = 1640, heightDp = 810)
@Preview(name = "W-Extended landscape", group = "landscape", widthDp = 1640, heightDp = 1400)
@Preview(name = "WF landscape", group = "landscape", widthDp = 1256, heightDp = 800)
annotation class PleosLandscapePreviews

// 프리셋 12개(portrait 6, landscape 6)를 한 번에 본다.
@PleosPortraitPreviews
@PleosLandscapePreviews
annotation class PleosPreviews
