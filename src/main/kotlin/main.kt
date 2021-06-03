import androidx.compose.desktop.LocalAppWindow
import androidx.compose.desktop.Window
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.lordcodes.turtle.shellRun
import org.jetbrains.skija.Image
import java.awt.FileDialog
import java.io.File
import java.io.FilenameFilter

fun main() = Window(title = "Pixel Sorter", size = IntSize(900, 900)) {
    var image by remember { mutableStateOf<File?>(null) }
    val window = LocalAppWindow.current
    var useAngle by remember { mutableStateOf(true) }
    var angle by remember { mutableStateOf(0) }
    var patternExpanded by remember { mutableStateOf(false) }
    val patternItems = listOf("Lines", "Circles")
    var patternSelectedIndex by remember { mutableStateOf(0) }
    MaterialTheme {
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(
                modifier = Modifier.padding(2.5.dp) then Modifier.fillMaxHeight() then Modifier.fillMaxWidth(fraction = image?.let { 0.5f }
                    ?: 1f),
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                Button(modifier = Modifier.align(Alignment.CenterHorizontally).padding(2.5.dp),
                    onClick = {
                        image = FileDialog(window.window).apply {
                            isVisible = true
                            filenameFilter = FilenameFilter { _, name -> name.endsWith(".jpg") }
                        }.files.firstOrNull()
                    }) {
                    Text(text = image?.let { "Change File" } ?: "Open File")
                }
                image?.let {
                    Image(
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(2.5.dp),
                        bitmap = it.imageBitmap(),
                        contentDescription = null,
                    )
                }
            }
            image?.let {
                Column(
                    modifier = Modifier.padding(2.5.dp) then Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Row(
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(2.5.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        Text(
                            text = patternItems[patternSelectedIndex],
                            modifier = Modifier.clickable(onClick = { patternExpanded = true }),
                        )
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.clickable { patternExpanded = !patternExpanded }
                        )
                        DropdownMenu(
                            expanded = patternExpanded,
                            onDismissRequest = { patternExpanded = false }
                        ) {
                            patternItems.forEachIndexed { index, pattern ->
                                DropdownMenuItem(
                                    onClick = {
                                        patternSelectedIndex = index
                                        patternExpanded = false
                                    }
                                ) {
                                    Text(text = pattern)
                                }
                            }
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Row(
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(2.5.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            Checkbox(
                                checked = useAngle,
                                onCheckedChange = { useAngle = it },
                            )
                            Text(
                                text = "Angle",
                                modifier = Modifier.clickable(onClick = { useAngle = !useAngle }),
                            )
                        }
                        Slider(
                            value = angle.toFloat(),
                            valueRange = 0f..359f,
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(2.5.dp),
                            onValueChange = { angle = it.toInt() },
                            enabled = useAngle,
                        )
                        OutlinedTextField(
                            value = angle.toString(),
                            onValueChange = { angle = it.toIntOrNull() ?: 0 },
                            singleLine = true,
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(2.5.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text(text = "Angle") },
                            enabled = useAngle,
                        )
                    }
                    Button(modifier = Modifier.align(Alignment.CenterHorizontally).padding(2.5.dp),
                        onClick = {
                            val arguments = mutableListOf(
                                it.absolutePath,
                                "-p",
                                patternItems[patternSelectedIndex].toLowerCase(),
                            )
                            if (useAngle) {
                                arguments.add("-a")
                                arguments.add(angle.toString())
                            }
                            shellRun(
                                command = "pixel-sorter",
                                arguments = arguments
                            )
                        }) {
                        Text(text = "Run")
                    }
                }
            }
        }
    }
}

fun File.imageBitmap() = Image.makeFromEncoded(readBytes()).asImageBitmap()
