package zlc.season.tango

import android.graphics.Rect
import android.os.SystemClock
import android.util.Size
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
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

const val CLICK_DEBOUNCE = 500L

fun <T : View> T.click(delay: Long = CLICK_DEBOUNCE, block: (T) -> Unit) {
    var time: Long = 0
    setOnClickListener {
        val clickTime = SystemClock.elapsedRealtime()
        if (clickTime - time >= delay) {
            time = clickTime
            block(this)
        }
    }
}

fun <T : View> T.longClick(block: (T) -> Unit) {
    setOnLongClickListener {
        block(this@longClick)
        return@setOnLongClickListener true
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

fun <T : View> T.suspendClick(block: suspend (T) -> Unit) {
    click {
        activityScope.launch {
            block(it)
        }
    }
}

fun <T : View> T.suspendMultiClick(oneShot: suspend (T) -> Unit, doubleClick: suspend (T) -> Unit) {
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

fun <T : View> T.suspendLongClick(block: suspend (T) -> Unit) {
    setOnLongClickListener {
        activityScope.launch {
            block(this@suspendLongClick)
        }
        return@setOnLongClickListener true
    }
}

@OptIn(FlowPreview::class)
fun <T : View> T.clickFlow(delay: Long = CLICK_DEBOUNCE): Flow<T> = callbackFlow {
    setOnClickListener {
        channel.trySendBlocking(this@clickFlow).onFailure {}
    }
    awaitClose {
        setOnClickListener(null)
    }
}.debounce(delay)

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

suspend fun View.getSize(): Size = suspendCancellableCoroutine {
    val runnable = Runnable {
        if (it.isActive) {
            it.resumeWith(Result.success(Size(width, height)))
        }
    }
    it.invokeOnCancellation {
        removeCallbacks(runnable)
    }
    post(runnable)
}

suspend fun View.getScreenRect(): Rect = suspendCancellableCoroutine {
    val rect = getScreenRectDirect()
    if (rect.isEmpty) {
        val runnable = Runnable {
            if (it.isActive) {
                it.resumeWith(Result.success(getScreenRectDirect()))
            }
        }
        it.invokeOnCancellation {
            removeCallbacks(runnable)
        }
        post(runnable)
    } else {
        it.resumeWith(Result.success(rect))
    }
}

fun View.getScreenRectDirect(): Rect {
    val intArray = IntArray(2)
    getLocationInWindow(intArray)

    val x = intArray[0]
    val y = intArray[1]

    val rect = Rect()
    rect.left = x
    rect.top = y
    rect.right = x + width
    rect.bottom = y + height
    return rect
}