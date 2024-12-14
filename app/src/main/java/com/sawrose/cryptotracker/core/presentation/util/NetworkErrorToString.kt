package com.sawrose.cryptotracker.core.presentation.util

import android.content.Context
import com.sawrose.cryptotracker.R
import com.sawrose.cryptotracker.core.domain.util.NetworkError

fun NetworkError.toString(context: Context): String {
    val resId = when(this) {
        NetworkError.REQUEST_TIMEOUT -> R.string.request_timout_error
        NetworkError.TOO_MANY_REQUESTS -> R.string.too_many_requests_error
        NetworkError.NO_INTERNET_CONNECTION -> R.string.no_internet_connection_error
        NetworkError.SERVER_ERROR -> R.string.server_error
        NetworkError.SERIALIZATION_ERROR -> R.string.serialization_error
        NetworkError.UNKNOWN_ERROR -> R.string.unknown_error
    }
    return context.getString(resId)
}