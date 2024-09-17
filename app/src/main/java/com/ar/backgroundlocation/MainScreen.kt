package com.ar.backgroundlocation

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * @Author: Abdul Rehman
 * @Date: 06/05/2024.
 */
@Preview
@Composable
fun App() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00ADC7))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header positioned at the top
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp, bottom = 20.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.gps_icon), // Ensure this resource exists
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 10.dp)
            )
            Text(
                text = "GeoTechAmendis",
                fontSize = 24.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }

        // Spacer to push the main content to the center
        Spacer(modifier = Modifier.weight(1f))

        // Main content centered vertically
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo), // Ensure this resource exists
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .width(300.dp)
                    .height(120.dp)
                    .padding(bottom = 50.dp)
            )

            Button(
                onClick = {
                    // Start Service
                    Toast.makeText(context, "Service Start button clicked", Toast.LENGTH_SHORT).show()
                    Intent(context, LocationService::class.java).apply {
                        action = LocationService.ACTION_SERVICE_START
                        context.startService(this)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "CHECK IN",
                    color = Color(0xFF00ADC7),
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    // Stop Service
                    Toast.makeText(context, "Service Stop button clicked", Toast.LENGTH_SHORT).show()
                    Intent(context, LocationService::class.java).apply {
                        action = LocationService.ACTION_SERVICE_STOP
                        context.startService(this)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "CHECK OUT",
                    color = Color(0xFF00ADC7),
                    fontSize = 18.sp
                )
            }
        }

        // Spacer to balance the layout
        Spacer(modifier = Modifier.weight(1f))
    }
}
