package zlc.season.tango

import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

val View.lifecycleScope: CoroutineScope
    get() {
        val lifecycleOwner = ViewTreeLifecycleOwner.get(this) ?: throw IllegalStateException("LifecycleOwner not found")
        return lifecycleOwner.lifecycleScope
    }

val View.activityScope: CoroutineScope
    get() {
        return context.findCoroutineScope() ?: throw IllegalStateException("CoroutineScope not found")
    }

val View.lifecycle: Lifecycle
    get() {
        return context.findLifecycle() ?: throw IllegalStateException("Lifecycle not found")
    }

suspend fun View.wait() = suspendCancellableCoroutine {
    val runnable = Runnable {
        if (it.isActive) {
            it.resumeWith(Result.success(Unit))
        }
    }
    it.invokeOnCancellation {
        removeCallbacks(runnable)
    }
    post(runnable)
}

fun <T : View> T.click(delay: Long = 500, block: (T) -> Unit) {
    var time: Long = 0
    setOnClickListener {
        val clickTime = SystemClock.elapsedRealtime()
        if (clickTime - time >= delay) {
            time = clickTime
            block(this)
        }
    }
}

fun <T : View> T.doubleClick(delay: Long = 500, onDoubleClick: (T) -> Unit) {
    multiClick(delay) { view, i ->
        if (i >= 2) {
            onDoubleClick(view)
        }
    }
}

fun <T : View> T.multiClick(delay: Long = 500, block: (T, Int) -> Unit) {
    var count = 0
    val runnable = Runnable {
        block(this, count)
        count = 0
    }
    setOnClickListener {
        count++
        removeCallbacks(runnable)
        postDelayed(runnable, delay)
    }
}

fun View.suspendClick(block: suspend (View) -> Unit) {
    click {
        activityScope.launch {
            block(it)
        }
    }
}

fun View.suspendMultiClick(oneShot: suspend (View) -> Unit, doubleClick: suspend (View) -> Unit) {
    multiClick(400) { v, count ->
        activityScope.launch {
            if (count >= 2) {
                doubleClick(v)
            } else {
                oneShot(v)
            }
        }
    }
}

fun View.suspendLongClick(block: suspend (View) -> Unit) {
    setOnLongClickListener {
        activityScope.launch {
            block(it)
        }
        return@setOnLongClickListener true
    }
}

fun View.clickFlow(): Flow<View> = callbackFlow {
    this@clickFlow.setOnClickListener {
        channel.trySendBlocking(it).onFailure {}
    }
    awaitClose {
        this@clickFlow.setOnClickListener(null)
    }
}

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.visually(flag: Boolean) {
    if (flag) visible() else gone()
}

fun ViewGroup.inflate(res: Int, attach: Boolean = false): View {
    return LayoutInflater.from(this.context).inflate(res, this, attach)
}
