package com.example.uni_project.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uni_project.R
import com.example.uni_project.ui.theme.Purple

@Composable
fun CongratulationScreen(
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = stringResource(R.string.name_congrats),
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 100.dp)
            )
            Image(
                painter = painterResource(R.drawable.congrats),
                contentDescription = "Congrats",
                modifier = Modifier
                    .size(250.dp)
                    .padding(top = 150.dp),
            )
            Text(
                text = stringResource(R.string.congrats),
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 50.dp, bottom = 35.dp)
            )
            Text(
                text = stringResource(R.string.congrast_des),
                color = Color.Black.copy(alpha = 0.8f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp, start = 24.dp, end = 24.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(75.dp)
                    .padding(bottom = 20.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(R.string.cont),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

        }


    }
}

@Preview(showBackground = true)
@Composable
fun Congrat(){
    CongratulationScreen (
        onClick = {}
    )
}