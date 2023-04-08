package com.pg.cloudcleaner.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties


@Composable
fun PopupWindowDialog() {
    // on below line we are creating variable for button title
    // and open dialog.
    val openDialog = remember { mutableStateOf(false) }
    val buttonTitle = remember {
        mutableStateOf("Show Pop Up")
    }

    // on the below line we are creating a column
    Column(

        // in this column we are specifying
        // modifier to add padding and fill
        // max size
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),

        // on below line we are adding horizontal alignment
        // and vertical arrangement
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {

        // on the below line we are creating a button
        Button(

            // on below line we are adding modifier.
            // and padding to it,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),

            // on below line we are adding
            // on click to our button
            onClick = {

                // on below line we are updating
                // boolean value of open dialog.
                openDialog.value = !openDialog.value

                // on below line we are checking if dialog is close
                if (!openDialog.value) {

                    // on below line we are updating value
                    buttonTitle.value = "Show Pop Up"
                }
            }) {

            // on the below line we are creating a text for our button.
            Text(text = buttonTitle.value, modifier = Modifier.padding(3.dp))
        }

        // on below line we are creating a box to display box.
        // on below line we are specifying height and width
        val popupWidth = 300.dp
        val popupHeight = 100.dp

        // on below line we are checking if dialog is open
        if (openDialog.value) {
            // on below line we are updating button
            // title value.
            buttonTitle.value = "Hide Pop Up"
            // on below line we are adding pop up
            Popup(
                // on below line we are adding
                // alignment and properties.

                alignment = Alignment.TopEnd, properties = PopupProperties(dismissOnBackPress = true, dismissOnClickOutside = true), offset = IntOffset(0, 56)
            ) {

                // on the below line we are creating a box.
                Box(
                    // adding modifier to it.
                    Modifier
                        .size(popupWidth, popupHeight)
                        .padding(top = 5.dp)
                        // on below line we are adding background color
                        .background(Color.Green, RoundedCornerShape(10.dp))
                        // on below line we are adding border.
                        .border(1.dp, color = Color.Black, RoundedCornerShape(10.dp))
                ) {

                    // on below line we are adding column
                    Column(
                        // on below line we are adding modifier to it.
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        // on below line we are adding horizontal and vertical
                        // arrangement to it.
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // on below line we are adding text for our pop up
                        Text(
                            // on below line we are specifying text
                            text = "Welcome to Geeks for Geeks",
                            // on below line we are specifying color.
                            color = Color.White,
                            // on below line we are adding padding to it
                            modifier = Modifier.padding(vertical = 5.dp),
                            // on below line we are adding font size.
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}