package com.jnetai.gdprcompliance

import android.app.Application
import com.jnetai.gdprcompliance.data.GDPRDatabase

class GDPRApp : Application() {
    val database by lazy { GDPRDatabase.getInstance(this) }
}