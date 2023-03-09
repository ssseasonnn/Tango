package zlc.season.tango

import android.content.res.Resources

private val resources by lazy {
    Resources.getSystem()
}

private val density: Float by lazy {
    resources.displayMetrics.density
}

/**
 * px to dp
 */
val Int.dp: Int
    get() = this.toFloat().dp.toInt()

/**
 * dp to px
 */
val Int.px: Int
    get() = this.toFloat().px.toInt()

val Long.dp: Long
    get() = this.toDouble().dp.toLong()

val Long.px: Long
    get() = this.toDouble().px.toLong()

val Float.dp: Float
    get() = this / density

val Float.px: Float
    get() = this * density

val Double.dp: Double
    get() = this / density

val Double.px: Double
    get() = this * density

