package com.yozmpeople.dontspill.ui.leaderboard

// 리더보드 화면 상태. 실제 순위 데이터 연결은 서버 연동 때 한다.
data class LeaderboardUiState(
    val month: Int,
    val week: Int,
    val rankings: List<LeaderboardEntry>,
    // 순위가 없으면 하단 '내 순위' 영역을 그리지 않는다.
    val myRanking: LeaderboardEntry?
)

data class LeaderboardEntry(
    val rank: Int,
    val nickname: String,
    val score: Int
)

// 데이터 연결 전까지 화면과 Preview에 쓰는 시안 예시 데이터 (사용자, 2026-11-11)
// 5·6위는 가려지는 행을 보려고 덧붙였다 (추정)
internal val SampleLeaderboardUiState = LeaderboardUiState(
    month = 11,
    week = 2,
    rankings = listOf(
        LeaderboardEntry(1, "용감한검정친칠라", 2072),
        LeaderboardEntry(2, "귀여운분홍토끼", 2045),
        LeaderboardEntry(3, "침착한푸른두더지", 2013),
        LeaderboardEntry(4, "건강한노란사자", 1920),
        LeaderboardEntry(5, "느긋한하얀고양이", 1874),
        LeaderboardEntry(6, "씩씩한초록개구리", 1802)
    ),
    myRanking = LeaderboardEntry(192, "분명한주황장어", 490)
)
