package com.yozmpeople.dontspill.data.network

// 서버 에러 코드 표. 상수 이름은 서버 표의 name, 참고는 각 항목 위 주석에 적는다.
// 앱이 코드에 따라 동작을 나눠야 하는 것만 넣는다.
enum class ApiErrorCode(
    val code: String,
    val httpStatus: Int,
    val message: String
) {
    // access token 재발급 필요
    INVALID_TOKEN("T001", 400, "Invalid Token Input"),
    NO_ACCESS_AUTHORIZED("T002", 403, "No Access Allowed"),
    NO_SIGN_IN("T003", 401, "No Sign In "),
    ACCESS_TOKEN_EXPIRED("T004", 401, "Access Token Expired"),

    // refresh token 만료 -> 다시 큐알 로그인 진행할 수 있도록 로그아웃 처리 필요
    REFRESH_TOKEN_EXPIRED("T005", 401, "Refresh Token Expired");

    companion object {
        fun from(code: String): ApiErrorCode? = entries.find { it.code == code }
    }
}

// ApiException의 code를 표의 항목으로 바꾼다. 표에 없으면 null
val ApiException.errorCode: ApiErrorCode?
    get() = ApiErrorCode.from(code)
