package com.amefure.unchilog.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel

abstract class RootViewModel(application: Application) : AndroidViewModel(application) {
    protected val rootRepository = (application as RootApplication).rootRepository
}