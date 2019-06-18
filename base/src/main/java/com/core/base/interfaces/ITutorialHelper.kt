package com.eaterytemplate.interfaces

import com.eaterytemplate.fragments.BaseFragment

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