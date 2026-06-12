package edu.metrostate.ics342.mediatracker.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import edu.metrostate.ics342.mediatracker.R

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var displayName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(R.drawable.smart_display),
            contentDescription = "Application Icon",
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Join the community"
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = displayName,
            onValueChange = { displayName = it },
            label = { Text("Display Name") }
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") }
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onRegisterSuccess) {
            Text("Sign Up")
        }

        TextButton(onClick = onNavigateToLogin) {
            Text("Already have an account? Log In")
        }
    }
}