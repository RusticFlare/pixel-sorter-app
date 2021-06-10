import Interval.LIGHTNESS
import Interval.RANDOM
import Pattern.CIRCLES
import Pattern.LINES
import androidx.compose.desktop.LocalAppWindow
import androidx.compose.desktop.Window
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.lordcodes.turtle.shellRun
import org.jetbrains.skija.Image
import java.awt.FileDialog
import java.io.File
import java.io.FilenameFilter

enum class Pattern(val displayText: String) {
    LINES(displayText = "Lines"),
    CIRCLES(displayText = "Circles"),
}

enum class Sort(val displayText: String) {
    LIGHTNESS(displayText = "Lightness"),
    HUE(displayText = "Hue"),
    SATURATION(displayText = "Saturation"),
    INTENSITY(displayText = "Intensity"),
}

enum class Interval(val displayText: String) {
    LIGHTNESS(displayText = "Lightness"),
    RANDOM(displayText = "Random"),
    RANDOMFILE(displayText = "RandomFile"),
    NONE(displayText = "None"),
}

fun main() = Window(title = "Pixel Sorter", size = IntSize(width = 900, height = 900)) {
    var image by remember { mutableStateOf<File?>(null) }
    var mask by remember { mutableStateOf<File?>(null) }
    val window = LocalAppWindow.current

    var useAngle by remember { mutableStateOf(true) }
    var angle by remember { mutableStateOf(0) }

    var reverseTheSort by remember { mutableStateOf(false) }

    var patternExpanded by remember { mutableStateOf(false) }
    var patternSelectedIndex by remember { mutableStateOf(0) }

    fun pattern() = Pattern.values()[patternSelectedIndex]
    fun isCircles() = pattern() == CIRCLES

    var sortExpanded by remember { mutableStateOf(false) }
    var sortSelectedIndex by remember { mutableStateOf(0) }

    fun sort() = Sort.values()[sortSelectedIndex]

    var intervalExpanded by remember { mutableStateOf(false) }
    var intervalSelectedIndex by remember { mutableStateOf(0) }

    fun interval() = Interval.values()[intervalSelectedIndex]
    fun isLightness() = interval() == LIGHTNESS
    fun isRandom() = interval() == RANDOM

    var lowerThreshold by remember { mutableStateOf(0.25f) }
    var upperThreshold by remember { mutableStateOf(0.8f) }

    var averageWidth by remember { mutableStateOf(400) }

    var centerX by remember { mutableStateOf(0) }
    var centerY by remember { mutableStateOf(0) }

    MaterialTheme {
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(
                modifier = Modifier.padding(2.5.dp).fillMaxHeight().fillMaxWidth(fraction = image?.let { 0.5f } ?: 1f),
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                Button(modifier = Modifier.align(Alignment.CenterHorizontally).padding(2.5.dp),
                    onClick = {
                        image = FileDialog(window.window).apply {
                            isVisible = true
                            filenameFilter = FilenameFilter { _, name -> name.endsWith(".jpg") }
                        }.files.firstOrNull()
                    },
                    content = { Text(text = image?.let { "Change File" } ?: "Open File") }
                )
                image?.run {
                    Image(
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(2.5.dp),
                        bitmap = imageBitmap(),
                        contentDescription = null,
                    )
                }
            }
            image?.let { file ->
                Column(
                    modifier = Modifier.padding(2.5.dp) then Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                            .border(
                                border = BorderStroke(width = 1.dp, color = Color.LightGray),
                                shape = RoundedCornerShape(percent = 5),
                            ),
                    ) {
                        Text(
                            text = "Pattern",
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(2.5.dp),
                        )
                        Row(
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(2.5.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            Text(
                                text = pattern().displayText,
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
                                Pattern.values().forEachIndexed { index, pattern ->
                                    DropdownMenuItem(
                                        onClick = {
                                            patternSelectedIndex = index
                                            useAngle = when (pattern) {
                                                LINES -> true
                                                CIRCLES -> false
                                            }
                                            patternExpanded = false
                                        }
                                    ) {
                                        Text(text = pattern.displayText)
                                    }
                                }
                            }
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                            .border(
                                border = BorderStroke(width = 1.dp, color = Color.LightGray),
                                shape = RoundedCornerShape(percent = 5),
                            ),
                    ) {
                        Text(
                            text = "Sort",
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(2.5.dp),
                        )
                        Row(
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(2.5.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            Text(
                                text = sort().displayText,
                                modifier = Modifier.clickable(onClick = { sortExpanded = true }),
                            )
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.clickable { sortExpanded = !sortExpanded }
                            )
                            DropdownMenu(
                                expanded = sortExpanded,
                                onDismissRequest = { sortExpanded = false }
                            ) {
                                Sort.values().forEachIndexed { index, sort ->
                                    DropdownMenuItem(
                                        onClick = {
                                            sortSelectedIndex = index
                                            sortExpanded = false
                                        }
                                    ) {
                                        Text(text = sort.displayText)
                                    }
                                }
                            }
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                            .border(
                                border = BorderStroke(width = 1.dp, color = Color.LightGray),
                                shape = RoundedCornerShape(percent = 5),
                            ),
                    ) {
                        Text(
                            text = "Interval",
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(2.5.dp),
                        )
                        Row(
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(2.5.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            Text(
                                text = interval().displayText,
                                modifier = Modifier.clickable(onClick = { intervalExpanded = true }),
                            )
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.clickable { intervalExpanded = !intervalExpanded }
                            )
                            DropdownMenu(
                                expanded = intervalExpanded,
                                onDismissRequest = { intervalExpanded = false }
                            ) {
                                Interval.values().forEachIndexed { index, interval ->
                                    DropdownMenuItem(
                                        onClick = {
                                            intervalSelectedIndex = index
                                            intervalExpanded = false
                                        }
                                    ) {
                                        Text(text = interval.displayText)
                                    }
                                }
                            }
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.border(
                            border = BorderStroke(width = 1.dp, color = Color.LightGray),
                            shape = RoundedCornerShape(percent = 5),
                        ),
                    ) {
                        Row(
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(2.5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            if (isCircles()) {
                                Checkbox(
                                    checked = useAngle,
                                    onCheckedChange = { useAngle = it },
                                )
                            }
                            val textModifier = if (isCircles()) {
                                Modifier.clickable(onClick = { useAngle = !useAngle })
                            } else {
                                Modifier
                            }
                            Text(
                                text = "Angle",
                                modifier = textModifier,
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
                            onValueChange = {
                                val value = it.toIntOrNull() ?: 0
                                if (value in 0..359) {
                                    angle = value
                                }
                            },
                            singleLine = true,
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(2.5.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            enabled = useAngle,
                        )
                    }
                    if (isLightness()) Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.border(
                            border = BorderStroke(width = 1.dp, color = Color.LightGray),
                            shape = RoundedCornerShape(percent = 5),
                        ),
                    ) {
                        Text(
                            text = "Threshold",
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(2.5.dp),
                        )
                        Row(
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(2.5.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            OutlinedTextField(
                                value = lowerThreshold.toString(),
                                onValueChange = {
                                    val value = it.toFloatOrNull() ?: 0f
                                    if (value in 0f..1f) {
                                        lowerThreshold = value
                                    }
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(fraction = 0.5f).padding(2.5.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                label = { Text(text = "Lower") }
                            )
                            OutlinedTextField(
                                value = upperThreshold.toString(),
                                onValueChange = {
                                    val value = it.toFloatOrNull() ?: 0f
                                    if (value in 0f..1f) {
                                        upperThreshold = value
                                    }
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().padding(2.5.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                label = { Text(text = "Upper") }
                            )
                        }
                    }
                    if (isCircles()) Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.border(
                            border = BorderStroke(width = 1.dp, color = Color.LightGray),
                            shape = RoundedCornerShape(percent = 5),
                        ),
                    ) {
                        Text(
                            text = "Center",
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(2.5.dp),
                        )
                        Row(
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(2.5.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            OutlinedTextField(
                                value = centerX.toString(),
                                onValueChange = {
                                    centerX = it.toIntOrNull() ?: 0
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(fraction = 0.5f).padding(2.5.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                label = { Text(text = "X") }
                            )
                            OutlinedTextField(
                                value = centerY.toString(),
                                onValueChange = { centerY = it.toIntOrNull() ?: 0 },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().padding(2.5.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                label = { Text(text = "Y") }
                            )
                        }
                    }
                    if (isRandom()) OutlinedTextField(
                        value = averageWidth.toString(),
                        onValueChange = {
                            val value = it.toIntOrNull() ?: 1
                            if (value > 1) {
                                averageWidth = value
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(2.5.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text(text = "Average Width") }
                    )
                    Row(
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(2.5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        mask?.run {
                            Image(
                                modifier = Modifier.height(40.dp).padding(2.5.dp),
                                bitmap = imageBitmap(),
                                contentDescription = null,
                            )
                        }
                        Button(
                            modifier = Modifier.padding(2.5.dp),
                            onClick = {
                                mask = FileDialog(window.window).apply {
                                    isVisible = true
                                    filenameFilter = FilenameFilter { _, name -> name.endsWith(".jpg") }
                                }.files.firstOrNull()
                            },
                            content = { Text(text = mask?.let { "Change Mask" } ?: "Select Mask") }
                        )
                        mask?.run {
                            Button(
                                modifier = Modifier.padding(2.5.dp),
                                onClick = { mask = null },
                                content = { Text(text = "Remove Mask") },
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(2.5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Checkbox(
                            checked = reverseTheSort,
                            onCheckedChange = { reverseTheSort = it },
                        )
                        Text(
                            text = "Reverse the sort",
                            modifier = Modifier.clickable(onClick = { reverseTheSort = !reverseTheSort }),
                        )
                    }
                    Button(
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(2.5.dp),
                        onClick = {
                            val arguments = mutableListOf(
                                file.absolutePath,
                                "-p",
                                pattern().displayText.toLowerCase(),
                                "-s",
                                sort().displayText.toLowerCase(),
                                "-i",
                                interval().displayText.toLowerCase(),
                                "-l",
                                lowerThreshold.toString(),
                                "-u",
                                upperThreshold.toString(),
                                "-w",
                                averageWidth.toString(),
                                "-c",
                                centerX.toString(),
                                centerY.toString(),
                            )
                            if (useAngle) {
                                arguments.add("-a")
                                arguments.add(angle.toString())
                            }
                            if (reverseTheSort) {
                                arguments.add("-r")
                            }
                            mask?.run {
                                arguments.add("-m")
                                arguments.add(absolutePath)
                            }
                            shellRun(
                                command = "pixel-sorter",
                                arguments = arguments
                            )
                        },
                        content = { Text(text = "Run") },
                    )
                }
            }
        }
    }
}

fun File.imageBitmap() = Image.makeFromEncoded(readBytes()).asImageBitmap()
