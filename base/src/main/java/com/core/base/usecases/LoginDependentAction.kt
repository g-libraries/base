package com.core.base.usecases


/**
 * Choosing which func to launch
 */

fun guestDependentFun(
    guest: Boolean,
    onGuest: () -> Unit,
    onUser: () -> Unit
) {
    if (guest) {
        onGuest()
    } else {
        onUser()
    }
}