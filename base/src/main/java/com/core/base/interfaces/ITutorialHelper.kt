package com.core.base.interfaces

import com.core.base.fragments.BaseFragment

interface ITutorialHelper {

    /**
     * Attach Tutorial to BaseFragment
     */
    fun attach(fragment: BaseFragment)

    /**
     * Show current Tutorial step
     */
    fun showStep()
}