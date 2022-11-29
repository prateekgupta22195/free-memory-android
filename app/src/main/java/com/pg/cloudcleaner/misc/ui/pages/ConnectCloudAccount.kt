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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.pg.cloudcleaner.misc.vm.MainViewModel
import com.pg.cloudcleaner.utils.LogCompositions

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun ConnectCloudAccount(vm: MainViewModel = viewModel()) {
    LogCompositions(msg = "ConnectCloudAccount")
    val account by vm.lastSignedInAccount.observeAsState(null)
    Home(account = account, vm)
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
