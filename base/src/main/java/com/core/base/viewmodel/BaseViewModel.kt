package com.eaterytemplate.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.core.base.usecases.Event
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber

open class BaseViewModel : ScopedViewModel() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    val errorEvent = MutableLiveData<Event<Int>>()

    val internalErrorEvent = MutableLiveData<Event<Throwable>>()

    protected fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    private fun clearDisposables() {
        compositeDisposable.clear()
    }

    override fun onCleared() {
        clearDisposables()
    }

    /**
     *  400 - для любого некорректного запроса со стороны приложения
     *  401 - пользователь не авторизован
     *  403 - ошибка неверно введенного кода авторизации или если код уже не активен
     *  422 - ошибка валидации данных
     *  423 - ошибка если пользователь заблокирован
     *  503 - сервер недоступен
     *  504 - сервер недоступен
     *  499 - ошибка - гостевой токен
     */

    fun handleError(throwable: Throwable) {
            Timber.e(throwable)
    }

    fun handleErrorExt(throwable: Throwable) {
        internalErrorEvent.value = Event(throwable)
        Timber.e(throwable)
    }
}