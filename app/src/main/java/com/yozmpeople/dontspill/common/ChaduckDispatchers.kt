package com.yozmpeople.dontspill.common

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

// 화면보다 오래 사는 작업용 범위. GlobalScope 대신 주입해서 쓴다.
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope
