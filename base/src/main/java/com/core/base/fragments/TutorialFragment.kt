package com.core.base.fragments

import android.os.Bundle
import android.view.View
import com.core.base.fragments.BaseFragment
import com.core.base.interfaces.ITutorialHelper
import javax.inject.Inject

class TutorialFragment : BaseFragment() {
    @Inject
    lateinit var tutorialHelper: ITutorialHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tutorialHelper.attach(this)
        tutorialHelper.showStep()
    }
}