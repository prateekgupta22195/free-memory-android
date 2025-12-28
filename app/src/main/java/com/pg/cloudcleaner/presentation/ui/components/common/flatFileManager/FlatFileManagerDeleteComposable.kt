import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pg.cloudcleaner.presentation.vm.SelectableDeletableVM
import kotlinx.coroutines.launch

@Composable
fun FlatFileManagerDeleteComposable(vm: SelectableDeletableVM) {
    val selectedFileIds = remember { vm.selectedFiles }
    val selectedModeOn = remember { vm.selectedModeOn }
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Button(
            onClick = {
                scope.launch {
                    vm.deleteFiles(selectedFileIds.value)
                }
                selectedModeOn.value = false
            },
            enabled = selectedFileIds.value.isNotEmpty(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(text = if (selectedFileIds.value.isEmpty()) "Delete" else "Delete ${selectedFileIds.value.size} Files")
        }
    }
}