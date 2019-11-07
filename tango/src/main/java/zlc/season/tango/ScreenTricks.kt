package zlc.season.tango

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.WindowManager
import zlc.season.claritypotion.ClarityPotion


private val resources by lazy {
    Resources.getSystem()
}

private val desity: Float by lazy {
    resources.displayMetrics.density
}

private val windowManager by lazy {
    ClarityPotion.clarityPotion.getSystemService(Context.WINDOW_SERVICE) as WindowManager
}

private val defaultDisplay by lazy {
    windowManager.defaultDisplay
}

/**
 * Convert px to dp
 */
val Int.dp: Int
    get() = this.toFloat().dp.toInt()

/**
 * Convert dp to px
 */
val Int.px: Int
    get() = this.toFloat().px.toInt()

val Long.dp: Long
    get() = this.toDouble().dp.toLong()

val Long.px: Long
    get() = this.toDouble().px.toLong()

val Float.dp: Float
    get() = this / desity

val Float.px: Float
    get() = this * desity

val Double.dp: Double
    get() = this / desity

val Double.px: Double
    get() = this * desity


/**
 * return screen width
 */
fun getScreenWidth(): Int {
    return usableDisplayMetrics().widthPixels
}

/**
 * return screen height, not include navigation bar!
 */
fun getScreenHeight(): Int {
    return usableDisplayMetrics().heightPixels
}

/**
 * Return full screen width
 */
fun getRealScreenWidth(): Int {
    return realDisplayMetrics().widthPixels
}

/**
 * Return full screen height, include status bar and navigation bar!
 */
fun getRealScreenHeight(): Int {
    return realDisplayMetrics().heightPixels
}

private fun usableDisplayMetrics(): DisplayMetrics {
    val metrics = DisplayMetrics()
    defaultDisplay.getMetrics(metrics)
    return metrics
}

private fun realDisplayMetrics(): DisplayMetrics {
    val metrics = DisplayMetrics()
    defaultDisplay.getRealMetrics(metrics)
    return metrics
}

/**
 * indicate navigation bar is shown
 */
fun isShowNavigationBar(): Boolean {
    return getNavigationBarHeight() > 0
}

/**
 * return navigation bar height
 */
fun getNavigationBarHeight(): Int {
    val usableHeight = getScreenHeight()
    val realHeight = getRealScreenHeight()

    return if (realHeight > usableHeight) {
        realHeight - usableHeight
    } else {
        0
    }
}