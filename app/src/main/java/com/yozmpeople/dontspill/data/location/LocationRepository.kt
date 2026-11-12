package com.yozmpeople.dontspill.data.location

import com.yozmpeople.dontspill.common.ApplicationScope
import com.yozmpeople.dontspill.data.location.model.Location
import com.yozmpeople.dontspill.data.location.pleos.PleosLocationSource
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

// 마지막 구독이 끝나도 잠깐은 SDK 등록을 유지한다. 짧은 재구독에 다시 등록하지 않으려고서다.
private const val STOP_TIMEOUT_MS = 5_000L

interface LocationRepository {
    val location: Flow<Location>
}

// 여러 곳에서 구독해도 SDK에는 한 번만 등록한다. 새 구독자는 마지막 값을 바로 받는다.
@Singleton
class DefaultLocationRepository @Inject constructor(
    pleosLocationSource: PleosLocationSource,
    @ApplicationScope scope: CoroutineScope
) : LocationRepository {

    override val location: Flow<Location> = pleosLocationSource.observeLocation()
        .map { Location(it.latitude, it.longitude, it.timestampMillis) }
        .shareIn(scope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), replay = 1)
}
