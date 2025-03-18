package com.sunarp.mspgu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.*
import com.sunarp.mspgu.ui.fragments.MainScreen
import com.sunarp.mspgu.ui.fragments.SecurityScreen
import com.sunarp.mspgu.ui.fragments.NetworkLocationScreen
import com.sunarp.mspgu.ui.fragments.PermissionsScreen
import com.sunarp.mspgu.ui.fragments.SecureLocationScreen
import com.sunarp.mspgu.ui.fragments.TrackingProtectionScreen
import com.sunarp.mspgu.ui.fragments.NetworkDetectionScreen
import com.sunarp.mspgu.ui.fragments.MaliciousAppsScreen
import com.sunarp.mspgu.ui.fragments.DeviceActivityScreen
import com.sunarp.mspgu.ui.fragments.SecurityAuditScreen
import com.sunarp.mspgu.ui.fragments.FeedbackScreen
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    MainScreen(
                        onNavigateToSecurity = { navController.navigate("security") },
                        onNavigateToNetwork = { navController.navigate("network") },
                        onNavigateToPermissions = { navController.navigate("permissions") },
                        onNavigateToSecureLocation = { navController.navigate("secure_location") },
                        onNavigateToTrackingProtection = { navController.navigate("tracking_protection") },
                        onNavigateToNetworkDetection = { navController.navigate("network_security") },
                        onNavigateToMaliciousApps = { navController.navigate("app_protection") },
                        onNavigateToDeviceActivity = { navController.navigate("device_activity") },
                        onNavigateToSecurityAudit = { navController.navigate("security_audit") },
                        onNavigateToFeedback = { navController.navigate("user_feedback") }
                    )
                }
                composable(route = "security") {
                    SecurityScreen(
                        onNavigateBack = { navController.popBackStack() } // Pasamos la función para regresar
                    )
                }
                composable(route = "network") {
                    NetworkLocationScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(route = "permissions") {
                    PermissionsScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable("secure_location") {
                    SecureLocationScreen(onNavigateBack = { navController.popBackStack() })
                }
                composable("tracking_protection") {
                    TrackingProtectionScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable("network_security") {
                    NetworkDetectionScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable("app_protection") {
                    MaliciousAppsScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable("device_activity") {
                    DeviceActivityScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable("security_audit") {
                    SecurityAuditScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable("user_feedback") {
                    FeedbackScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

            }
        }
    }
}
