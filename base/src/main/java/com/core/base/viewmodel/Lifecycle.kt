package com.core.base.viewmodel

interface Lifecycle {
    fun onCreate()
    fun onCreated()
    fun onStart()
    fun onStop()
    fun onDestroy()
}