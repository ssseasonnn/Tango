package zlc.season.tango

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun View.click(block: () -> Unit) {
    this.setOnClickListener {
        block()
    }
}

fun View.longClick(block: () -> Unit) {
    this.setOnLongClickListener {
        block()
        return@setOnLongClickListener true
    }
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.isGone(): Boolean {
    return this.visibility == View.GONE
}

fun View.isVisible(): Boolean {
    return this.visibility == View.VISIBLE
}

fun View.isInvisible(): Boolean {
    return this.visibility == View.INVISIBLE
}

fun ViewGroup.inflate(res: Int, attach: Boolean = false): View {
    return LayoutInflater.from(this.context).inflate(res, this, attach)
}
