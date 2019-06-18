package com.eaterytemplate.viewmodel

interface Lifecycle {
    fun onCreate()
    fun onCreated()
    fun onStart()
    fun onStop()
    fun onDestroy()
}