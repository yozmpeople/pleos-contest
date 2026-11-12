package com.yozmpeople.dontspill.data.driving.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DrivingDao {

    // 드라이빙이 시작될 때 넣는다. endedAtMs는 null이다.
    @Insert
    suspend fun insert(driving: DrivingEntity)

    // 주행 중 점수를 저장한다. 끝난 드라이빙은 바꾸지 않는다.
    @Query(
        "UPDATE driving SET score = :score, penaltyCounts = :penaltyCounts " +
            "WHERE drivingId = :drivingId AND endedAtMs IS NULL"
    )
    suspend fun updateScore(drivingId: String, score: Int, penaltyCounts: String)

    // 최종 결과를 넣고 끝낸다. 이미 끝난 드라이빙이면 아무것도 바꾸지 않는다.
    @Query(
        "UPDATE driving SET endedAtMs = :endedAtMs, score = :score, penaltyCounts = :penaltyCounts, " +
            "distanceMeters = :distanceMeters WHERE drivingId = :drivingId AND endedAtMs IS NULL"
    )
    suspend fun finish(drivingId: String, endedAtMs: Long, score: Int, penaltyCounts: String, distanceMeters: Double)

    // 끝났지만 아직 보여 주지 않은 결과. 최근에 끝난 것부터
    @Query(
        "SELECT * FROM driving WHERE endedAtMs IS NOT NULL AND resultShown = 0 " +
            "ORDER BY endedAtMs DESC"
    )
    fun observeUnshownResults(): Flow<List<DrivingEntity>>

    // 가장 최근 결과만 보여 주고, 쌓인 결과는 모두 본 것으로 한다
    @Query("UPDATE driving SET resultShown = 1 WHERE endedAtMs IS NOT NULL")
    suspend fun markAllResultsShown()
}
