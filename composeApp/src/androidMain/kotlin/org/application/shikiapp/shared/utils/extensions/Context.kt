package org.application.shikiapp.shared.utils.extensions

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.MainScope

fun Context.showToast(text: String, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, text, length).show()

inline val Context.asyncScope
    get() = (this as? LifecycleOwner)?.lifecycleScope ?: MainScope()