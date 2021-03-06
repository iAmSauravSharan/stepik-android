package org.stepic.droid.util

import android.net.Uri

/**
 * Returns map with query params
 */
fun Uri.getQueryParameters(): Map<String, String> =
    queryParameterNames
        .associateWith { getQueryParameter(it) }

/**
 * Returns map with all query params
 */
fun Uri.getAllQueryParameters(): Map<String, List<String>> =
    queryParameterNames
        .associateWith { getQueryParameters(it) }


@JvmName("Uri_Builder_appendQueryParameters")
fun Uri.Builder.appendQueryParameters(queryParamMap: Map<String, String>): Uri.Builder =
    queryParamMap
        .entries
        .fold(this) { builder, (key, value) ->
            builder.appendQueryParameter(key, value)
        }

@JvmName("Uri_Builder_appendAllQueryParameters")
fun Uri.Builder.appendQueryParameters(queryParamMap: Map<String, List<String>>): Uri.Builder =
    queryParamMap
        .entries
        .fold(this) { builder, (key, values) ->
            values.fold(builder) { uriBuilder, value ->
                uriBuilder.appendQueryParameter(key, value)
            }
        }