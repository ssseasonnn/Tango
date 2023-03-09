package zlc.season.tango

import android.util.Log.*

private var DEBUG = true
private var LOG_TAG = "TANGO"

fun setLogTag(tag: String) {
    LOG_TAG = tag
}

fun setDebug(flag: Boolean) {
    DEBUG = flag
}

fun <T> T.logd(prefix: String = "", tag: String = ""): T {
    innerLog(::d, ::d, prefix, tag)
    return this
}

fun <T> T.logi(prefix: String = "", tag: String = ""): T {
    innerLog(::i, ::i, prefix, tag)
    return this
}

fun <T> T.logw(prefix: String = "", tag: String = ""): T {
    innerLog(::w, ::w, prefix, tag)
    return this
}

fun <T> T.loge(prefix: String = "", tag: String = ""): T {
    innerLog(::e, ::e, prefix, tag)
    return this
}

fun <T> T.logv(prefix: String = "", tag: String = ""): T {
    innerLog(::v, ::v, prefix, tag)
    return this
}

private fun <T> T.innerLog(
    function1: (String, String, Throwable) -> Int,
    function2: (String, String) -> Int,
    prefix: String,
    tag: String
) {
    if (DEBUG) {
        val realTag = tag.ifEmpty { LOG_TAG }
        if (this is Throwable) {
            function1(realTag, "$prefix ${this.message}", this)
        } else {
            function2(realTag, "$prefix ${this.toString()}")
        }
    }
}