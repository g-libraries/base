package com.core.base.usecases

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject
import timber.log.Timber

interface RxBus {
    fun publish(event: Any)
    fun <T> listen(eventType: Class<T>): Observable<T>
}

object RxBusGlobal : RxBus {
    private val publisher = PublishSubject.create<Any>()

    override fun publish(event: Any) {
        Timber.i("RxBusGlobal publish : ${event.javaClass.simpleName}")
        publisher.onNext(event)
    }

    // Listen should return an Observable and not the publisher
    // Using ofType we filter only events that match that class type
    override fun <T> listen(eventType: Class<T>): Observable<T> {
        Timber.i("RxBusGlobal listen : ${eventType.javaClass.simpleName}")
        return publisher.ofType(eventType)
    }
}

object RxBusBehaviour : RxBus {

    private val publisher = ReplaySubject.createWithSize<Any>(1)

    override fun publish(event: Any) {
        Timber.i("RxBusBehaviour publish : ${event.javaClass.simpleName}")
        publisher.onNext(event)
    }

    // Listen should return an Observable and not the publisher
    // Using ofType we filter only events that match that class type
    override fun <T> listen(eventType: Class<T>): Observable<T> {
        Timber.i("RxBusBehaviour listen : ${eventType.javaClass.simpleName}")
        return publisher.ofType(eventType)
    }
}

object RxBusReplay : RxBus {

    private val publisher = ReplaySubject.create<Any>()

    override fun publish(event: Any) {
        Timber.i("ReplaySubject publish : ${event.javaClass.simpleName}")
        publisher.onNext(event)
    }

    // Listen should return an Observable and not the publisher
    // Using ofType we filter only events that match that class type
    override fun <T> listen(eventType: Class<T>): Observable<T> {
        Timber.i("ReplaySubject listen : ${eventType.javaClass.simpleName}")
        return publisher.ofType(eventType)
    }
}

data class EventServerError(val code: Int)