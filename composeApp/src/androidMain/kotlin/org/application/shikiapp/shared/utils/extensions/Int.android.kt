package org.application.shikiapp.shared.utils.extensions

import android.content.pm.verify.domain.DomainVerificationUserState
import android.os.Build
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.ResourceText
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_turned_off
import shikiapp.composeapp.generated.resources.text_turned_on
import shikiapp.composeapp.generated.resources.text_unknown
import shikiapp.composeapp.generated.resources.text_verified

fun Int.toDomainStateString(): ResourceText = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
    ResourceText.StaticString(BLANK)
} else {
    when (this) {
        DomainVerificationUserState.DOMAIN_STATE_VERIFIED -> ResourceText.StringResource(Res.string.text_verified)
        DomainVerificationUserState.DOMAIN_STATE_SELECTED -> ResourceText.StringResource(Res.string.text_turned_on)
        DomainVerificationUserState.DOMAIN_STATE_NONE -> ResourceText.StringResource(Res.string.text_turned_off)
        else -> ResourceText.StringResource(Res.string.text_unknown)
    }
}