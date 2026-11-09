package com.yozmpeople.dontspill.data.network

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException

// 서버의 모든 응답이 쓰는 공통 틀이다. 실제 값은 data에 있고, data는 없을 수도 있다.
@Serializable
data class ApiResponse<T>(
    @SerialName("status") val status: Int,
    @SerialName("code") val code: String,
    @SerialName("message") val message: String,
    @SerialName("data") val data: T? = null
)

// 서버가 실패로 응답했을 때 던진다. 공통 실패 처리 함수(safeRequest)가 화면용 에러로 바꾼다.
class ApiException(
    val status: Int,
    val code: String,
    override val message: String
) : Exception(message)

// 공통 틀을 풀어 data를 꺼낸다. data가 없을 수 있는 API는 unwrap<Foo?>()처럼 nullable 타입으로 부른다.
// HTTP 상태나 본문의 status가 실패면 ApiException을 던진다.
suspend inline fun <reified T> HttpResponse.unwrap(): T {
    val response = try {
        body<ApiResponse<T>>()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        // 실패 응답인데 본문이 공통 틀이 아니면(게이트웨이 오류 페이지 등) HTTP 상태로 알린다.
        if (status.isSuccess()) throw e
        throw ApiException(status.value, "", status.description)
    }
    if (!status.isSuccess() || response.status !in 200..299) {
        throw ApiException(response.status, response.code, response.message)
    }
    if (response.data == null && null !is T) throw SerializationException("응답에 data가 없습니다.")
    return response.data as T
}
