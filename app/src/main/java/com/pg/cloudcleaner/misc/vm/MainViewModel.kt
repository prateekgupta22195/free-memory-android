package com.pg.cloudcleaner.misc.vm

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.pg.cloudcleaner.misc.data.repo.FileActionRepoImpl
import com.pg.cloudcleaner.utils.getGoogleAccessToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(context: Application) : AndroidViewModel(context) {

    private var _lastSignedInAccount: MutableLiveData<GoogleSignInAccount?> = MutableLiveData()
    val lastSignedInAccount: LiveData<GoogleSignInAccount?> get() = _lastSignedInAccount

    private val gso by lazy {
        GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )
            .requestEmail()
            .requestScopes(Scope(Scopes.DRIVE_FULL))
            .build()
    }

    private val fileRepo by lazy {
        FileActionRepoImpl(context = context)
    }

    fun getSignInIntent(context: Context): Intent {
        return GoogleSignIn.getClient(context, gso).signInIntent
    }

    fun handleResult(result: ActivityResult, context: Context) {
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data

            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)

            viewModelScope.launch {
                handleSignInResult(task.getResult(ApiException::class.java), context)
            }
        }
    }

    private suspend fun handleSignInResult(account: GoogleSignInAccount?, context: Context) {
        withContext(Dispatchers.IO) {
            fileRepo.syncDBFiles(getGoogleAccessToken(context = context), null)
            withContext(Dispatchers.Main) {
                _lastSignedInAccount.value = account
            }
        }
    }
}
