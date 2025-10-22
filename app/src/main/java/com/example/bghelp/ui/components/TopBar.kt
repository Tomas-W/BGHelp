package com.example.bghelp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bghelp.ui.screens.task.TaskViewModel
import com.example.bghelp.R
import com.example.bghelp.data.local.TaskEntity
import com.example.bghelp.utils.AlarmMode
import com.example.bghelp.ui.theme.MainBlue
import kotlinx.coroutines.launch
import java.time.Instant

@Composable
fun TopBar(taskViewModel: TaskViewModel) {
    val scope = rememberCoroutineScope()

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                .background(Color.Black)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MainBlue)
                .height(56.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}) {
                Image(
                    painter = painterResource(id = R.drawable.screenshot),
                    contentDescription = "Screenshot",
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = "+",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    val now = Instant.now().epochSecond
                    val temporaryTask = TaskEntity(
                        date = now,
                        message = "Added task",
                        expired = false,
                        alarmName = "AlarmOne",
                        sound = AlarmMode.CONTINUOUS,
                        vibrate = AlarmMode.OFF,
                        snoozeTime = 0
                    )
                }
            )

            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    tint = Color.White,
                    contentDescription = "Menu"
                )
            }
        }
    }
}