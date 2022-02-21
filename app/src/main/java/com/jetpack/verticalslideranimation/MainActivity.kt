package com.jetpack.verticalslideranimation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jetpack.verticalslideranimation.ui.theme.VerticalSliderAnimationTheme

@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var sliderProgressValue by rememberSaveable { mutableStateOf(50) }
            VerticalSliderAnimationTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = "Vertical Slider Animation",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            )
                        }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$sliderProgressValue",
                                textAlign = TextAlign.Center,
                                fontSize = 50.sp,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.padding(10.dp))

                            Card(
                                modifier = Modifier
                                    .wrapContentSize(),
                                shape = RoundedCornerShape(30.dp),
                                elevation = 5.dp
                            ) {
                                ComposeVerticalSlider(progressValue = sliderProgressValue) {
                                    sliderProgressValue = it
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun ComposeVerticalSlider(
    progressValue: Int? = null,
    value: (Int) -> Unit
) {
    val state = rememberComposeVerticalSlider()
    VerticalSlider(
        state = state,
        onProgressChanged = {
            value(it)
        },
        onStopTrackingTouch = {
            value(it)
        },
        enabled = state.isEnabled.value,
        progressValue = progressValue
    )
}




















