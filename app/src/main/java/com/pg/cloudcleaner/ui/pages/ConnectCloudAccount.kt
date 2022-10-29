package com.pg.cloudcleaner.ui.pages

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.pg.cloudcleaner.utils.LogCompositions
import com.pg.cloudcleaner.vm.MainViewModel

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun ConnectCloudAccount() {

    LogCompositions(msg = "ConnectCloudAccount")
    val viewModel = ViewModelProvider(LocalViewModelStoreOwner.current!!)[MainViewModel::class.java]

    val account by viewModel.lastSignedInAccount.observeAsState(null)

    Home(account = account, viewModel)
}

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun Home(account: GoogleSignInAccount?, viewModel: MainViewModel) {
    LogCompositions(msg = "Home")

    val context = LocalContext.current

    val startForResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.handleResult(result, context = context)
            }
        }

    if (account == null)
        Button(onClick = { startForResult.launch(viewModel.getSignInIntent(context)) }) { Text(text = "Hello") }
    else
        FileExplorer()
}
