package com.a0100019.mypat.presentation.ui.component

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.a0100019.mypat.R
import androidx.annotation.DrawableRes
import androidx.compose.ui.unit.Dp
import com.a0100019.mypat.presentation.ui.MusicPlayer
import com.a0100019.mypat.presentation.ui.SfxPlayer

@Composable
fun MainButton(
    text: String = "",
    @DrawableRes iconResId: Int? = null,
    showBadge: Boolean = false,
    imageSize: Dp = 30.dp,
    onClick: () -> Unit = {},
    style: TextStyle = MaterialTheme.typography.titleMedium,
    backgroundColor: Color = MaterialTheme.colorScheme.scrim, // ✅ 추가
    borderColor: Color = MaterialTheme.colorScheme.primaryContainer, // ✅ 추가
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "scale"
    )
    val context = androidx.compose.ui.platform.LocalContext.current

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor, // ✅ 외부에서 지정 가능
        border = BorderStroke(2.dp, borderColor),
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null, // 👈 눌렀을 때 색 효과 완전히 제거
                onClick = {
                    onClick()
                    SfxPlayer.play(context, R.raw.bubble)
                }
            )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            iconResId?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier.size(imageSize)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = text,
                style = style,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        if (showBadge) {
            Box(
                modifier = Modifier
                    .offset(x = 6.dp, y = (-6).dp)
                    .size(16.dp)
                    .background(Color.Red, shape = CircleShape)
                    .border(2.dp, Color.White, shape = CircleShape)
            )
        }
    }

}

@SuppressLint("SuspiciousIndentation")
@Preview(showBackground = true)
@Composable
fun CuteButtonPreview() {

    MainButton(
            text = "Love",
            iconResId = R.drawable.heart,
            showBadge = true,
            onClick = { /* Do something adorable! */ }
        )

}
