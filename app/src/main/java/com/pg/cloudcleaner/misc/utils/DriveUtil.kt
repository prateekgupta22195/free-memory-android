package com.pg.cloudcleaner.utils

import android.content.Context
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.Scopes

fun getGoogleAccessToken(context: Context): String {
    val scope = "oauth2:${Scopes.DRIVE_FULL}"
    GoogleSignIn.getLastSignedInAccount(context)?.account?.let {
        return GoogleAuthUtil.getToken(context, it, scope)
    } ?: throw IllegalStateException("No Account logged In")
}
