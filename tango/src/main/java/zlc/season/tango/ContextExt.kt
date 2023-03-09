package zlc.season.tango

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

fun Context.findComponentActivity(): ComponentActivity? {
    val activity = findActivity() ?: return null
    if (activity is ComponentActivity) {
        return activity
    }
    return null
}

fun Context.findCoroutineScope(): CoroutineScope? {
    return findComponentActivity()?.lifecycleScope
}

fun Context.findLifecycle(): Lifecycle? {
    return findComponentActivity()?.lifecycle
}