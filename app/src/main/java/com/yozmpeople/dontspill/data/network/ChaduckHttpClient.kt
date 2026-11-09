package com.yozmpeople.dontspill.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlin.time.Duration.Companion.seconds
import kotlinx.serialization.json.Json

// 요청 전체 상한은 Pleos 심사 기준 "콘텐츠 10초 이내 로드"에 맞춘다. 연결·소켓 값은 임시값이다.
private val REQUEST_TIMEOUT = 10.seconds
private val CONNECT_TIMEOUT = 5.seconds
private val SOCKET_TIMEOUT = 10.seconds

// 앱과 테스트가 같은 설정을 쓰도록 엔진만 바꿔 끼운다. 앱은 CIO, 테스트는 MockEngine을 넣는다.
fun createChaduckHttpClient(engine: HttpClientEngine, baseUrl: String, json: Json): HttpClient = HttpClient(engine) {
    expectSuccess = false
    install(ContentNegotiation) {
        json(json)
    }
    install(HttpTimeout) {
        requestTimeoutMillis = REQUEST_TIMEOUT.inWholeMilliseconds
        connectTimeoutMillis = CONNECT_TIMEOUT.inWholeMilliseconds
        socketTimeoutMillis = SOCKET_TIMEOUT.inWholeMilliseconds
    }
    defaultRequest {
        url(baseUrl)
        contentType(ContentType.Application.Json)
    }
}
