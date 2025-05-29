package com.mybitmirror.ui.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mybitmirror.ui.theme.MyBitMirrorTheme // Assuming this theme exists and is accessible

@Composable
fun RhombusView(
    modifier: Modifier = Modifier,
    color: Color
) {
    Canvas(modifier = modifier) {
        val path = Path().apply {
            moveTo(size.width / 2f, 0f) // Top center
            lineTo(size.width, size.height / 2f) // Middle right
            lineTo(size.width / 2f, size.height) // Bottom center
            lineTo(0f, size.height / 2f) // Middle left
            close() // Close the path to form the rhombus
        }
        drawPath(
            path = path,
            color = color
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RhombusViewPreview() {
    MyBitMirrorTheme {
        RhombusView(
            modifier = Modifier
                .size(60.dp) // Increased size for better preview with padding
                .padding(10.dp),
            color = Color.Magenta
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RhombusViewPreviewDifferentSize() {
    MyBitMirrorTheme {
        RhombusView(
            modifier = Modifier.size(30.dp),
            color = Color.Blue
        )
    }
}
