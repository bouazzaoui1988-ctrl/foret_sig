package com.example

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ChangeHistory
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.ui.theme.MyApplicationTheme
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer
import org.maplibre.android.annotations.Icon
import org.maplibre.android.annotations.IconFactory
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.annotations.Polyline
import org.maplibre.android.annotations.PolylineOptions
import org.maplibre.android.annotations.Polygon
import org.maplibre.android.annotations.PolygonOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import java.util.UUID

// Data structure representing predefined landmark locations
fun createUserMarkerIcon(context: android.content.Context): Icon {
    val size = 64
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Outer aura shadow (green beacon glow)
    paint.color = android.graphics.Color.parseColor("#4410B981")
    canvas.drawCircle(size / 2f, size / 2f, size * 0.45f, paint)

    // Outer clean white border
    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(size / 2f, size / 2f, size * 0.32f, paint)

    // Inner bright Emerald green core
    paint.color = android.graphics.Color.parseColor("#10B981")
    canvas.drawCircle(size / 2f, size / 2f, size * 0.24f, paint)

    // Tiny white center dot
    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(size / 2f, size / 2f, size * 0.08f, paint)

    val iconFactory = IconFactory.getInstance(context)
    return iconFactory.fromBitmap(bitmap)
}

data class Landmark(
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val iconEmoji: String
)

// Data structure representing user-saved custom map markers
data class CustomMarker(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val category: String, // e.g. "Vacation 🏖️", "Food 🍔", "Work 💼", "Home 🏠"
    val description: String = ""
)

// Data structures for multi-format vector graphics
data class GeoPointData(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
) {
    // Required empty constructor for Firestore
    constructor() : this(0.0, 0.0)
}

data class VectorElement(
    var id: String = "",
    var name: String = "",
    var type: String = "POINT", // "POINT", "LINE", "POLYGON"
    var points: List<GeoPointData> = emptyList(),
    var color: String = "#3B82F6",
    var strokeWidth: Float = 4.0f,
    var category: String = "Vacation 🏖️",
    var description: String = "",
    var creatorEmail: String = "Anonymous",
    var timestamp: Long = System.currentTimeMillis()
) {
    // Required empty constructor for Firestore
    constructor() : this("")
}

// Data structure representing a shared user position
data class SharedPosition(
    val email: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = 0L
) {
    // Required empty constructor for Firestore
    constructor() : this("", 0.0, 0.0, 0L)
}

data class Amodiataire(
    var documentId: String = "",
    var accepted_area: String = "",
    var activity_type: String = "",
    var applicant_name: String = "",
    var applicant_name_french: String = "",
    var assigned_area: String = "",
    var bail_date: String = "",
    var bail_id: String = "",
    var bail_notes: String = "",
    var bail_type: String = "",
    var commune: String = "",
    var correspondences: List<Any> = emptyList(),
    var daira: String? = null,
    var dossier_display_id: String = "",
    var duration: String = "",
    var forest_name: String? = null,
    var id: String = "",
    var ilot: String? = null,
    var last_payment_date: String = "",
    var location: String = "",
    var location_name: String? = null,
    var payment_status: String = "",
    var phone: String = "",
    var reg_amount: String = "",
    var reg_end_date: String = "",
    var reg_start_date: String = "",
    var secondary_activity: String? = null,
    var secondary_activity_pct: String? = null,
    var section: String? = null,
    var start_date: String = "",
    var status: String = "",
    var total_paid: Double = 0.0,
    var x_center: String? = null,
    var y_center: String? = null,
    var yearly_payments: Map<String, Any> = emptyMap()
) {
    constructor() : this("")
}

data class Investor(
    var documentId: String = "",
    var address: String = "",
    var annual_fee: Any? = null,
    var area: String = "",
    var birth_date: String = "",
    var birth_place: String = "",
    var circonscription: String = "",
    var decision_date: String = "",
    var decision_number: String = "",
    var duration: String? = null,
    var file_number: String = "",
    var forest: String? = null,
    var id: String = "",
    var investor_name: String = "",
    var last_payment_date: String = "",
    var location: String = "",
    var national_id: String = "",
    var national_id_date: String = "",
    var national_id_place: String = "",
    var payment_status: String = "",
    var perimetre: String = "",
    var phone: String = "",
    var project_nature: String = "",
    var status: String = "",
    var status_notes: String? = null,
    var total_amount: Any? = null,
    var total_paid: Double = 0.0,
    var x_coord: String = "",
    var y_coord: String = "",
    var yearly_payments: Map<String, Any> = emptyMap()
) {
    constructor() : this("")
}

data class Contrat(
    var documentId: String = "",
    var adresse: String = "",
    var art_imp: String = "",
    var circonscription: String = "",
    var date_convention: String = "",
    var date_lancement: String = "",
    var delai_initial: String = "",
    var effectif: String = "",
    var etat_juridique: String = "",
    var exploitant: String = "",
    var id: String = "",
    var materiel: String = "",
    var montant: String = "",
    var name: String = "",
    var nif: String = "",
    var nis: String = "",
    var rc: String = "",
    var rib: String = "",
    var tel: String = "",
    var type: String = "",
    var fiches_navette: Any? = null,
    var lots: Any? = null,
    var nettoyage: Any? = null,
    var paiements: Any? = null,
    var permis: Any? = null
) {
    constructor() : this("")
}

// Map style configurations utilizing Mapbox vector tiles style JSON with user's access token
data class MapStyleConfig(
    val name: String,
    val description: String,
    val url: String,
    val icon: ImageVector
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Safely initialize Firebase App programmatically if google-services.json is missing
        try {
            val resId = resources.getIdentifier("google_app_id", "string", packageName)
            if (resId != 0 && getString(resId).isNotBlank()) {
                com.google.firebase.FirebaseApp.initializeApp(this)
            } else {
                val prefs = getSharedPreferences("sig_foret_prefs", android.content.Context.MODE_PRIVATE)
                if (!prefs.contains("firebase_project_id") || prefs.getString("firebase_project_id", "") == "firerealevent") {
                    prefs.edit().putString("firebase_project_id", "fireevent-8c15c").apply()
                }
                if (!prefs.contains("firebase_db_url") || prefs.getString("firebase_db_url", "").isNullOrBlank() || prefs.getString("firebase_db_url", "")?.contains("firerealevent") == true || prefs.getString("firebase_db_url", "")?.contains("fireevent-8c15c-default-rtdb.firebaseio.com") == true) {
                    prefs.edit().putString("firebase_db_url", "https://fireevent-8c15c-default-rtdb.europe-west1.firebasedatabase.app").apply()
                }
                val savedApiKey = prefs.getString("firebase_api_key", "")
                val savedProjectId = prefs.getString("firebase_project_id", "fireevent-8c15c") ?: "fireevent-8c15c"
                val savedAppId = prefs.getString("firebase_app_id", "")
                val savedDbUrl = prefs.getString("firebase_db_url", "") ?: ""

                if (!savedApiKey.isNullOrBlank() && !savedProjectId.isNullOrBlank() && !savedAppId.isNullOrBlank()) {
                    val dbUrl = savedDbUrl.ifBlank {
                        if (savedProjectId.trim() == "fireevent-8c15c") {
                            "https://fireevent-8c15c-default-rtdb.europe-west1.firebasedatabase.app"
                        } else {
                            "https://$savedProjectId-default-rtdb.firebaseio.com"
                        }
                    }
                    val options = com.google.firebase.FirebaseOptions.Builder()
                        .setApiKey(savedApiKey)
                        .setProjectId(savedProjectId)
                        .setApplicationId(savedAppId)
                        .setDatabaseUrl(dbUrl)
                        .build()
                    com.google.firebase.FirebaseApp.initializeApp(this, options)
                } else {
                    val options = com.google.firebase.FirebaseOptions.Builder()
                        .setApplicationId("1:123456789012:android:0123456789abcdef012345")
                        .setApiKey("AIzaSyMockKeyForLocalDemoModeOnly1234")
                        .setProjectId("local-demo-project")
                        .build()
                    com.google.firebase.FirebaseApp.initializeApp(this, options)
                }
            }
        } catch (e: Exception) {
            try {
                val options = com.google.firebase.FirebaseOptions.Builder()
                    .setApplicationId("1:123456789012:android:0123456789abcdef012345")
                    .setApiKey("AIzaSyMockKeyForLocalDemoModeOnly1234")
                    .setProjectId("local-demo-project")
                    .build()
                com.google.firebase.FirebaseApp.initializeApp(this, options)
            } catch (eFallback: Exception) {
                eFallback.printStackTrace()
            }
        }

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val context = androidx.compose.ui.platform.LocalContext.current
                val prefs = remember { context.getSharedPreferences("sig_foret_prefs", Context.MODE_PRIVATE) }
                
                var userAuthenticated by androidx.compose.runtime.remember {
                    val initialAuth = try {
                        val firebaseAuthUser = FirebaseAuth.getInstance().currentUser != null
                        val wasAuthenticatedLocally = prefs.getBoolean("user_authenticated", false)
                        firebaseAuthUser || wasAuthenticatedLocally
                    } catch (e: Exception) {
                        val wasAuthenticatedLocally = prefs.getBoolean("user_authenticated", false)
                        wasAuthenticatedLocally
                    }
                    androidx.compose.runtime.mutableStateOf(initialAuth)
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    if (userAuthenticated) {
                        MapExplorerScreen(
                            modifier = Modifier.padding(innerPadding),
                            onSignOut = {
                                prefs.edit().putBoolean("user_authenticated", false).apply()
                                try {
                                    FirebaseAuth.getInstance().signOut()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                userAuthenticated = false
                            }
                        )
                    } else {
                        AuthScreen(
                            onAuthSuccess = {
                                prefs.edit().putBoolean("user_authenticated", true).apply()
                                userAuthenticated = true
                            }
                        )
                    }
                }
            }
        }
    }
}

// Helper function to resolve Mapbox style URLs (including mapbox:// protocols) dynamically
fun resolveStyleUrl(inputUrl: String, token: String): String {
    return if (inputUrl.startsWith("mapbox://styles/")) {
        val path = inputUrl.substringAfter("mapbox://styles/")
        "https://api.mapbox.com/styles/v1/$path?access_token=$token"
    } else {
        inputUrl
    }
}

// Safely retrieve standard Android system location as a fallback if Google Play Services fails or is unavailable
fun getSystemLocationFallback(context: Context): Location? {
    try {
        val fineGranted = androidx.core.content.ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = androidx.core.content.ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!fineGranted && !coarseGranted) {
            return null
        }
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null
        val providers = locationManager.getProviders(true) ?: emptyList()
        var bestLocation: Location? = null
        for (provider in providers) {
            val loc = locationManager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || loc.accuracy < bestLocation.accuracy) {
                bestLocation = loc
            }
        }
        return bestLocation
    } catch (e: SecurityException) {
        // Permission was missing or revoked in the background
    } catch (e: Throwable) {
        // Any other system issue
    }
    return null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapExplorerScreen(
    modifier: Modifier = Modifier,
    onSignOut: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    // Fetch initial Mapbox access token from SharedPreferences or BuildConfig (gitignored .env configuration)
    val initialToken = remember {
        val saved = context.getSharedPreferences("sig_foret_prefs", Context.MODE_PRIVATE)
            .getString("mapbox_access_token", "")
        if (!saved.isNullOrBlank()) {
            saved
        } else {
            val token = BuildConfig.MAPBOX_ACCESS_TOKEN
            if (token.isNullOrBlank() || token == "YOUR_MAPBOX_ACCESS_TOKEN" || token.startsWith("MY_")) "" else token
        }
    }
    
    var mapboxToken by remember { mutableStateOf(initialToken) }

    val isTokenConfigured = remember(mapboxToken) {
        mapboxToken.isNotBlank()
    }

    var userRole by remember {
        mutableStateOf(
            context.getSharedPreferences("sig_foret_prefs", Context.MODE_PRIVATE)
                .getString("user_role", "titulaire") ?: "titulaire"
        )
    }

    // Available map style designs with Mapbox vector tile formats
    val mapStyles = remember(mapboxToken, isTokenConfigured, userRole) {
        if (userRole == "vacataire") {
            listOf(
                MapStyleConfig(
                    name = "OpenStreetMap",
                    description = "Carte routière libre et collaborative.",
                    url = "asset://osm_style.json",
                    icon = Icons.Default.Map
                ),
                MapStyleConfig(
                    name = "Satellite",
                    description = "Imagerie satellite mondiale haute résolution.",
                    url = "asset://satellite_style.json",
                    icon = Icons.Default.Layers
                ),
                MapStyleConfig(
                    name = "Topographique",
                    description = "Carte topographique avec relief.",
                    url = "asset://topo_style.json",
                    icon = Icons.Default.Explore
                )
            )
        } else if (isTokenConfigured) {
            listOf(
                MapStyleConfig(
                    name = "Terrains Amodiables",
                    description = "Visualisation des terrains amodiables.",
                    url = "mapbox://styles/ayyoub1988/clmsdo1ya02a901nzc106b64v",
                    icon = Icons.Default.Map
                ),
                MapStyleConfig(
                    name = "Cartes des Programmes",
                    description = "Cartographie des programmes en cours.",
                    url = "mapbox://styles/ayyoub1988/clmsdupr902aa01nz7m37f21u",
                    icon = Icons.Default.Layers
                ),
                MapStyleConfig(
                    name = "Carte du Cadastre",
                    description = "Consultation des données cadastrales.",
                    url = "mapbox://styles/ayyoub1988/clmsf9ck402fg01r7bial80cz",
                    icon = Icons.Default.Explore
                ),
                MapStyleConfig(
                    name = "Lutte Incendies",
                    description = "Dispositif de lutte contre les incendies - Visualisation tactique.",
                    url = "mapbox://styles/ayyoub1988/cllfmow62014201pmez5y4bbj",
                    icon = Icons.Default.Navigation
                )
            )
        } else {
            listOf(
                MapStyleConfig(
                    name = "Terrains Amodiables",
                    description = "Fonds de plan ouvert & gratuit (OpenFreeMap).",
                    url = "https://config.openfreemap.org/styles/liberty",
                    icon = Icons.Default.Map
                ),
                MapStyleConfig(
                    name = "Cartes des Programmes",
                    description = "Style de carte clair & lisible (OpenFreeMap).",
                    url = "https://config.openfreemap.org/styles/bright",
                    icon = Icons.Default.Layers
                ),
                MapStyleConfig(
                    name = "Carte du Cadastre",
                    description = "Style minimaliste clair (OpenFreeMap).",
                    url = "https://config.openfreemap.org/styles/positron",
                    icon = Icons.Default.Explore
                )
            )
        }
    }

    // Screen and map interactive states
    var selectedStyle by remember(mapStyles) { mutableStateOf(mapStyles[0]) }
    var currentAppliedStyleUrl by remember { mutableStateOf("") }
    var centerLat by remember { mutableStateOf(35.0348) } // Initial center
    var centerLon by remember { mutableStateOf(-0.6336) }
    var currentZoom by remember { mutableStateOf(10.0) }
    var isMapLoading by remember { mutableStateOf(true) }
    var showSidebar by remember { mutableStateOf(false) }

    // Dialog state for adding a custom marker
    var showAddMarkerDialog by remember { mutableStateOf(false) }
    var customMarkerName by remember { mutableStateOf("") }
    var customMarkerCategory by remember { mutableStateOf("Plantation forestière 🌲") }

    // Manual fly-to coordinates state
    var showFlyToDialog by remember { mutableStateOf(false) }
    var inputLat by remember { mutableStateOf("") }
    var inputLon by remember { mutableStateOf("") }

    // List of active user custom markers
    val customMarkers = remember {
        mutableStateListOf<CustomMarker>()
    }

    // --- CLOUD FIRESTORE DATABASES & VECTOR DESIGN MODES ---
    var firebaseInitTrigger by remember { mutableStateOf(0) }
    
    // Real-time location sharing states
    var isSharingLocation by remember { mutableStateOf(false) }
    val firestorePositions = remember { mutableStateListOf<SharedPosition>() }
    val rtdbPositions = remember { mutableStateListOf<SharedPosition>() }
    val localPositions = remember { mutableStateListOf<SharedPosition>() }
    val sharedPositions = remember {
        derivedStateOf {
            val mergedMap = mutableMapOf<String, SharedPosition>()
            for (pos in rtdbPositions) {
                mergedMap[pos.email] = pos
            }
            for (pos in firestorePositions) {
                mergedMap[pos.email] = pos
            }
            for (pos in localPositions) {
                mergedMap[pos.email] = pos
            }
            mergedMap.values.toList()
        }
    }

    val db = remember(firebaseInitTrigger) {
        try {
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            null
        }
    }

    val rtdb = remember(firebaseInitTrigger) {
        try {
            com.google.firebase.database.FirebaseDatabase.getInstance()
        } catch (e: Exception) {
            null
        }
    }

    val firebaseService = remember(db, rtdb) {
        FirebaseService(db, rtdb)
    }

    // Live synced Firestore vector database elements
    val firestoreElements = remember { mutableStateListOf<VectorElement>() }
    val rtdbElements = remember { mutableStateListOf<VectorElement>() }
    val vectorElements = remember { mutableStateListOf<VectorElement>() }

    LaunchedEffect(firestoreElements.toList(), rtdbElements.toList()) {
        val mergedMap = mutableMapOf<String, VectorElement>()
        for (el in rtdbElements) {
            mergedMap[el.id] = el
        }
        for (el in firestoreElements) {
            mergedMap[el.id] = el
        }
        vectorElements.clear()
        vectorElements.addAll(mergedMap.values)
    }

    fun parseStr(v: Any?): String {
        return when (v) {
            null -> ""
            is String -> v
            is Number -> v.toString()
            is Boolean -> v.toString()
            else -> v.toString()
        }
    }

    fun parseDbl(v: Any?): Double {
        return when (v) {
            null -> 0.0
            is Number -> v.toDouble()
            is String -> v.replace(",", ".").toDoubleOrNull() ?: 0.0
            else -> 0.0
        }
    }

    fun getAny(doc: com.google.firebase.firestore.DocumentSnapshot, vararg keys: String): Any? {
        for (k in keys) {
            val v = doc.get(k)
            if (v != null) return v
        }
        return null
    }

    // Live synced Firestore amodiataires collection
    val amodiatairesList = remember { mutableStateListOf<Amodiataire>() }
    var isRefreshingAmodiataires by remember { mutableStateOf(false) }
    var showAmodiatairesDialog by remember { mutableStateOf(false) }

    fun refreshAmodiataires() {
        isRefreshingAmodiataires = true
        if (db != null) {
            db.collection("amodiataires").get()
                .addOnCompleteListener { task ->
                    isRefreshingAmodiataires = false
                    if (task.isSuccessful && task.result != null) {
                        val list = mutableListOf<Amodiataire>()
                        for (doc in task.result.documents) {
                            try {
                                val amod = Amodiataire(
                                    documentId = doc.id,
                                    accepted_area = parseStr(doc.get("accepted_area")),
                                    activity_type = parseStr(doc.get("activity_type")),
                                    applicant_name = parseStr(doc.get("applicant_name")),
                                    applicant_name_french = parseStr(doc.get("applicant_name_french")),
                                    assigned_area = parseStr(doc.get("assigned_area")),
                                    bail_date = parseStr(doc.get("bail_date")),
                                    bail_id = parseStr(doc.get("bail_id")),
                                    bail_notes = parseStr(doc.get("bail_notes")),
                                    bail_type = parseStr(doc.get("bail_type")),
                                    commune = parseStr(doc.get("commune")),
                                    correspondences = (doc.get("correspondences") as? List<*>)?.filterNotNull() ?: emptyList(),
                                    daira = doc.get("daira")?.let { parseStr(it) },
                                    dossier_display_id = parseStr(doc.get("dossier_display_id")),
                                    duration = parseStr(doc.get("duration")),
                                    forest_name = doc.get("forest_name")?.let { parseStr(it) },
                                    id = parseStr(doc.get("id")),
                                    ilot = doc.get("ilot")?.let { parseStr(it) },
                                    last_payment_date = parseStr(doc.get("last_payment_date")),
                                    location = parseStr(doc.get("location")),
                                    location_name = doc.get("location_name")?.let { parseStr(it) },
                                    payment_status = parseStr(doc.get("payment_status")),
                                    phone = parseStr(doc.get("phone")),
                                    reg_amount = parseStr(doc.get("reg_amount")),
                                    reg_end_date = parseStr(doc.get("reg_end_date")),
                                    reg_start_date = parseStr(doc.get("reg_start_date")),
                                    secondary_activity = doc.get("secondary_activity")?.let { parseStr(it) },
                                    secondary_activity_pct = doc.get("secondary_activity_pct")?.let { parseStr(it) },
                                    section = doc.get("section")?.let { parseStr(it) },
                                    start_date = parseStr(doc.get("start_date")),
                                    status = parseStr(doc.get("status")),
                                    total_paid = parseDbl(doc.get("total_paid")),
                                    x_center = doc.get("x_center")?.let { parseStr(it) },
                                    y_center = doc.get("y_center")?.let { parseStr(it) },
                                    yearly_payments = (doc.get("yearly_payments") as? Map<*, *>)?.entries?.associate { it.key.toString() to (it.value ?: "") } ?: emptyMap()
                                )
                                list.add(amod)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        amodiatairesList.clear()
                        amodiatairesList.addAll(list)
                        Toast.makeText(context, "${list.size} amodiataires actualisés depuis Firestore 🌲", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Erreur Firestore: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
        } else {
            isRefreshingAmodiataires = false
            Toast.makeText(context, "Firestore non initialisé", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(db) {
        if (db != null && amodiatairesList.isEmpty()) {
            refreshAmodiataires()
        }
    }

    // Live synced Firestore investors collection
    val investorsList = remember { mutableStateListOf<Investor>() }
    var isRefreshingInvestors by remember { mutableStateOf(false) }
    var showInvestorsDialog by remember { mutableStateOf(false) }

    fun refreshInvestors() {
        isRefreshingInvestors = true
        if (db != null) {
            db.collection("investors").get()
                .addOnCompleteListener { task ->
                    isRefreshingInvestors = false
                    if (task.isSuccessful && task.result != null) {
                        val list = mutableListOf<Investor>()
                        for (doc in task.result.documents) {
                            try {
                                val inv = Investor(
                                    documentId = doc.id,
                                    address = parseStr(doc.get("address")),
                                    annual_fee = doc.get("annual_fee"),
                                    area = parseStr(doc.get("area")),
                                    birth_date = parseStr(doc.get("birth_date")),
                                    birth_place = parseStr(doc.get("birth_place")),
                                    circonscription = parseStr(doc.get("circonscription")),
                                    decision_date = parseStr(doc.get("decision_date")),
                                    decision_number = parseStr(doc.get("decision_number")),
                                    duration = doc.get("duration")?.let { parseStr(it) },
                                    file_number = parseStr(doc.get("file_number")),
                                    forest = doc.get("forest")?.let { parseStr(it) },
                                    id = parseStr(doc.get("id")),
                                    investor_name = parseStr(doc.get("investor_name")),
                                    last_payment_date = parseStr(doc.get("last_payment_date")),
                                    location = parseStr(doc.get("location")),
                                    national_id = parseStr(doc.get("national_id")),
                                    national_id_date = parseStr(doc.get("national_id_date")),
                                    national_id_place = parseStr(doc.get("national_id_place")),
                                    payment_status = parseStr(doc.get("payment_status")),
                                    perimetre = parseStr(doc.get("perimetre")),
                                    phone = parseStr(doc.get("phone")),
                                    project_nature = parseStr(doc.get("project_nature")),
                                    status = parseStr(doc.get("status")),
                                    status_notes = doc.get("status_notes")?.let { parseStr(it) },
                                    total_amount = doc.get("total_amount"),
                                    total_paid = parseDbl(doc.get("total_paid")),
                                    x_coord = parseStr(doc.get("x_coord")),
                                    y_coord = parseStr(doc.get("y_coord")),
                                    yearly_payments = (doc.get("yearly_payments") as? Map<*, *>)?.entries?.associate { it.key.toString() to (it.value ?: "") } ?: emptyMap()
                                )
                                list.add(inv)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        investorsList.clear()
                        investorsList.addAll(list)
                        Toast.makeText(context, "${list.size} investisseurs actualisés depuis Firestore 💼", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Erreur Firestore: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
        } else {
            isRefreshingInvestors = false
            Toast.makeText(context, "Firestore non initialisé", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(db) {
        if (db != null && investorsList.isEmpty()) {
            refreshInvestors()
        }
    }

    // Live synced Firestore contrats collection
    val contratsList = remember { mutableStateListOf<Contrat>() }
    var isRefreshingContrats by remember { mutableStateOf(false) }
    var showContratsDialog by remember { mutableStateOf(false) }

    fun refreshContrats() {
        isRefreshingContrats = true
        if (db != null) {
            db.collection("contrats").get()
                .addOnCompleteListener { task ->
                    isRefreshingContrats = false
                    if (task.isSuccessful && task.result != null) {
                        val list = mutableListOf<Contrat>()
                        for (doc in task.result.documents) {
                            try {
                                val c = Contrat(
                                    documentId = doc.id,
                                    adresse = parseStr(getAny(doc, "adresse", "address")),
                                    art_imp = parseStr(getAny(doc, "art_imp")),
                                    circonscription = parseStr(getAny(doc, "circonscription")),
                                    date_convention = parseStr(getAny(doc, "date_convention")),
                                    date_lancement = parseStr(getAny(doc, "date_lancement")),
                                    delai_initial = parseStr(getAny(doc, "delai_initial")),
                                    effectif = parseStr(getAny(doc, "effectif")),
                                    etat_juridique = parseStr(getAny(doc, "etat_juridique")),
                                    exploitant = parseStr(getAny(doc, "exploitant", "beneficiary", "beneficiaire")),
                                    id = parseStr(getAny(doc, "id")),
                                    materiel = parseStr(getAny(doc, "materiel")),
                                    montant = parseStr(getAny(doc, "montant")),
                                    name = parseStr(getAny(doc, "name")),
                                    nif = parseStr(getAny(doc, "nif")),
                                    nis = parseStr(getAny(doc, "nis")),
                                    rc = parseStr(getAny(doc, "rc")),
                                    rib = parseStr(getAny(doc, "rib")),
                                    tel = parseStr(getAny(doc, "tel", "phone", "telephone")),
                                    type = parseStr(getAny(doc, "type")),
                                    fiches_navette = doc.get("fiches_navette"),
                                    lots = doc.get("lots"),
                                    nettoyage = doc.get("nettoyage"),
                                    paiements = doc.get("paiements"),
                                    permis = doc.get("permis")
                                )
                                list.add(c)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        contratsList.clear()
                        contratsList.addAll(list)
                        Toast.makeText(context, "${list.size} contrats actualisés depuis Firestore 📜", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Erreur Firestore (Contrats): ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
        } else {
            isRefreshingContrats = false
            Toast.makeText(context, "Firestore non initialisé", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(db) {
        if (db != null && contratsList.isEmpty()) {
            refreshContrats()
        }
    }
    
    // Load local fallback elements from SharedPreferences using Moshi
    val localFallbackElements = remember {
        val list = mutableStateListOf<VectorElement>()
        try {
            val prefs = context.getSharedPreferences("sig_foret_prefs", android.content.Context.MODE_PRIVATE)
            val json = prefs.getString("local_vector_elements_json", "")
            if (!json.isNullOrBlank()) {
                val moshi = com.squareup.moshi.Moshi.Builder()
                    .addLast(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
                    .build()
                val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, VectorElement::class.java)
                val adapter = moshi.adapter<List<VectorElement>>(listType)
                val savedList = adapter.fromJson(json)
                if (savedList != null) {
                    list.addAll(savedList)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        list
    }

    val saveLocalFallbackElements = {
        try {
            val moshi = com.squareup.moshi.Moshi.Builder()
                .addLast(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
                .build()
            val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, VectorElement::class.java)
            val adapter = moshi.adapter<List<VectorElement>>(listType)
            val json = adapter.toJson(localFallbackElements.toList())
            context.getSharedPreferences("sig_foret_prefs", android.content.Context.MODE_PRIVATE)
                .edit()
                .putString("local_vector_elements_json", json)
                .apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Check if Firebase Auth is logged in to get user email
    val auth = remember(firebaseInitTrigger) {
        try {
            com.google.firebase.auth.FirebaseAuth.getInstance()
        } catch (e: Exception) {
            null
        }
    }
    val currentUserEmail = remember(context, firebaseInitTrigger) {
        val authEmail = auth?.currentUser?.email
        if (!authEmail.isNullOrBlank()) {
            authEmail
        } else {
            val prefs = context.getSharedPreferences("sig_foret_prefs", android.content.Context.MODE_PRIVATE)
            prefs.getString("authenticated_user_email", "bouazzaoui1988@gmail.com") ?: "bouazzaoui1988@gmail.com"
        }
    }

    // Real-time Firestore sync listener
    DisposableEffect(db) {
        if (db == null) {
            onDispose {}
        } else {
            val listener = db.collection("vector_elements")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        error.printStackTrace()
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val list = mutableListOf<VectorElement>()
                        for (doc in snapshot.documents) {
                            try {
                                val el = doc.toObject(VectorElement::class.java)
                                if (el != null) {
                                    list.add(el.copy(id = doc.id))
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        firestoreElements.clear()
                        firestoreElements.addAll(list)
                    }
                }
            onDispose {
                listener.remove()
            }
        }
    }

    // Real-time shared positions listener
    DisposableEffect(db, firebaseInitTrigger) {
        if (db == null) {
            onDispose {}
        } else {
            val listener = db.collection("shared_positions")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        error.printStackTrace()
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val list = mutableListOf<SharedPosition>()
                        for (doc in snapshot.documents) {
                            try {
                                val emailVal = doc.getString("email") ?: ""
                                val latVal = doc.getDouble("latitude") ?: 0.0
                                val lngVal = doc.getDouble("longitude") ?: 0.0
                                val tsVal = doc.getLong("timestamp") ?: 0L
                                if (emailVal.isNotBlank()) {
                                    list.add(SharedPosition(emailVal, latVal, lngVal, tsVal))
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        firestorePositions.clear()
                        firestorePositions.addAll(list)
                    }
                }
            onDispose {
                listener.remove()
            }
        }
    }

    // Real-time Realtime Database sync listener
    DisposableEffect(rtdb, firebaseInitTrigger) {
        if (rtdb == null) {
            onDispose {}
        } else {
            val ref = rtdb.getReference("vector_elements")
            val listener = object : com.google.firebase.database.ValueEventListener {
                override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                    val list = mutableListOf<VectorElement>()
                    for (child in snapshot.children) {
                        try {
                            val el = child.getValue(VectorElement::class.java)
                            if (el != null) {
                                list.add(el.copy(id = child.key ?: ""))
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    rtdbElements.clear()
                    rtdbElements.addAll(list)
                }
                override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                    error.toException().printStackTrace()
                }
            }
            ref.addValueEventListener(listener)
            onDispose {
                ref.removeEventListener(listener)
            }
        }
    }

    // Real-time Realtime Database shared positions listener
    DisposableEffect(rtdb, firebaseInitTrigger) {
        if (rtdb == null) {
            onDispose {}
        } else {
            val ref = rtdb.getReference("shared_positions")
            val listener = object : com.google.firebase.database.ValueEventListener {
                override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                    val list = mutableListOf<SharedPosition>()
                    for (child in snapshot.children) {
                        try {
                            val emailVal = child.child("email").getValue(String::class.java) ?: ""
                            val latVal = child.child("latitude").getValue(Double::class.java) ?: 0.0
                            val lngVal = child.child("longitude").getValue(Double::class.java) ?: 0.0
                            val tsVal = child.child("timestamp").getValue(Long::class.java) ?: 0L
                            if (emailVal.isNotBlank()) {
                                list.add(SharedPosition(emailVal, latVal, lngVal, tsVal))
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    rtdbPositions.clear()
                    rtdbPositions.addAll(list)
                }
                override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                    error.toException().printStackTrace()
                }
            }
            ref.addValueEventListener(listener)
            onDispose {
                ref.removeEventListener(listener)
            }
        }
    }

    // Drawing states for Lines and Polygons
    var drawingMode by remember { mutableStateOf<String?>(null) } // null, "LINE", "POLYGON"
    val activeDrawingPoints = remember { mutableStateListOf<LatLng>() }
    var showSaveVectorDialog by remember { mutableStateOf(false) }
    var vectorNameInput by remember { mutableStateOf("") }
    var vectorDescriptionInput by remember { mutableStateOf("") }
    var vectorColorInput by remember { mutableStateOf("#3B82F6") } // Hex Blue
    var vectorCategoryInput by remember { mutableStateOf("Plantation forestière 🌲") }

    var showVectorListDialog by remember { mutableStateOf(false) }
    var pendingKmlToSave by remember { mutableStateOf<String?>(null) }
    var pendingKmlFileName by remember { mutableStateOf("") }

    val createDocLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/vnd.google-earth.kml+xml")
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    outputStream.write(pendingKmlToSave?.toByteArray() ?: ByteArray(0))
                }
                Toast.makeText(context, "Fichier KML exporté avec succès ! 💾", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Erreur d'exportation: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Keep reference to the raw MapLibreMap instance to allow runtime controller animations
    var mapController by remember { mutableStateOf<MapLibreMap?>(null) }
    var isDrawingMenuOpen by remember { mutableStateOf(false) }
    val mapMarkersReference = remember { mutableStateListOf<Marker>() }

    // Helper functions for common map navigation controls
    val flyToLocation = { lat: Double, lon: Double, zoom: Double ->
        mapController?.let { controller ->
            controller.animateCamera(
                CameraUpdateFactory.newLatLngZoom(LatLng(lat, lon), zoom),
                1800
            )
            centerLat = lat
            centerLon = lon
            currentZoom = zoom
        }
    }

    val adjustZoom = { isZoomIn: Boolean ->
        mapController?.let { controller ->
            val targetZoom = if (isZoomIn) currentZoom + 1.2 else currentZoom - 1.2
            controller.animateCamera(CameraUpdateFactory.zoomTo(targetZoom), 500)
            currentZoom = targetZoom
        }
    }

    val resetCameraOrientation = {
        mapController?.let { controller ->
            controller.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder()
                        .bearing(0.0)
                        .tilt(0.0)
                        .build()
                ),
                1000
            )
        }
    }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // GPS Position telemetry and active inspection states
    var activeGpsLocation by remember { mutableStateOf<Location?>(null) }
    var isContinuousTracking by remember { mutableStateOf(false) }
    var isLocationCallbackRegistered by remember { mutableStateOf(false) }
    var isLocationTrackingPausedByLifecycle by remember { mutableStateOf(false) }
    var isTelemetryPanelExpanded by remember { mutableStateOf(true) }
    var finePermissionGranted by remember {
        mutableStateOf(
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }
    var coarsePermissionGranted by remember {
        mutableStateOf(
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val retrieveLocationAndFly = {
        try {
            val fineGranted = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            val coarseGranted = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            finePermissionGranted = fineGranted
            coarsePermissionGranted = coarseGranted

            if (fineGranted || coarseGranted) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        activeGpsLocation = location
                        flyToLocation(location.latitude, location.longitude, 15.0)
                        Toast.makeText(context, "Centré sur votre position GPS 📍", Toast.LENGTH_SHORT).show()
                    } else {
                        // Try fallback
                        val fallback = getSystemLocationFallback(context)
                        if (fallback != null) {
                            activeGpsLocation = fallback
                            flyToLocation(fallback.latitude, fallback.longitude, 15.0)
                            Toast.makeText(context, "Centré sur votre position (Réseau/GPS système) 📍", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Position GPS non disponible. Assurez-vous que la localisation de votre appareil est activée.", Toast.LENGTH_LONG).show()
                        }
                    }
                }.addOnFailureListener {
                    val fallback = getSystemLocationFallback(context)
                    if (fallback != null) {
                        activeGpsLocation = fallback
                        flyToLocation(fallback.latitude, fallback.longitude, 15.0)
                        Toast.makeText(context, "Centré sur votre position (Réseau/GPS système) 📍", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Erreur de localisation: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                val fallback = getSystemLocationFallback(context)
                if (fallback != null) {
                    activeGpsLocation = fallback
                    flyToLocation(fallback.latitude, fallback.longitude, 15.0)
                    Toast.makeText(context, "Centré sur votre position (Système) 📍", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Veuillez accorder la permission de localisation.", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: SecurityException) {
            val fallback = getSystemLocationFallback(context)
            if (fallback != null) {
                activeGpsLocation = fallback
                flyToLocation(fallback.latitude, fallback.longitude, 15.0)
                Toast.makeText(context, "Centré sur votre position (Système) 📍", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Erreur de permission de localisation: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Throwable) {
            val fallback = getSystemLocationFallback(context)
            if (fallback != null) {
                activeGpsLocation = fallback
                flyToLocation(fallback.latitude, fallback.longitude, 15.0)
                Toast.makeText(context, "Centré sur votre position (Système) 📍", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Erreur lors de la récupération de la localisation.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val locationPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            finePermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            coarsePermissionGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            val granted = permissions.values.any { it }
            if (granted) {
                retrieveLocationAndFly()
            } else {
                Toast.makeText(context, "Permission de localisation refusée", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val locateUser = {
        try {
            val fineGranted = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            val coarseGranted = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            finePermissionGranted = fineGranted
            coarsePermissionGranted = coarseGranted

            if (fineGranted || coarseGranted) {
                retrieveLocationAndFly()
            } else {
                locationPermissionsLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        } catch (e: Throwable) {
            Toast.makeText(context, "Impossible de demander la permission de localisation: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    val locationCallback = remember {
        object : com.google.android.gms.location.LocationCallback() {
            override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                val lastLoc = locationResult.lastLocation
                if (lastLoc != null) {
                    activeGpsLocation = lastLoc
                    if (isSharingLocation) {
                        if (db != null || rtdb != null) {
                            firebaseService.saveSharedPosition(currentUserEmail, lastLoc)
                        } else {
                            val existingIndex = localPositions.indexOfFirst { it.email == currentUserEmail }
                            val updatedObj = SharedPosition(currentUserEmail, lastLoc.latitude, lastLoc.longitude, System.currentTimeMillis())
                            if (existingIndex >= 0) {
                                localPositions[existingIndex] = updatedObj
                            } else {
                                localPositions.add(updatedObj)
                            }
                        }
                    }
                }
            }
        }
    }

    val pauseLocationUpdates = {
        if (isContinuousTracking && isLocationCallbackRegistered) {
            try {
                fusedLocationClient.removeLocationUpdates(locationCallback)
                isLocationCallbackRegistered = false
                isLocationTrackingPausedByLifecycle = true
                android.util.Log.d("MainActivity", "Location updates paused by lifecycle")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val resumeLocationUpdates = {
        if (isLocationTrackingPausedByLifecycle) {
            try {
                val fineGranted = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                val coarseGranted = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                if (fineGranted || coarseGranted) {
                    val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
                        com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                        3000L
                    ).setMinUpdateIntervalMillis(3000L).build()

                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        android.os.Looper.getMainLooper()
                    )
                    isLocationCallbackRegistered = true
                    android.util.Log.d("MainActivity", "Location updates resumed by lifecycle")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            isLocationTrackingPausedByLifecycle = false
        }
    }

    var isRefreshingVectors by remember { mutableStateOf(false) }

    fun refreshVectorElements() {
        isRefreshingVectors = true
        var pendingCount = 0
        if (db != null) pendingCount++
        if (rtdb != null) pendingCount++
        
        val decrementAndCheck = {
            pendingCount--
            if (pendingCount <= 0) {
                isRefreshingVectors = false
                Toast.makeText(context, "Données vectorielles actualisées avec succès 🔄", Toast.LENGTH_SHORT).show()
            }
        }
        
        if (db != null) {
            db.collection("vector_elements").get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        val list = mutableListOf<VectorElement>()
                        for (doc in task.result.documents) {
                            try {
                                val el = doc.toObject(VectorElement::class.java)
                                if (el != null) {
                                    list.add(el.copy(id = doc.id))
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        firestoreElements.clear()
                        firestoreElements.addAll(list)
                    }
                    decrementAndCheck()
                }
        }
        
        if (rtdb != null) {
            rtdb.getReference("vector_elements").get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        val list = mutableListOf<VectorElement>()
                        for (child in task.result.children) {
                            try {
                                val el = child.getValue(VectorElement::class.java)
                                if (el != null) {
                                    list.add(el.copy(id = child.key ?: ""))
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        rtdbElements.clear()
                        rtdbElements.addAll(list)
                    }
                    decrementAndCheck()
                }
        }
        
        if (pendingCount == 0) {
            isRefreshingVectors = false
            Toast.makeText(context, "Aucune base de données active pour l'actualisation", Toast.LENGTH_SHORT).show()
        }
    }

    fun startContinuousTracking() {
        try {
            val fineGranted = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            val coarseGranted = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            
            finePermissionGranted = fineGranted
            coarsePermissionGranted = coarseGranted

            if (fineGranted || coarseGranted) {
                val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
                    com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                    3000L
                ).setMinUpdateIntervalMillis(3000L).build()

                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    android.os.Looper.getMainLooper()
                )
                isLocationCallbackRegistered = true
                isContinuousTracking = true
                Toast.makeText(context, "Suivi GPS continu activé 📡", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Permission requise pour le suivi continu", Toast.LENGTH_SHORT).show()
            }
        } catch (e: SecurityException) {
            Toast.makeText(context, "Permission refusée: ${e.message}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Erreur lors du démarrage du suivi: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun stopContinuousTracking() {
        try {
            if (isLocationCallbackRegistered) {
                fusedLocationClient.removeLocationUpdates(locationCallback)
                isLocationCallbackRegistered = false
            }
            isContinuousTracking = false
            isLocationTrackingPausedByLifecycle = false
            Toast.makeText(context, "Suivi GPS continu arrêté 🛑", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toggleShareLocation() {
        val fineGranted = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        
        finePermissionGranted = fineGranted
        coarsePermissionGranted = coarseGranted
        
        if (!fineGranted && !coarseGranted) {
            locationPermissionsLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            Toast.makeText(context, "Veuillez accorder la permission de localisation pour partager votre position", Toast.LENGTH_LONG).show()
            return
        }

        val nextState = !isSharingLocation
        isSharingLocation = nextState
        if (nextState) {
            startContinuousTracking()
            activeGpsLocation?.let { lastLoc ->
                if (db != null || rtdb != null) {
                    val currentProjectId = try {
                        com.google.firebase.FirebaseApp.getInstance().options.projectId
                    } catch (e: Exception) { "" }
                    
                    if (currentProjectId == "local-demo-project") {
                        Toast.makeText(context, "Mode Démo : Position simulée en local (google-services.json manquant)", Toast.LENGTH_LONG).show()
                    }
                    firebaseService.saveSharedPosition(currentUserEmail, lastLoc)
                } else {
                    val existingIndex = localPositions.indexOfFirst { it.email == currentUserEmail }
                    val updatedObj = SharedPosition(currentUserEmail, lastLoc.latitude, lastLoc.longitude, System.currentTimeMillis())
                    if (existingIndex >= 0) {
                        localPositions[existingIndex] = updatedObj
                    } else {
                        localPositions.add(updatedObj)
                    }
                }
            }
            Toast.makeText(context, "Partage de position activé 🟢", Toast.LENGTH_SHORT).show()
        } else {
            stopContinuousTracking()
            if (db != null || rtdb != null) {
                firebaseService.deleteSharedPosition(currentUserEmail)
            } else {
                localPositions.removeAll { it.email == currentUserEmail }
            }
            Toast.makeText(context, "Partage de position désactivé 🛑", Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                if (isLocationCallbackRegistered) {
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                    isLocationCallbackRegistered = false
                }
                if ((db != null || rtdb != null) && isSharingLocation) {
                    firebaseService.deleteSharedPosition(currentUserEmail)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }




    val toggle3DTilt = {
        mapController?.let { controller ->
            val currentTilt = controller.cameraPosition.tilt
            val newTilt = if (currentTilt > 20) 0.0 else 55.0
            val newBearing = if (currentTilt > 20) 0.0 else 45.0
            controller.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder()
                        .tilt(newTilt)
                        .bearing(newBearing)
                        .build()
                ),
                1000
            )
        }
    }

    // Build the master screen layout
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- BASE MAP VIEW CONTAINER ---
        Box(modifier = Modifier.fillMaxSize()) {
            val mapView = remember(mapboxToken, isTokenConfigured) {
                if (isTokenConfigured) {
                    MapLibre.getInstance(context, mapboxToken, WellKnownTileServer.Mapbox)
                } else {
                    MapLibre.getInstance(context)
                }
                MapView(context)
            }

            val lifecycleObserver = remember(mapView) {
                LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_CREATE -> mapView.onCreate(null)
                        Lifecycle.Event.ON_START -> mapView.onStart()
                        Lifecycle.Event.ON_RESUME -> {
                            mapView.onResume()
                            resumeLocationUpdates()
                        }
                        Lifecycle.Event.ON_PAUSE -> {
                            pauseLocationUpdates()
                            mapView.onPause()
                        }
                        Lifecycle.Event.ON_STOP -> {
                            pauseLocationUpdates()
                            mapView.onStop()
                        }
                        Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                        else -> {}
                    }
                }
            }

            DisposableEffect(lifecycle, mapView) {
                lifecycle.addObserver(lifecycleObserver)
                onDispose {
                    lifecycle.removeObserver(lifecycleObserver)
                }
            }

            AndroidView(
                factory = {
                    mapView.apply {
                        getMapAsync { mapboxMap ->
                            mapController = mapboxMap
                            isMapLoading = false
                            
                            // Set custom InfoWindowAdapter for clean, small, beautiful card with small characters
                            mapboxMap.setInfoWindowAdapter { marker ->
                                val ctx = mapView.context
                                val density = ctx.resources.displayMetrics.density
                                val container = android.widget.LinearLayout(ctx).apply {
                                    orientation = android.widget.LinearLayout.VERTICAL
                                    val padLeftRight = (12 * density).toInt()
                                    val padTopBottom = (8 * density).toInt()
                                    setPadding(padLeftRight, padTopBottom, padLeftRight, padTopBottom)
                                    background = android.graphics.drawable.GradientDrawable().apply {
                                        setColor(android.graphics.Color.parseColor("#E6121214")) // Elegant dark high-contrast theme
                                        cornerRadius = 8 * density
                                        setStroke((1 * density).toInt(), android.graphics.Color.parseColor("#3F3F46"))
                                    }
                                }
                                
                                val titleTxt = android.widget.TextView(ctx).apply {
                                    text = marker.title
                                    setTextColor(android.graphics.Color.WHITE)
                                    textSize = 11f // Nice small text size
                                    setTypeface(null, android.graphics.Typeface.BOLD)
                                    ellipsize = android.text.TextUtils.TruncateAt.END
                                    maxLines = 1
                                }
                                container.addView(titleTxt)
                                
                                // Try to match real-time positions
                                val matchedSp = sharedPositions.value.find { it.email.trim().lowercase() == marker.title.trim().lowercase() }
                                val dateStr = if (matchedSp != null) {
                                    try {
                                        java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(matchedSp.timestamp))
                                    } catch (e: Exception) {
                                        ""
                                    }
                                } else {
                                    ""
                                }
                                
                                if (dateStr.isNotEmpty()) {
                                    val subTxt = android.widget.TextView(ctx).apply {
                                        text = dateStr
                                        setTextColor(android.graphics.Color.parseColor("#A1A1AA"))
                                        textSize = 9.5f // Extremely crisp small subtitle
                                        setPadding(0, (2 * density).toInt(), 0, 0)
                                    }
                                    container.addView(subTxt)
                                } else {
                                    marker.snippet?.let { snip ->
                                        if (snip.isNotBlank()) {
                                            val subTxt = android.widget.TextView(ctx).apply {
                                                text = snip
                                                setTextColor(android.graphics.Color.parseColor("#A1A1AA"))
                                                textSize = 9.5f
                                                setPadding(0, (2 * density).toInt(), 0, 0)
                                            }
                                            container.addView(subTxt)
                                        }
                                    }
                                }
                                container
                            }

                            // Load selected style
                            val resolvedUrl = resolveStyleUrl(selectedStyle.url, mapboxToken)
                            mapboxMap.setStyle(resolvedUrl) { style ->
                                // Style loaded successfully
                            }
                            currentAppliedStyleUrl = selectedStyle.url

                            // Center on custom initial coordinates
                            mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(centerLat, centerLon), currentZoom))

                            // Bind camera change listeners to synchronize UI coordinate telemetry card in real-time
                            mapboxMap.addOnCameraMoveListener {
                                val target = mapboxMap.cameraPosition.target
                                if (target != null) {
                                    centerLat = target.latitude
                                    centerLon = target.longitude
                                }
                                currentZoom = mapboxMap.cameraPosition.zoom
                            }

                            // Tap on screen to create a point or add a vertex for a line or polygon
                            mapboxMap.addOnMapClickListener { latLng ->
                                if (drawingMode == "POINT") {
                                    centerLat = latLng.latitude
                                    centerLon = latLng.longitude
                                    showAddMarkerDialog = true
                                    drawingMode = null
                                    true
                                } else if (drawingMode == "LINE" || drawingMode == "POLYGON") {
                                    activeDrawingPoints.add(latLng)
                                    Toast.makeText(context, "Point ajouté à la géométrie 📍", Toast.LENGTH_SHORT).show()
                                    true
                                } else {
                                    false
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { view ->
                    // Update MapLibre Engine token dynamically if needed
                    if (isTokenConfigured) {
                        MapLibre.getInstance(view.context, mapboxToken, WellKnownTileServer.Mapbox)
                    } else {
                        MapLibre.getInstance(view.context)
                    }

                    // Update the style URL if user changes style configuration safely via tracking state
                    mapController?.let { mapboxMap ->
                        if (currentAppliedStyleUrl != selectedStyle.url) {
                            val resolvedUrl = resolveStyleUrl(selectedStyle.url, mapboxToken)
                            mapboxMap.setStyle(resolvedUrl)
                            currentAppliedStyleUrl = selectedStyle.url
                        }

                        // Clear past markers, polylines, and polygons from the map
                        mapboxMap.clear()

                        // Draw each vector element
                        val currentElements = (vectorElements + localFallbackElements).distinctBy { it.id }
                        currentElements.forEach { element ->
                            when (element.type) {
                                "POINT" -> {
                                    if (element.points.isNotEmpty()) {
                                        val pt = element.points.first()
                                        val markerOpt = MarkerOptions()
                                            .position(LatLng(pt.latitude, pt.longitude))
                                            .title("${element.category} - ${element.name}")
                                            .snippet(element.description)
                                        mapboxMap.addMarker(markerOpt)
                                    }
                                }
                                "LINE" -> {
                                    if (element.points.isNotEmpty()) {
                                        val latLngList = element.points.map { LatLng(it.latitude, it.longitude) }
                                        val colorInt = try {
                                            android.graphics.Color.parseColor(element.color)
                                        } catch (e: Exception) {
                                            android.graphics.Color.BLUE
                                        }
                                        val polylineOpt = PolylineOptions()
                                            .addAll(latLngList)
                                            .color(colorInt)
                                            .width(element.strokeWidth)
                                        mapboxMap.addPolyline(polylineOpt)
                                    }
                                }
                                "POLYGON" -> {
                                    if (element.points.isNotEmpty()) {
                                        val latLngList = element.points.map { LatLng(it.latitude, it.longitude) }
                                        val baseColor = try {
                                            android.graphics.Color.parseColor(element.color)
                                        } catch (e: Exception) {
                                            android.graphics.Color.BLUE
                                        }
                                        val fillColorInt = android.graphics.Color.argb(
                                            75, // Alpha (approx 30%)
                                            android.graphics.Color.red(baseColor),
                                            android.graphics.Color.green(baseColor),
                                            android.graphics.Color.blue(baseColor)
                                        )
                                        val polygonOpt = PolygonOptions()
                                            .addAll(latLngList)
                                            .fillColor(fillColorInt)
                                            .strokeColor(baseColor)
                                        mapboxMap.addPolygon(polygonOpt)
                                    }
                                }
                            }
                        }

                        // Also draw legacy customMarkers as backup
                        customMarkers.forEach { marker ->
                            val markerOpt = MarkerOptions()
                                .position(LatLng(marker.latitude, marker.longitude))
                                .title("${marker.category} - ${marker.name}")
                            mapboxMap.addMarker(markerOpt)
                        }

                        // Draw shared positions of other users in real-time
                        val userIcon = createUserMarkerIcon(context)
                        sharedPositions.value.distinctBy { it.email }.forEach { sp ->
                            val markerOpt = MarkerOptions()
                                .position(LatLng(sp.latitude, sp.longitude))
                                .title(sp.email)
                                .snippet("📍 Position partagée en direct")
                                .icon(userIcon)
                            mapboxMap.addMarker(markerOpt)
                        }

                        // Draw active drawing points preview
                        if (drawingMode != null && activeDrawingPoints.isNotEmpty()) {
                            activeDrawingPoints.forEachIndexed { index, pt ->
                                val tempMarker = MarkerOptions()
                                    .position(pt)
                                    .title("Sommet ${index + 1}")
                                mapboxMap.addMarker(tempMarker)
                            }

                            if (activeDrawingPoints.size >= 2) {
                                val pathColor = try {
                                    android.graphics.Color.parseColor(vectorColorInput)
                                } catch (e: Exception) {
                                    android.graphics.Color.BLUE
                                }

                                if (drawingMode == "LINE") {
                                    val polylineOpt = PolylineOptions()
                                        .addAll(activeDrawingPoints)
                                        .color(pathColor)
                                        .width(4.0f)
                                    mapboxMap.addPolyline(polylineOpt)
                                } else if (drawingMode == "POLYGON") {
                                    val fillColorInt = android.graphics.Color.argb(
                                        50,
                                        android.graphics.Color.red(pathColor),
                                        android.graphics.Color.green(pathColor),
                                        android.graphics.Color.blue(pathColor)
                                    )
                                    val polygonOpt = PolygonOptions()
                                        .addAll(activeDrawingPoints)
                                        .fillColor(fillColorInt)
                                        .strokeColor(pathColor)
                                    mapboxMap.addPolygon(polygonOpt)
                                }
                            }
                        }
                    }
                }
            )

            // --- DECORATIVE CENTRAL CROSSHAIR / TARGET CHIP ---
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Center)
                    .background(Color.Transparent)
            ) {
                // Crosshair inner indicator
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(MaterialTheme.colorScheme.error, CircleShape)
                        .align(Alignment.Center)
                )
                // Outer ring
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .border(1.5.dp, MaterialTheme.colorScheme.error, CircleShape)
                        .align(Alignment.Center)
                )
            }
        }

        // --- TOP LANDMARK CHIPS BAR ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, start = 12.dp, end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Elegant top branding header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.92f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { showSidebar = !showSidebar },
                    modifier = Modifier.testTag("toggle_sidebar_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Open map styles menu",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Mapbox Explorer",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Real-time Vector Map & Spatial Tracker",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = { showVectorListDialog = true },
                    modifier = Modifier.testTag("show_vector_list_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Liste des vecteurs créés",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                }
                IconButton(
                    onClick = { locateUser() },
                    modifier = Modifier.testTag("locate_user_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Activer la position GPS",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            if (!isTokenConfigured && userRole != "vacataire") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(10.dp))
                        .clickable { showSidebar = true },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.95f)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Warning",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Mode Démo Public (OpenFreeMap)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = "Ouvrez le Menu en haut à gauche pour configurer votre jeton (Token) Mapbox et charger vos calques.",
                                fontSize = 9.5.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        }

        // --- SIDEBAR SCRIM DISMISSER ---
        AnimatedVisibility(
            visible = showSidebar,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { showSidebar = false }
            )
        }

        // --- SIDEBAR SLIDE-IN PANEL ---
        AnimatedVisibility(
            visible = showSidebar,
            enter = androidx.compose.animation.slideInHorizontally(
                initialOffsetX = { -it }
            ) + fadeIn(),
            exit = androidx.compose.animation.slideOutHorizontally(
                targetOffsetX = { -it }
            ) + fadeOut(),
            modifier = Modifier
                .fillMaxHeight()
                .width(300.dp)
                .align(Alignment.CenterStart)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .shadow(16.dp, RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
                    .testTag("sidebar_panel"),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(androidx.compose.foundation.rememberScrollState())
                ) {
                    // Sidebar Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Cartes & Thèmes",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(
                            onClick = { showSidebar = false },
                            modifier = Modifier.testTag("close_sidebar_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fermer le menu",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    androidx.compose.material3.HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    // Active User Profile Display
                    val currentUser = remember {
                        try {
                            com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                        } catch (e: Exception) {
                            null
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    if (currentUser != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(MaterialTheme.colorScheme.primary, androidx.compose.foundation.shape.CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = (currentUser.displayName?.take(1) ?: currentUser.email?.take(1) ?: "U").uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = currentUser.displayName ?: "Utilisateur",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = currentUser.email ?: "",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier
                                            .background(
                                                if (userRole == "vacataire") MaterialTheme.colorScheme.tertiaryContainer 
                                                else MaterialTheme.colorScheme.primaryContainer,
                                                RoundedCornerShape(6.dp)
                                            )
                                            .clickable {
                                                val newRole = if (userRole == "vacataire") "titulaire" else "vacataire"
                                                userRole = newRole
                                                context.getSharedPreferences("sig_foret_prefs", Context.MODE_PRIVATE)
                                                    .edit()
                                                    .putString("user_role", newRole)
                                                    .apply()
                                                Toast.makeText(context, "Rôle mis à jour : ${if (newRole == "vacataire") "Vacataire Dédié" else "Titulaire"}", Toast.LENGTH_SHORT).show()
                                            }
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (userRole == "vacataire") "Vacataire Dédié 🔑" else "Fonctionnaire Titulaire 🌲",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (userRole == "vacataire") MaterialTheme.colorScheme.onTertiaryContainer 
                                                    else MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Offline / Local Demo mode user card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(MaterialTheme.colorScheme.primary, androidx.compose.foundation.shape.CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = currentUserEmail.take(1).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Agent Local (Demo)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = currentUserEmail,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier
                                            .background(
                                                if (userRole == "vacataire") MaterialTheme.colorScheme.tertiaryContainer 
                                                else MaterialTheme.colorScheme.primaryContainer,
                                                RoundedCornerShape(6.dp)
                                            )
                                            .clickable {
                                                val newRole = if (userRole == "vacataire") "titulaire" else "vacataire"
                                                userRole = newRole
                                                context.getSharedPreferences("sig_foret_prefs", Context.MODE_PRIVATE)
                                                    .edit()
                                                    .putString("user_role", newRole)
                                                    .apply()
                                                Toast.makeText(context, "Rôle mis à jour : ${if (newRole == "vacataire") "Vacataire Dédié" else "Titulaire"}", Toast.LENGTH_SHORT).show()
                                            }
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (userRole == "vacataire") "Vacataire Dédié 🔑" else "Fonctionnaire Titulaire 🌲",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (userRole == "vacataire") MaterialTheme.colorScheme.onTertiaryContainer 
                                                    else MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- REALT-TIME SHARED POSITIONS SECTION ---
                    Text(
                        text = "MEMBRES EN DIRECT (${sharedPositions.value.distinctBy { it.email }.size}) :",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    
                    if (sharedPositions.value.isEmpty()) {
                        Text(
                            text = "Aucune position partagée.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        ) {
                            sharedPositions.value.distinctBy { it.email }.forEach { sp ->
                                val isMe = sp.email == currentUserEmail
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            flyToLocation(sp.latitude, sp.longitude, 15.5)
                                            Toast.makeText(context, "Centrage sur : ${sp.email}", Toast.LENGTH_SHORT).show()
                                        }
                                        .testTag("shared_user_${sp.email.replace("@", "_").replace(".", "_")}"),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isMe) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isMe) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .background(
                                                    if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = sp.email.take(1).uppercase(),
                                                fontWeight = FontWeight.Bold,
                                                color = if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary,
                                                fontSize = 12.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = if (isMe) "Moi (${sp.email})" else sp.email,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.5.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "Lat: ${"%.5f".format(sp.latitude)}, Lng: ${"%.5f".format(sp.longitude)}",
                                                fontSize = 9.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            val recordTime = if (sp.timestamp > 0) {
                                                try {
                                                    java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(sp.timestamp))
                                                } catch (e: Exception) {
                                                    ""
                                                }
                                            } else {
                                                ""
                                            }
                                            if (recordTime.isNotEmpty()) {
                                                Text(
                                                    text = "Mis à jour: $recordTime ⏱️",
                                                    fontSize = 8.5.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                        Icon(
                                            imageVector = Icons.Default.Explore,
                                            contentDescription = "Zoomer sur la position",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    androidx.compose.material3.HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = "SÉLECTIONNER UN CALQUE :",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // List of style cards
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        mapStyles.forEach { style ->
                            val isSelected = selectedStyle.name == style.name
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedStyle = style
                                        Toast.makeText(context, "Calque activé : ${style.name}", Toast.LENGTH_SHORT).show()
                                    }
                                    .testTag("sidebar_style_${style.name.replace(" ", "_")}"),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = if (isSelected) {
                                    androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                } else {
                                    androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.surface,
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = style.icon,
                                            contentDescription = null,
                                            tint = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                            else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = style.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                            else MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = style.description,
                                            fontSize = 11.sp,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                            else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Spacer
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            showSidebar = false
                            showAmodiatairesDialog = true
                            if (amodiatairesList.isEmpty()) {
                                refreshAmodiataires()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("sidebar_amodiataires_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Base de Données Amodiataires 🗄️",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            showSidebar = false
                            showInvestorsDialog = true
                            if (investorsList.isEmpty()) {
                                refreshInvestors()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("sidebar_investors_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Base de Données Investisseurs 💼",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            showSidebar = false
                            showContratsDialog = true
                            if (contratsList.isEmpty()) {
                                refreshContrats()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("sidebar_contrats_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Contrats d'Exploitation 📜",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            try {
                                com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            onSignOut()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("sidebar_signout_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Sign Out",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Se déconnecter",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    // Sidebar Footer
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Système d'Information Géographique",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }

        // --- FLOATING MAP CONTROLS (ZOOM & UTILITIES) ---
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.End
        ) {
            // Real-time GPS Position Sharing Button (turns green when active)
            Card(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(6.dp, CircleShape)
                    .clickable { toggleShareLocation() }
                    .testTag("share_position_button"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSharingLocation) Color(0xFF10B981) else MaterialTheme.colorScheme.surface.copy(alpha = 0.90f)
                ),
                shape = CircleShape,
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (isSharingLocation) Color(0xFF047857) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Partager ma position",
                        tint = if (isSharingLocation) Color.White else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Zoom & Navigation Controls Pill
            Card(
                modifier = Modifier
                    .width(48.dp)
                    .shadow(6.dp, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.90f)
                ),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = {
                            mapController?.let { controller ->
                                controller.animateCamera(CameraUpdateFactory.zoomIn(), 300)
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("zoom_in_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ZoomIn,
                            contentDescription = "Zoom In",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    androidx.compose.material3.HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    IconButton(
                        onClick = {
                            mapController?.let { controller ->
                                controller.animateCamera(CameraUpdateFactory.zoomOut(), 300)
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("zoom_out_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ZoomOut,
                            contentDescription = "Zoom Out",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    androidx.compose.material3.HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    // Button 3: Search / Fly-to Coordinates
                    IconButton(
                        onClick = { showFlyToDialog = true },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("fly_to_trigger_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Navigation,
                            contentDescription = "Aller aux coordonnées",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // --- CONSOLIDATED FORESTRY DRAWING TOOL (POINT, LINE, POLYGON) ---
            Box(
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Expanded Menu Content (Point, Line, Polygon options)
                    AnimatedVisibility(
                        visible = isDrawingMenuOpen,
                        enter = fadeIn() + androidx.compose.animation.slideInHorizontally(initialOffsetX = { it }),
                        exit = fadeOut() + androidx.compose.animation.slideOutHorizontally(targetOffsetX = { it })
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Option 1: Point / Balise
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            showAddMarkerDialog = true
                                            isDrawingMenuOpen = false
                                        }
                                        .padding(8.dp)
                                        .testTag("consolidated_menu_point_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = "Ajouter un repère",
                                        tint = Color(0xFF10B981), // Emerald green
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Point 📍",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(30.dp)
                                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                )

                                // Option 2: Ligne
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            drawingMode = "LINE"
                                            activeDrawingPoints.clear()
                                            isDrawingMenuOpen = false
                                            Toast.makeText(context, "Mode Dessin: Ligne activé 📐\nDéplacez la carte et cliquez sur 'Ajouter Point'.", Toast.LENGTH_LONG).show()
                                        }
                                        .padding(8.dp)
                                        .testTag("consolidated_menu_line_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Dessiner une Ligne",
                                        tint = if (drawingMode == "LINE") Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Ligne 📐",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(30.dp)
                                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                )

                                // Option 3: Polygone
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            drawingMode = "POLYGON"
                                            activeDrawingPoints.clear()
                                            isDrawingMenuOpen = false
                                            Toast.makeText(context, "Mode Dessin: Polygone activé ⬡\nDéplacez la carte et cliquez sur 'Ajouter Point'.", Toast.LENGTH_LONG).show()
                                        }
                                        .padding(8.dp)
                                        .testTag("consolidated_menu_polygon_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChangeHistory,
                                        contentDescription = "Dessiner un Polygone",
                                        tint = if (drawingMode == "POLYGON") Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Polygone ⬡",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    // Floating Action Button (Trigger)
                    androidx.compose.material3.FloatingActionButton(
                        onClick = { isDrawingMenuOpen = !isDrawingMenuOpen },
                        containerColor = if (isDrawingMenuOpen) MaterialTheme.colorScheme.primary else Color(0xFF10B981),
                        contentColor = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .size(56.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp))
                            .testTag("consolidated_drawing_fab")
                    ) {
                        Icon(
                            imageVector = if (isDrawingMenuOpen) Icons.Default.Close else Icons.Default.Edit,
                            contentDescription = "Menu de dessin",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }



        // --- FLOATING VECTOR DRAWING CONTROLLER PANEL ---
        AnimatedVisibility(
            visible = drawingMode != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth(0.92f)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when (drawingMode) {
                                    "LINE" -> Icons.Default.Edit
                                    "POLYGON" -> Icons.Default.ChangeHistory
                                    else -> Icons.Default.Place
                                },
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Mode Dessin: ${if (drawingMode == "LINE") "Ligne 📐" else "Polygone ⬡"}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        ) {
                            Text(
                                text = "${activeDrawingPoints.size} points",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    
                    Text(
                        text = "Touchez directement la carte pour ajouter des points/sommets, ou utilisez le réticule et cliquez sur 'Ajouter Point'.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )
                    
                    // Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Button: Add Vertex
                        Button(
                            onClick = {
                                activeDrawingPoints.add(LatLng(centerLat, centerLon))
                            },
                            modifier = Modifier.weight(1.2f).height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ajouter Point", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        // Button: Undo last point
                        OutlinedButton(
                            onClick = {
                                if (activeDrawingPoints.isNotEmpty()) {
                                    activeDrawingPoints.removeAt(activeDrawingPoints.size - 1)
                                }
                            },
                            enabled = activeDrawingPoints.isNotEmpty(),
                            modifier = Modifier.weight(0.9f).height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text("Annuler", fontSize = 10.sp)
                        }
                        
                        // Button: Save Drawing
                        val isValid = if (drawingMode == "LINE") activeDrawingPoints.size >= 2 else activeDrawingPoints.size >= 3
                        Button(
                            onClick = {
                                showSaveVectorDialog = true
                            },
                            enabled = isValid,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF10B981),
                                disabledContainerColor = Color(0xFF10B981).copy(alpha = 0.3f),
                                contentColor = Color.White,
                                disabledContentColor = Color.White.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.weight(1f).height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text("Enregistrer", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    // Cancel Drawing Button
                    OutlinedButton(
                        onClick = {
                            drawingMode = null
                            activeDrawingPoints.clear()
                        },
                        modifier = Modifier.fillMaxWidth().height(32.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                        contentPadding = PaddingValues(vertical = 2.dp)
                    ) {
                        Text("Quitter le mode dessin", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // --- DIALOG: SAVE ACTIVE VECTOR DRAWING ---
    if (showSaveVectorDialog) {
        val categories = listOf(
            "Plantation forestière 🌲",
            "Plantation fruitière 🍎",
            "Incendie 🔥",
            "Infraction ⚠️",
            "Point d'eau 💧",
            "Position Brigade 👮"
        )
        val colors = listOf(
            Pair("Bleu", "#3B82F6"),
            Pair("Vert", "#10B981"),
            Pair("Rouge", "#EF4444"),
            Pair("Orange", "#F59E0B"),
            Pair("Violet", "#8B5CF6"),
            Pair("Rose", "#EC4899")
        )
        
        AlertDialog(
            onDismissRequest = { showSaveVectorDialog = false },
            title = {
                Text(
                    text = "Enregistrer la ${if (drawingMode == "LINE") "Ligne 📐" else "Forme ⬡"}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Donnez un nom et configurez le style de votre dessin vectoriel contenant ${activeDrawingPoints.size} sommets.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = vectorNameInput,
                        onValueChange = { vectorNameInput = it },
                        label = { Text("Nom du dessin") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = vectorDescriptionInput,
                        onValueChange = { vectorDescriptionInput = it },
                        label = { Text("Description (Optionnel)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    Text(
                        text = "Couleur du vecteur",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    // Color Selection Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(colors) { colorPair ->
                            val isSelected = vectorColorInput == colorPair.second
                            val chipColor = Color(android.graphics.Color.parseColor(colorPair.second))
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { vectorColorInput = colorPair.second },
                                color = if (isSelected) chipColor else chipColor.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(2.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            ) {
                                Text(
                                    text = colorPair.first,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else chipColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "Catégorie",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    // Category Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { category ->
                            val isSelected = vectorCategoryInput == category
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { vectorCategoryInput = category },
                                color = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = category,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (vectorNameInput.isNotBlank()) {
                            val newElement = VectorElement(
                                id = UUID.randomUUID().toString(),
                                name = vectorNameInput,
                                type = drawingMode ?: "LINE",
                                points = activeDrawingPoints.map { GeoPointData(it.latitude, it.longitude) },
                                color = vectorColorInput,
                                strokeWidth = 4.0f,
                                category = vectorCategoryInput,
                                description = vectorDescriptionInput,
                                creatorEmail = currentUserEmail,
                                timestamp = System.currentTimeMillis()
                            )

                            if (db != null || rtdb != null) {
                                firebaseService.saveVectorElement(newElement) { success ->
                                    if (success) {
                                        Toast.makeText(context, "Dessin enregistré sur Firebase ! ☁️", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Enregistré localement (Mode hors-ligne) 💾", Toast.LENGTH_SHORT).show()
                                        localFallbackElements.add(newElement)
                                        saveLocalFallbackElements()
                                    }
                                }
                            } else {
                                localFallbackElements.add(newElement)
                                saveLocalFallbackElements()
                                Toast.makeText(context, "Enregistré localement 💾", Toast.LENGTH_SHORT).show()
                            }

                            // Reset states
                            drawingMode = null
                            activeDrawingPoints.clear()
                            showSaveVectorDialog = false
                            vectorNameInput = ""
                            vectorDescriptionInput = ""
                        } else {
                            Toast.makeText(context, "Veuillez entrer un nom", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Sauvegarder", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveVectorDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    // --- DIALOG: DROP / CREATE CUSTOM LANDMARK ---
    if (showAddMarkerDialog) {
        val categories = listOf(
            "Plantation forestière 🌲",
            "Plantation fruitière 🍎",
            "Incendie 🔥",
            "Infraction ⚠️",
            "Point d'eau 💧",
            "Position Brigade 👮"
        )
        AlertDialog(
            onDismissRequest = { showAddMarkerDialog = false },
            title = {
                Text(
                    text = "Save Spatial Marker",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Store coordinates [${String.format("%.4f, %.4f", centerLat, centerLon)}] with a custom pin description.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = customMarkerName,
                        onValueChange = { customMarkerName = it },
                        label = { Text("Marker Title (e.g. My Favorite Cafe)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("marker_name_input"),
                        singleLine = true
                    )

                    Text(
                        text = "Category Pin Icon",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    // Horizontal selection of category pins
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { category ->
                            val isSelected = customMarkerCategory == category
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { customMarkerCategory = category }
                                    .testTag("category_chip_$category"),
                                color = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = category,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customMarkerName.isNotBlank()) {
                            val newElement = VectorElement(
                                id = java.util.UUID.randomUUID().toString(),
                                name = customMarkerName,
                                type = "POINT",
                                points = listOf(GeoPointData(centerLat, centerLon)),
                                color = "#EF4444", // standard marker color (Red)
                                strokeWidth = 4.0f,
                                category = customMarkerCategory,
                                creatorEmail = currentUserEmail,
                                timestamp = System.currentTimeMillis()
                            )

                            if (db != null || rtdb != null) {
                                firebaseService.saveVectorElement(newElement) { success ->
                                    if (success) {
                                        Toast.makeText(context, "Saved spatial pin to Firebase! ☁️", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Saved locally (Offline/No Firebase config)", Toast.LENGTH_SHORT).show()
                                        localFallbackElements.add(newElement)
                                        saveLocalFallbackElements()
                                    }
                                }
                            } else {
                                localFallbackElements.add(newElement)
                                saveLocalFallbackElements()
                                Toast.makeText(context, "Saved locally 💾", Toast.LENGTH_SHORT).show()
                            }

                            // Keep customMarkers populated as legacy compatibility
                            customMarkers.add(
                                CustomMarker(
                                    name = customMarkerName,
                                    latitude = centerLat,
                                    longitude = centerLon,
                                    category = customMarkerCategory
                                )
                            )
                            showAddMarkerDialog = false
                            customMarkerName = ""
                        } else {
                            Toast.makeText(context, "Please enter a valid title", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.testTag("dialog_confirm_button")
                ) {
                    Text("Save Pin")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMarkerDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(18.dp)
        )
    }

    // --- DIALOG: FLY TO SPECIFIC LATITUDE & LONGITUDE ---
    if (showFlyToDialog) {
        AlertDialog(
            onDismissRequest = { showFlyToDialog = false },
            title = {
                Text(
                    text = "Navigate to Coordinates",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Enter raw coordinates decimal values to fly immediately to that location.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = inputLat,
                        onValueChange = { inputLat = it },
                        label = { Text("Latitude (e.g. 35.6595)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("latitude_input"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    OutlinedTextField(
                        value = inputLon,
                        onValueChange = { inputLon = it },
                        label = { Text("Longitude (e.g. 139.7004)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("longitude_input"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val latVal = inputLat.toDoubleOrNull()
                        val lonVal = inputLon.toDoubleOrNull()
                        if (latVal != null && lonVal != null && latVal in -90.0..90.0 && lonVal in -180.0..180.0) {
                            flyToLocation(latVal, lonVal, 14.0)
                            showFlyToDialog = false
                            Toast.makeText(context, "Navigating...", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Please enter valid numeric latitude [-90, 90] & longitude [-180, 180]", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.testTag("fly_to_confirm_button")
                ) {
                    Text("Fly Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFlyToDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(18.dp)
        )
    }

    // --- DIALOG: LIST OF CREATED VECTORS (DATABASE LAYER CONTROL & EXPORT) ---
    if (showVectorListDialog) {
        AlertDialog(
            onDismissRequest = { showVectorListDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Base de données géospatiale 🌲",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Gérer, exporter et partager vos tracés",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isRefreshingVectors) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(4.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            IconButton(
                                onClick = { refreshVectorElements() },
                                modifier = Modifier.testTag("refresh_vector_list_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Actualiser la liste",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        IconButton(onClick = { showVectorListDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Fermer")
                        }
                    }
                }
            },
            text = {
                var searchQuery by remember { mutableStateOf("") }
                var selectedTypeFilter by remember { mutableStateOf("ALL") }
                val selectedElementIds = remember { mutableStateListOf<String>() }

                val allElements = remember(vectorElements, localFallbackElements) {
                    (vectorElements + localFallbackElements).distinctBy { it.id }.sortedByDescending { it.timestamp }
                }

                val filteredElements = allElements.filter { el ->
                    val matchesSearch = el.name.contains(searchQuery, ignoreCase = true) || el.category.contains(searchQuery, ignoreCase = true)
                    val matchesType = selectedTypeFilter == "ALL" || el.type == selectedTypeFilter
                    matchesSearch && matchesType
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(480.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Search box
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Rechercher par nom ou catégorie...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Effacer")
                                }
                            }
                        }
                    )

                    // Filter chips / tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val filters = listOf(
                            Triple("ALL", "Tous", "📦"),
                            Triple("POINT", "Points", "📍"),
                            Triple("LINE", "Lignes", "📐"),
                            Triple("POLYGON", "Polygones", "⬡")
                        )
                        filters.forEach { (typeKey, labelStr, emoji) ->
                            val isSelected = selectedTypeFilter == typeKey
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedTypeFilter = typeKey },
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$emoji $labelStr",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Select All / Deselect All row
                    if (filteredElements.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val allVisibleSelected = filteredElements.all { selectedElementIds.contains(it.id) }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    if (allVisibleSelected) {
                                        filteredElements.forEach { selectedElementIds.remove(it.id) }
                                    } else {
                                        filteredElements.forEach { el ->
                                            if (!selectedElementIds.contains(el.id)) {
                                                selectedElementIds.add(el.id)
                                            }
                                        }
                                    }
                                }
                            ) {
                                Checkbox(
                                    checked = allVisibleSelected && filteredElements.isNotEmpty(),
                                    onCheckedChange = { checked ->
                                        if (checked) {
                                            filteredElements.forEach { el ->
                                                if (!selectedElementIds.contains(el.id)) {
                                                    selectedElementIds.add(el.id)
                                                }
                                            }
                                        } else {
                                            filteredElements.forEach { selectedElementIds.remove(it.id) }
                                        }
                                    },
                                    modifier = Modifier.testTag("select_all_checkbox")
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Tout sélectionner (${filteredElements.size})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            
                            if (selectedElementIds.isNotEmpty()) {
                                TextButton(
                                    onClick = { selectedElementIds.clear() },
                                    modifier = Modifier.testTag("clear_selection_button")
                                ) {
                                    Text("Effacer la sélection", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }

                    // Bulk/Selected export and share action buttons
                    if (filteredElements.isNotEmpty()) {
                        if (selectedElementIds.isNotEmpty()) {
                            // Export/Share selected
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Save Selected
                                Button(
                                    onClick = {
                                        val selectedElements = filteredElements.filter { selectedElementIds.contains(it.id) }
                                        val kmlStr = generateAllKmlContent(selectedElements)
                                        pendingKmlToSave = kmlStr
                                        pendingKmlFileName = "Export_Selection_Vecteurs.kml"
                                        createDocLauncher.launch(pendingKmlFileName)
                                    },
                                    modifier = Modifier.weight(1f).testTag("export_selected_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)), // Emerald Green
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Sauver (${selectedElementIds.size}) 📥",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1
                                    )
                                }

                                // Share Selected
                                Button(
                                    onClick = {
                                        val selectedElements = filteredElements.filter { selectedElementIds.contains(it.id) }
                                        val kmlStr = generateAllKmlContent(selectedElements)
                                        shareKmlFile(context, "Export_Selection_Vecteurs.kml", kmlStr)
                                    },
                                    modifier = Modifier.weight(1f).testTag("share_selected_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Partager (${selectedElementIds.size}) 📤",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1
                                    )
                                }
                            }
                        } else {
                            // Export/Share all
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Save All
                                Button(
                                    onClick = {
                                        val allKmlStr = generateAllKmlContent(filteredElements)
                                        pendingKmlToSave = allKmlStr
                                        pendingKmlFileName = "Export_Tous_Vecteurs.kml"
                                        createDocLauncher.launch(pendingKmlFileName)
                                    },
                                    modifier = Modifier.weight(1f).testTag("export_all_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Sauver Tout 📁", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                }

                                // Share All
                                Button(
                                    onClick = {
                                        val allKmlStr = generateAllKmlContent(filteredElements)
                                        shareKmlFile(context, "Export_Tous_Vecteurs.kml", allKmlStr)
                                    },
                                    modifier = Modifier.weight(1f).testTag("share_all_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Partager Tout 📤", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                }
                            }
                        }
                    }

                    // Elements list
                    if (filteredElements.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Aucun vecteur trouvé 🌲",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredElements) { el ->
                                var isTableExpanded by remember { mutableStateOf(false) }
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        0.5.dp,
                                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val isSelected = selectedElementIds.contains(el.id)
                                            Checkbox(
                                                checked = isSelected,
                                                onCheckedChange = { checked ->
                                                    if (checked) {
                                                        selectedElementIds.add(el.id)
                                                    } else {
                                                        selectedElementIds.remove(el.id)
                                                    }
                                                },
                                                modifier = Modifier.testTag("select_vector_${el.id}")
                                            )

                                            Row(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clickable {
                                                        el.points.firstOrNull()?.let { pt ->
                                                            flyToLocation(pt.latitude, pt.longitude, 15.0)
                                                            showVectorListDialog = false
                                                            Toast.makeText(context, "Centrage sur : ${el.name}", Toast.LENGTH_SHORT).show()
                                                        }
                                                    },
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                val typeEmoji = when (el.type) {
                                                    "POINT" -> "📍"
                                                    "LINE" -> "📐"
                                                    "POLYGON" -> "⬡"
                                                    else -> "📦"
                                                }
                                                Text(
                                                    text = typeEmoji,
                                                    fontSize = 16.sp
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Column {
                                                    Text(
                                                        text = el.name,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = el.category,
                                                        fontSize = 11.sp,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }

                                            // Delete Button
                                            IconButton(
                                                onClick = {
                                                    if (db != null || rtdb != null) {
                                                        firebaseService.deleteVectorElement(el.id) { success ->
                                                            localFallbackElements.removeAll { it.id == el.id }
                                                            saveLocalFallbackElements()
                                                            if (success) {
                                                                Toast.makeText(context, "Vecteur supprimé !", Toast.LENGTH_SHORT).show()
                                                            } else {
                                                                Toast.makeText(context, "Supprimé localement", Toast.LENGTH_SHORT).show()
                                                            }
                                                        }
                                                    } else {
                                                        localFallbackElements.removeAll { it.id == el.id }
                                                        saveLocalFallbackElements()
                                                        Toast.makeText(context, "Supprimé localement", Toast.LENGTH_SHORT).show()
                                                    }
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Supprimer",
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }

                                        if (el.description.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = el.description,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        
                                        // Collapsible Table Toggle Button
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { isTableExpanded = !isTableExpanded },
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (isTableExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.secondary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Text(
                                                    text = if (isTableExpanded) "Masquer la table d'attributs 📊" else "Afficher la table d'attributs 📊",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.secondary
                                                )
                                            }
                                        }

                                        // Collapsible Attribute Table Content
                                        if (isTableExpanded) {
                                            val attrDateString = try {
                                                java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(el.timestamp))
                                            } catch (e: Exception) {
                                                ""
                                            }
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 6.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                                                ),
                                                border = androidx.compose.foundation.BorderStroke(
                                                    0.5.dp,
                                                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                                                ),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(8.dp),
                                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    AttributeRow("Créateur 📧", el.creatorEmail)
                                                    AttributeRow("Date de création 📅", attrDateString)
                                                    when (el.type) {
                                                        "POINT" -> {
                                                            val pt = el.points.firstOrNull()
                                                            if (pt != null) {
                                                                AttributeRow("Type de géométrie", "Point 📍")
                                                                AttributeRow("Coordonnée X (Long)", String.format(java.util.Locale.US, "%.6f°", pt.longitude))
                                                                AttributeRow("Coordonnée Y (Lat)", String.format(java.util.Locale.US, "%.6f°", pt.latitude))
                                                            } else {
                                                                AttributeRow("Erreur", "Aucune coordonnée")
                                                            }
                                                        }
                                                        "LINE" -> {
                                                            val lengthMeters = calculateLineLengthMeters(el.points)
                                                            val lengthStr = if (lengthMeters >= 1000.0) {
                                                                String.format(java.util.Locale.US, "%.2f km", lengthMeters / 1000.0)
                                                            } else {
                                                                String.format(java.util.Locale.US, "%.1f m", lengthMeters)
                                                            }
                                                            AttributeRow("Type de géométrie", "Ligne 📐")
                                                            AttributeRow("Longueur", lengthStr)
                                                            
                                                            val startPt = el.points.firstOrNull()
                                                            val endPt = el.points.lastOrNull()
                                                            if (startPt != null) {
                                                                AttributeRow("Départ X (Long)", String.format(java.util.Locale.US, "%.6f°", startPt.longitude))
                                                                AttributeRow("Départ Y (Lat)", String.format(java.util.Locale.US, "%.6f°", startPt.latitude))
                                                            }
                                                            if (endPt != null && el.points.size > 1) {
                                                                AttributeRow("Arrivée X (Long)", String.format(java.util.Locale.US, "%.6f°", endPt.longitude))
                                                                AttributeRow("Arrivée Y (Lat)", String.format(java.util.Locale.US, "%.6f°", endPt.latitude))
                                                            }
                                                        }
                                                        "POLYGON" -> {
                                                            val areaHa = calculatePolygonAreaHectares(el.points)
                                                            val areaStr = if (areaHa >= 0.01) {
                                                                String.format(java.util.Locale.US, "%.2f ha (approx. %.0f m²)", areaHa, areaHa * 10000.0)
                                                            } else {
                                                                String.format(java.util.Locale.US, "%.1f m²", areaHa * 10000.0)
                                                            }
                                                            val centroidLat = el.points.map { it.latitude }.average()
                                                            val centroidLon = el.points.map { it.longitude }.average()
                                                            
                                                            AttributeRow("Type de géométrie", "Polygone ⬡")
                                                            AttributeRow("Superficie", areaStr)
                                                            AttributeRow("Centre X (Long)", String.format(java.util.Locale.US, "%.6f°", centroidLon))
                                                            AttributeRow("Centre Y (Lat)", String.format(java.util.Locale.US, "%.6f°", centroidLat))
                                                        }
                                                        else -> {
                                                            AttributeRow("Type", "Inconnu")
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        val dateString = try {
                                            java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(el.timestamp))
                                        } catch (e: Exception) {
                                            ""
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Bottom
                                        ) {
                                            Text(
                                                text = "Créé le: $dateString",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                            )

                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                // Share KML File Button
                                                IconButton(
                                                    onClick = {
                                                        val kmlStr = generateKmlContent(el)
                                                        val cleanName = el.name.replace(" ", "_").replace("/", "_")
                                                        shareKmlFile(context, "$cleanName.kml", kmlStr)
                                                    },
                                                    modifier = Modifier.size(32.dp).testTag("share_kml_file_${el.id}")
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Share,
                                                        contentDescription = "Partager le fichier KML",
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }

                                                // Share Maps Location Link Button
                                                IconButton(
                                                    onClick = {
                                                        shareGoogleMapsLink(context, el)
                                                     },
                                                    modifier = Modifier.size(32.dp).testTag("share_maps_link_${el.id}")
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Place,
                                                        contentDescription = "Partager le lien Google Maps",
                                                        tint = Color(0xFFF59E0B), // Amber color
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }

                                                // Export / Save File Button
                                                IconButton(
                                                    onClick = {
                                                        val kmlStr = generateKmlContent(el)
                                                        pendingKmlToSave = kmlStr
                                                        pendingKmlFileName = "${el.name.replace(" ", "_").replace("/", "_")}.kml"
                                                        createDocLauncher.launch(pendingKmlFileName)
                                                    },
                                                    modifier = Modifier.size(32.dp).testTag("export_kml_file_${el.id}")
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.ContentCopy,
                                                        contentDescription = "Sauvegarder le fichier KML",
                                                        tint = Color(0xFF10B981), // Emerald green
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showVectorListDialog = false }) {
                    Text("Fermer")
                }
            },
            shape = RoundedCornerShape(18.dp)
        )
    }

    if (showAmodiatairesDialog) {
        AmodiatairesDatabaseDialog(
            amodiataires = amodiatairesList,
            isRefreshing = isRefreshingAmodiataires,
            onRefresh = { refreshAmodiataires() },
            onDismiss = { showAmodiatairesDialog = false },
            onFlyTo = { lat, lon ->
                flyToLocation(lat, lon, 15.0)
                Toast.makeText(context, "Centrage sur la position (Lat: $lat, Lng: $lon)", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showInvestorsDialog) {
        InvestorsDatabaseDialog(
            investors = investorsList,
            isRefreshing = isRefreshingInvestors,
            onRefresh = { refreshInvestors() },
            onDismiss = { showInvestorsDialog = false },
            onFlyTo = { lat, lon ->
                flyToLocation(lat, lon, 15.0)
                Toast.makeText(context, "Centrage sur la position (Lat: $lat, Lng: $lon)", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showContratsDialog) {
        ContratsDatabaseDialog(
            contrats = contratsList,
            isRefreshing = isRefreshingContrats,
            onRefresh = { refreshContrats() },
            onDismiss = { showContratsDialog = false },
            onFlyTo = { lat, lon ->
                flyToLocation(lat, lon, 15.0)
                Toast.makeText(context, "Centrage sur la position (Lat: $lat, Lng: $lon)", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun FloatingMapButton(
    icon: ImageVector,
    tag: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(46.dp)
            .shadow(3.dp, CircleShape)
            .clickable { onClick() }
            .testTag(tag),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// --- KML EXPORT GENERATOR HELPERS ---
fun convertHexToKmlColor(hex: String, alphaPercent: Int = 100): String {
    val clean = hex.replace("#", "").uppercase()
    if (clean.length < 6) return "ff0000ff"
    val r = clean.substring(0, 2)
    val g = clean.substring(2, 4)
    val b = clean.substring(4, 6)
    val alphaVal = (alphaPercent * 255) / 100
    val alphaHex = String.format("%02x", alphaVal)
    return "$alphaHex$b$g$r"
}

fun shareKmlFile(context: android.content.Context, fileName: String, kmlContent: String) {
    try {
        val kmlDir = java.io.File(context.cacheDir, "shared_kml")
        if (!kmlDir.exists()) {
            kmlDir.mkdirs()
        }
        val file = java.io.File(kmlDir, fileName)
        file.writeText(kmlContent)

        val authority = "${context.packageName}.fileprovider"
        val uri = androidx.core.content.FileProvider.getUriForFile(context, authority, file)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.google-earth.kml+xml"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Fichier KML: $fileName")
            putExtra(Intent.EXTRA_TEXT, "Voici le tracé vectoriel au format KML, lisible par Google Earth, Google Maps et d'autres outils cartographiques.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(intent, "Partager le fichier KML..."))
    } catch (e: Exception) {
        android.widget.Toast.makeText(context, "Erreur lors du partage: ${e.localizedMessage}", android.widget.Toast.LENGTH_LONG).show()
    }
}

fun shareGoogleMapsLink(context: android.content.Context, element: VectorElement) {
    val firstPt = element.points.firstOrNull() ?: GeoPointData(0.0, 0.0)
    val url = "https://www.google.com/maps/search/?api=1&query=${firstPt.latitude},${firstPt.longitude}"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Position de ${element.name}")
        putExtra(Intent.EXTRA_TEXT, "Voir '${element.name}' sur Google Maps: $url")
    }
    context.startActivity(Intent.createChooser(intent, "Partager le lien de localisation..."))
}

fun generateKmlContent(element: VectorElement): String {
    val geom = when (element.type) {
        "POINT" -> {
            val pt = element.points.firstOrNull() ?: GeoPointData(0.0, 0.0)
            "<Point><coordinates>${pt.longitude},${pt.latitude},0</coordinates></Point>"
        }
        "LINE" -> {
            val coordString = element.points.joinToString(" ") { "${it.longitude},${it.latitude},0" }
            "<LineString><coordinates>$coordString</coordinates></LineString>"
        }
        "POLYGON" -> {
            val coordList = element.points.toMutableList()
            if (coordList.isNotEmpty() && coordList.first() != coordList.last()) {
                coordList.add(coordList.first())
            }
            val coordString = coordList.joinToString(" ") { "${it.longitude},${it.latitude},0" }
            "<Polygon><outerBoundaryIs><LinearRing><coordinates>$coordString</coordinates></LinearRing></outerBoundaryIs></Polygon>"
        }
        else -> ""
    }
    val kmlColorLine = convertHexToKmlColor(element.color, 100)
    val kmlColorPoly = convertHexToKmlColor(element.color, 40)
    val strokeWidthInt = element.strokeWidth.toInt().coerceAtLeast(1)
    
    return """
        <?xml version="1.0" encoding="UTF-8"?>
        <kml xmlns="http://www.opengis.net/kml/2.2">
          <Document>
            <name>${element.name}</name>
            <description>Catégorie: ${element.category}${if (element.description.isNotBlank()) " | " + element.description else ""}</description>
            <Style id="vector_style">
              <LineStyle>
                <color>$kmlColorLine</color>
                <width>$strokeWidthInt</width>
              </LineStyle>
              <PolyStyle>
                <color>$kmlColorPoly</color>
                <fill>1</fill>
                <outline>1</outline>
              </PolyStyle>
            </Style>
            <Placemark>
              <name>${element.name}</name>
              <description>Catégorie: ${element.category}</description>
              <styleUrl>#vector_style</styleUrl>
              $geom
            </Placemark>
          </Document>
        </kml>
    """.trimIndent()
}

fun generateAllKmlContent(elements: List<VectorElement>): String {
    val styles = elements.joinToString("\n") { el ->
        val kmlColorLine = convertHexToKmlColor(el.color, 100)
        val kmlColorPoly = convertHexToKmlColor(el.color, 40)
        val strokeWidthInt = el.strokeWidth.toInt().coerceAtLeast(1)
        """
        <Style id="style_${el.id}">
          <LineStyle>
            <color>$kmlColorLine</color>
            <width>$strokeWidthInt</width>
          </LineStyle>
          <PolyStyle>
            <color>$kmlColorPoly</color>
            <fill>1</fill>
            <outline>1</outline>
          </PolyStyle>
        </Style>
        """.trimIndent()
    }
    
    val placemarks = elements.joinToString("\n") { el ->
        val geom = when (el.type) {
            "POINT" -> {
                val pt = el.points.firstOrNull() ?: GeoPointData(0.0, 0.0)
                "<Point><coordinates>${pt.longitude},${pt.latitude},0</coordinates></Point>"
            }
            "LINE" -> {
                val coordString = el.points.joinToString(" ") { "${it.longitude},${it.latitude},0" }
                "<LineString><coordinates>$coordString</coordinates></LineString>"
            }
            "POLYGON" -> {
                val coordList = el.points.toMutableList()
                if (coordList.isNotEmpty() && coordList.first() != coordList.last()) {
                    coordList.add(coordList.first())
                }
                val coordString = coordList.joinToString(" ") { "${it.longitude},${it.latitude},0" }
                "<Polygon><outerBoundaryIs><LinearRing><coordinates>$coordString</coordinates></LinearRing></outerBoundaryIs></Polygon>"
            }
            else -> ""
        }
        """
        <Placemark>
          <name>${el.name}</name>
          <description>Catégorie: ${el.category}\n${el.description}</description>
          <styleUrl>#style_${el.id}</styleUrl>
          $geom
        </Placemark>
        """.trimIndent()
    }
    return """
        <?xml version="1.0" encoding="UTF-8"?>
        <kml xmlns="http://www.opengis.net/kml/2.2">
          <Document>
            <name>Export National Forestier</name>
            <description>Vecteurs créés par l'utilisateur</description>
            $styles
            $placemarks
          </Document>
        </kml>
    """.trimIndent()
}

fun calculateLineLengthMeters(points: List<GeoPointData>): Double {
    if (points.size < 2) return 0.0
    var totalDistance = 0.0
    val results = FloatArray(1)
    for (i in 0 until points.size - 1) {
        val p1 = points[i]
        val p2 = points[i + 1]
        try {
            android.location.Location.distanceBetween(
                p1.latitude, p1.longitude,
                p2.latitude, p2.longitude,
                results
            )
            totalDistance += results[0]
        } catch (e: Exception) {
            // Fallback
        }
    }
    return totalDistance
}

fun calculatePolygonAreaHectares(points: List<GeoPointData>): Double {
    if (points.size < 3) return 0.0
    val centroidLat = points.map { it.latitude }.average()
    val centroidLon = points.map { it.longitude }.average()
    
    val latToMeters = 111132.0
    val lonToMeters = 111132.0 * Math.cos(Math.toRadians(centroidLat))
    
    val xCoords = points.map { (it.longitude - centroidLon) * lonToMeters }
    val yCoords = points.map { (it.latitude - centroidLat) * latToMeters }
    
    var area = 0.0
    val n = points.size
    for (i in 0 until n) {
        val j = (i + 1) % n
        area += xCoords[i] * yCoords[j] - xCoords[j] * yCoords[i]
    }
    val areaInSqMeters = Math.abs(area) / 2.0
    return areaInSqMeters / 10000.0
}

@Composable
fun AttributeRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun AmodiatairesDatabaseDialog(
    amodiataires: List<Amodiataire>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onDismiss: () -> Unit,
    onFlyTo: (Double, Double) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredList = remember(amodiataires, searchQuery) {
        amodiataires.filter { item ->
            searchQuery.isBlank() ||
                item.applicant_name.contains(searchQuery, ignoreCase = true) ||
                item.applicant_name_french.contains(searchQuery, ignoreCase = true) ||
                item.commune.contains(searchQuery, ignoreCase = true) ||
                item.bail_id.contains(searchQuery, ignoreCase = true) ||
                item.phone.contains(searchQuery, ignoreCase = true) ||
                item.documentId.contains(searchQuery, ignoreCase = true)
        }
    }

    val totalPaidSum = remember(amodiataires) {
        amodiataires.sumOf { it.total_paid }
    }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Base de Données - Amodiataires 🗄️",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${amodiataires.size} enregistrements | Total payé: ${"%.2f".format(totalPaidSum)} DA",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = onRefresh,
                            enabled = !isRefreshing,
                            modifier = Modifier.testTag("refresh_amodiataires_button")
                        ) {
                            if (isRefreshing) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Actualiser avec Firebase",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("close_amodiataires_dialog")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fermer",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar and Refresh
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Rechercher nom, commune, bail...", fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("amodiataires_search_input"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Effacer", modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        shape = RoundedCornerShape(10.dp)
                    )

                    Button(
                        onClick = onRefresh,
                        enabled = !isRefreshing,
                        modifier = Modifier.height(56.dp).testTag("mobile_refresh_amodiataires_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isRefreshing) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = "Actualiser", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Actualiser", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Content List
                if (filteredList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Aucun amodiataire trouvé.",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = onRefresh,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Actualiser depuis Firebase", fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredList) { item ->
                            var expanded by remember { mutableStateOf(false) }
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expanded = !expanded }
                                    .testTag("amodiataire_card_${item.documentId}"),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "${item.applicant_name} (${item.applicant_name_french})",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "Bail: ${item.bail_id.ifBlank { "N/A" }} | Date: ${item.bail_date.ifBlank { "N/A" }}",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        
                                        val statusColor = when {
                                            item.status.contains("ساري", ignoreCase = true) || item.status.contains("تحرير", ignoreCase = true) -> Color(0xFF10B981)
                                            else -> MaterialTheme.colorScheme.secondary
                                        }
                                        Surface(
                                            color = statusColor.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = item.status.ifBlank { item.payment_status.ifBlank { "Actif" } },
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = statusColor,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Commune: ${item.commune.ifBlank { "N/A" }} | Superficie: ${item.accepted_area.ifBlank { item.assigned_area }} ha",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Payé: ${item.total_paid} DA",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF10B981)
                                        )
                                    }

                                    if (expanded) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        androidx.compose.material3.HorizontalDivider(
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        AttributeRow("Activité", item.activity_type.ifBlank { "N/A" })
                                        AttributeRow("Type de Bail", item.bail_type.ifBlank { "N/A" })
                                        AttributeRow("Date de Bail", item.bail_date.ifBlank { "N/A" })
                                        val phoneNum = item.phone.trim()
Row(
    modifier = Modifier
        .fillMaxWidth()
        .clickable(enabled = phoneNum.isNotBlank()) {
            if (phoneNum.isNotBlank()) {
                try {
                    val intent = Intent(Intent.ACTION_DIAL, android.net.Uri.parse("tel:$phoneNum"))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Impossible de composer le numéro", Toast.LENGTH_SHORT).show()
                }
            }
        }
        .padding(vertical = 2.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
) {
    Text(
        text = "Téléphone",
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = phoneNum.ifBlank { "N/A" },
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (phoneNum.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
        if (phoneNum.isNotBlank()) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = "Appeler",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}
                                        val corrText = if (item.correspondences.isNotEmpty()) {
                                            item.correspondences.map { corr ->
                                                if (corr is Map<*, *>) {
                                                    corr.entries
                                                        .filter { (k, v) ->
                                                            val keyStr = k.toString().lowercase()
                                                            keyStr !in listOf("notes", "note", "subject", "deadline", "sujet", "delai", "delais") && v != null && v.toString().isNotBlank()
                                                        }
                                                        .joinToString(", ") { (k, v) -> "$k: $v" }
                                                } else {
                                                    corr.toString()
                                                }
                                            }.filter { it.isNotBlank() }.joinToString("; ")
                                        } else {
                                            ""
                                        }
                                        AttributeRow("Correspondances", if (corrText.isBlank()) "N/A" else corrText)
                                        AttributeRow("Surface Acceptée", "${item.accepted_area} ha")
                                        AttributeRow("Forêt", item.forest_name ?: "N/A")
                                        AttributeRow("Dossier ID", item.dossier_display_id)
                                        AttributeRow("Statut Paiement", item.payment_status)
                                        AttributeRow("Dernier Paiement", item.last_payment_date.ifBlank { "N/A" })
                                        
                                        if (item.yearly_payments.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Paiements Annuels: ${item.yearly_payments}",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        val xVal = item.x_center?.replace(",", ".")?.toDoubleOrNull()
                                        val yVal = item.y_center?.replace(",", ".")?.toDoubleOrNull()
                                        if (xVal != null && yVal != null) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Button(
                                                onClick = {
                                                    onFlyTo(yVal, xVal)
                                                    onDismiss()
                                                },
                                                modifier = Modifier.fillMaxWidth().height(36.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                            ) {
                                                Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Centrer sur la carte (Lat: $yVal, Lng: $xVal)", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InvestorsDatabaseDialog(
    investors: List<Investor>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onDismiss: () -> Unit,
    onFlyTo: (Double, Double) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredList = remember(investors, searchQuery) {
        investors.filter { item ->
            searchQuery.isBlank() ||
                item.investor_name.contains(searchQuery, ignoreCase = true) ||
                item.project_nature.contains(searchQuery, ignoreCase = true) ||
                item.circonscription.contains(searchQuery, ignoreCase = true) ||
                item.forest.orEmpty().contains(searchQuery, ignoreCase = true) ||
                item.phone.contains(searchQuery, ignoreCase = true) ||
                item.documentId.contains(searchQuery, ignoreCase = true)
        }
    }

    val totalPaidSum = remember(investors) {
        investors.sumOf { it.total_paid }
    }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Base de Données - Investisseurs 💼",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${investors.size} enregistrements | Total payé: ${"%.2f".format(totalPaidSum)} DA",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = onRefresh,
                            enabled = !isRefreshing,
                            modifier = Modifier.testTag("refresh_investors_button")
                        ) {
                            if (isRefreshing) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Actualiser avec Firebase",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("close_investors_dialog")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fermer",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar and Refresh
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Rechercher investisseur, projet, forêt...", fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("investors_search_input"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Effacer", modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        shape = RoundedCornerShape(10.dp)
                    )

                    Button(
                        onClick = onRefresh,
                        enabled = !isRefreshing,
                        modifier = Modifier.height(56.dp).testTag("mobile_refresh_investors_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isRefreshing) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onSecondary, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = "Actualiser", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Actualiser", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Content List
                if (filteredList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Aucun investisseur trouvé.",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = onRefresh,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Actualiser depuis Firebase", fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredList) { item ->
                            var expanded by remember { mutableStateOf(false) }
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expanded = !expanded }
                                    .testTag("investor_card_${item.documentId}"),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = item.investor_name.ifBlank { "Investisseur N/A" },
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "Projet: ${item.project_nature.ifBlank { "N/A" }} | Forêt: ${item.forest ?: "N/A"}",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                        
                                        val statusColor = when {
                                            item.payment_status.contains("jour", ignoreCase = true) || item.status.contains("ساري", ignoreCase = true) -> Color(0xFF10B981)
                                            else -> MaterialTheme.colorScheme.primary
                                        }
                                        Surface(
                                            color = statusColor.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = item.payment_status.ifBlank { item.status.ifBlank { "Actif" } },
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = statusColor,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Circonscription: ${item.circonscription.ifBlank { "N/A" }} | Superficie: ${item.area.ifBlank { "0" }} ha",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Payé: ${item.total_paid} DA",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF10B981)
                                        )
                                    }

                                    if (expanded) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        androidx.compose.material3.HorizontalDivider(
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        AttributeRow("Nature Projet", item.project_nature.ifBlank { "N/A" })
                                        AttributeRow("Circonscription", item.circonscription.ifBlank { "N/A" })
                                        AttributeRow("Périmètre", item.perimetre.ifBlank { "N/A" })
                                        AttributeRow("Forêt", item.forest ?: "N/A")
                                        AttributeRow("Décision", "${item.decision_number.ifBlank { "N/A" }} du ${item.decision_date.ifBlank { "N/A" }}" )
                                        
                                        val phoneNum = item.phone.trim()
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable(enabled = phoneNum.isNotBlank()) {
                                                    if (phoneNum.isNotBlank()) {
                                                        try {
                                                            val intent = Intent(Intent.ACTION_DIAL, android.net.Uri.parse("tel:$phoneNum"))
                                                            context.startActivity(intent)
                                                        } catch (e: Exception) {
                                                            Toast.makeText(context, "Impossible de composer le numéro", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                }
                                                .padding(vertical = 2.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Téléphone",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = phoneNum.ifBlank { "N/A" },
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (phoneNum.isNotBlank()) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                                                )
                                                if (phoneNum.isNotBlank()) {
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Icon(
                                                        imageVector = Icons.Default.Phone,
                                                        contentDescription = "Appeler",
                                                        tint = MaterialTheme.colorScheme.secondary,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                }
                                            }
                                        }

                                        AttributeRow("Adresse", item.address.ifBlank { "N/A" })
                                        AttributeRow("N° Pièce d'Identité", item.national_id.ifBlank { "N/A" })
                                        AttributeRow("Date Pièce ID", item.national_id_date.ifBlank { "N/A" })
                                        AttributeRow("Statut Paiement", item.payment_status.ifBlank { "N/A" })
                                        AttributeRow("Redevance Annuelle", "${item.annual_fee?.toString() ?: "N/A"} DA")
                                        AttributeRow("Montant Total", "${item.total_amount?.toString() ?: "N/A"} DA")
                                        AttributeRow("Dossier ID", item.documentId)

                                        if (item.yearly_payments.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "Paiements Annuels:",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            item.yearly_payments.forEach { (year, amount) ->
                                                Row(
                                                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp).padding(vertical = 1.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(text = "Année $year", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    Text(text = "$amount DA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                                }
                                            }
                                        }

                                        val xVal = item.x_coord.replace(",", ".").toDoubleOrNull()
                                        val yVal = item.y_coord.replace(",", ".").toDoubleOrNull()
                                        if (xVal != null && yVal != null && xVal != 0.0 && yVal != 0.0) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Button(
                                                onClick = {
                                                    onFlyTo(yVal, xVal)
                                                    onDismiss()
                                                },
                                                modifier = Modifier.fillMaxWidth().height(32.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Voir sur la carte 📍", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LotDetailItem(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp)) {
        Text(
            text = "$label: ",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = value,
            fontSize = 10.sp,
            color = Color.Black
        )
    }
}

@Composable
fun LotCompactMetric(label: String, value: String) {
    Surface(
        color = Color(0xFFFAF6F2),
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFE4D5C5)),
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 8.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            Text(text = value, fontSize = 9.sp, color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun LotsSubcollectionViewer(documentId: String) {
    val context = LocalContext.current
    var lotsList by remember { mutableStateOf<List<Map<String, Any>>?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(documentId) {
        isLoading = true
        errorMessage = null
        try {
            val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            firestore.collection("contrats")
                .document(documentId)
                .collection("lots")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    isLoading = false
                    if (querySnapshot != null) {
                        val parsed = querySnapshot.documents.map { doc ->
                            val data = doc.data?.toMutableMap() ?: mutableMapOf()
                            data["document_id_field"] = doc.id
                            data
                        }
                        lotsList = parsed
                    }
                }
                .addOnFailureListener { exception ->
                    isLoading = false
                    errorMessage = exception.localizedMessage
                }
        } catch (e: Exception) {
            isLoading = false
            errorMessage = e.localizedMessage
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Lotissement (Lots) 🪵",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            if (lotsList != null) {
                Surface(
                    color = Color(0xFFF3E9DD),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "${lotsList?.size ?: 0} lots",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (isLoading) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color(0xFF8B5A2B),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Chargement des lots...", fontSize = 11.sp, color = Color.Black)
            }
        } else if (errorMessage != null) {
            Text(
                text = "Erreur de chargement: $errorMessage",
                fontSize = 11.sp,
                color = Color.Red,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        } else if (lotsList != null) {
            if (lotsList!!.isEmpty()) {
                Text(
                    text = "Aucun lot enregistré pour cette convention.",
                    fontSize = 11.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            } else {
                lotsList!!.forEach { lot ->
                    val num = lot["numero"]?.toString() ?: lot["id"]?.toString() ?: "N/A"
                    val espece = lot["espece"]?.toString() ?: "N/A"
                    val etat = lot["etat_bois"]?.toString() ?: ""
                    val foret = lot["foret"]?.toString() ?: ""
                    val commune = lot["commune"]?.toString() ?: ""
                    val lieuDit = lot["lieu_dit"]?.toString() ?: ""
                    
                    // Quantities
                    val volBc = lot["vol_bc"]?.toString()?.toDoubleOrNull() ?: 0.0
                    val volBi = lot["vol_bi"]?.toString()?.toDoubleOrNull() ?: 0.0
                    val volBo = lot["vol_bo"]?.toString()?.toDoubleOrNull() ?: 0.0
                    val nbPerches = lot["nb_perches"]?.toString()?.toDoubleOrNull() ?: 0.0
                    val nbPerchettes = lot["nb_perchettes"]?.toString()?.toDoubleOrNull() ?: 0.0
                    val qte = lot["qte"]?.toString()?.toDoubleOrNull() ?: 0.0
                    val unite = lot["unite"]?.toString() ?: ""

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFBF7)),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color(0xFFEFE8DE)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Lot N° $num : $espece",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                if (etat.isNotBlank()) {
                                    Surface(
                                        color = Color(0xFFF3E9DD),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = etat,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            if (foret.isNotBlank() || commune.isNotBlank()) {
                                LotDetailItem("Forêt", listOfNotNull(foret.takeIf { it.isNotBlank() }, commune.takeIf { it.isNotBlank() }).joinToString(" - "))
                            }
                            if (lieuDit.isNotBlank()) {
                                LotDetailItem("Lieu-dit", lieuDit)
                            }

                            val hasVolumes = volBc > 0 || volBi > 0 || volBo > 0 || nbPerches > 0 || nbPerchettes > 0 || (qte > 0 && unite.isNotBlank())
                            if (hasVolumes) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Volume / Quantité",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                androidx.compose.foundation.layout.FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (volBc > 0) LotCompactMetric("BC", "$volBc m³")
                                    if (volBi > 0) LotCompactMetric("BI", "$volBi m³")
                                    if (volBo > 0) LotCompactMetric("BO", "$volBo m³")
                                    if (nbPerches > 0) LotCompactMetric("Perches", "${nbPerches.toInt()}")
                                    if (nbPerchettes > 0) LotCompactMetric("Perchettes", "${nbPerchettes.toInt()}")
                                    if (qte > 0) LotCompactMetric(unite.ifBlank { "Qte" }, "$qte")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun PermisSubcollectionViewer(documentId: String) {
    var permisList by remember { mutableStateOf<List<Map<String, Any>>?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(documentId) {
        isLoading = true
        errorMessage = null
        try {
            val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            firestore.collection("contrats")
                .document(documentId)
                .collection("permis")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    isLoading = false
                    if (querySnapshot != null) {
                        val parsed = querySnapshot.documents.map { doc ->
                            val data = doc.data?.toMutableMap() ?: mutableMapOf()
                            data["document_id_field"] = doc.id
                            data
                        }
                        val sorted = parsed.sortedWith { a, b ->
                            val dateA = a["date_delivrance"]?.toString() ?: a["date_ajout"]?.toString() ?: ""
                            val dateB = b["date_delivrance"]?.toString() ?: b["date_ajout"]?.toString() ?: ""
                            dateB.compareTo(dateA)
                        }
                        permisList = sorted
                    }
                }
                .addOnFailureListener { exception ->
                    isLoading = false
                    errorMessage = exception.localizedMessage
                }
        } catch (e: Exception) {
            isLoading = false
            errorMessage = e.localizedMessage
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Volume Autorisé (Permis) 📄",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            if (permisList != null) {
                Surface(
                    color = Color(0xFFD0EBE0),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "${permisList?.size ?: 0} permis",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (isLoading) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color(0xFF0F766E),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Chargement des permis...", fontSize = 11.sp, color = Color.Black)
            }
        } else if (errorMessage != null) {
            Text(
                text = "Erreur de chargement: $errorMessage",
                fontSize = 11.sp,
                color = Color.Red,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        } else if (permisList != null) {
            if (permisList!!.isEmpty()) {
                Text(
                    text = "Aucun permis de volume autorisé enregistré pour cette convention.",
                    fontSize = 11.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            } else {
                permisList!!.forEach { permit ->
                    val num = permit["numero"]?.toString() ?: permit["id"]?.toString() ?: "N/A"
                    val type = permit["type"]?.toString() ?: "Exploiter"
                    val dateDelivrance = permit["date_delivrance"]?.toString() ?: "N/A"
                    val dateExpiration = permit["date_expiration"]?.toString() ?: ""
                    
                    val detailsRaw = permit["details"] as? List<*>
                    val details = detailsRaw?.mapNotNull { it as? Map<String, Any> } ?: emptyList()

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1FAF5)),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color(0xFFD0EBE0)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Permis d'$type N° $num",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Surface(
                                    color = if (type.lowercase() == "enlever") Color(0xFFFEE2E2) else Color(0xFFFEF3C7),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = type.uppercase(),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Délivré le: ",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(
                                    text = dateDelivrance,
                                    fontSize = 10.sp,
                                    color = Color.Black
                                )
                                if (dateExpiration.isNotBlank()) {
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Expire le: ",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = dateExpiration,
                                        fontSize = 10.sp,
                                        color = Color.Black
                                    )
                                }
                            }

                            if (details.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Détails du volume autorisé par lot:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(4.dp))

                                details.forEach { detail ->
                                    val lotNum = detail["numero"]?.toString() ?: detail["lot_id"]?.toString() ?: "N/A"
                                    
                                    val volBc = detail["vol_bc"]?.toString()?.toDoubleOrNull() ?: 0.0
                                    val volBi = detail["vol_bi"]?.toString()?.toDoubleOrNull() ?: 0.0
                                    val volBo = detail["vol_bo"]?.toString()?.toDoubleOrNull() ?: 0.0
                                    val nbPerches = detail["nb_perches"]?.toString()?.toDoubleOrNull() ?: 0.0
                                    val nbPerchettes = detail["nb_perchettes"]?.toString()?.toDoubleOrNull() ?: 0.0
                                    val qte = detail["qte"]?.toString()?.toDoubleOrNull() ?: 0.0

                                    Surface(
                                        color = Color.White,
                                        shape = RoundedCornerShape(6.dp),
                                        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFD0EBE0)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(6.dp)) {
                                            Text(
                                                text = "Lot N° $lotNum",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            androidx.compose.foundation.layout.FlowRow(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                if (volBc > 0) LotCompactMetric("Vol BC", "$volBc m³")
                                                if (volBi > 0) LotCompactMetric("Vol BI", "$volBi m³")
                                                if (volBo > 0) LotCompactMetric("Vol BO", "$volBo m³")
                                                if (nbPerches > 0) LotCompactMetric("Perches", "${nbPerches.toInt()}")
                                                if (nbPerchettes > 0) LotCompactMetric("Perchettes", "${nbPerchettes.toInt()}")
                                                if (qte > 0) LotCompactMetric("Qte", "$qte")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun FichesNavetteSubcollectionViewer(documentId: String) {
    var fichesList by remember { mutableStateOf<List<Map<String, Any>>?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(documentId) {
        isLoading = true
        errorMessage = null
        try {
            val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            firestore.collection("contrats")
                .document(documentId)
                .collection("fiches_navette")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    isLoading = false
                    if (querySnapshot != null) {
                        val parsed = querySnapshot.documents.map { doc ->
                            val data = doc.data?.toMutableMap() ?: mutableMapOf()
                            data["document_id_field"] = doc.id
                            data
                        }
                        val sorted = parsed.sortedWith { a, b ->
                            val dateA = a["date_fiche"]?.toString() ?: a["date_ajout"]?.toString() ?: ""
                            val dateB = b["date_fiche"]?.toString() ?: b["date_ajout"]?.toString() ?: ""
                            dateB.compareTo(dateA)
                        }
                        fichesList = sorted
                    }
                }
                .addOnFailureListener { exception ->
                    isLoading = false
                    errorMessage = exception.localizedMessage
                }
        } catch (e: Exception) {
            isLoading = false
            errorMessage = e.localizedMessage
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Volume Exploité (Fiches Navette) 🚚",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            if (fichesList != null) {
                Surface(
                    color = Color(0xFFF3E9DD),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "${fichesList?.size ?: 0} fiches",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (isLoading) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color(0xFF8B5A2B),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Chargement des fiches navette...", fontSize = 11.sp, color = Color.Black)
            }
        } else if (errorMessage != null) {
            Text(
                text = "Erreur de chargement: $errorMessage",
                fontSize = 11.sp,
                color = Color.Red,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        } else if (fichesList != null) {
            if (fichesList!!.isEmpty()) {
                Text(
                    text = "Aucune fiche navette de volume exploité enregistrée pour cette convention.",
                    fontSize = 11.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            } else {
                fichesList!!.forEach { fiche ->
                    val numFiche = fiche["numero_fiche"]?.toString() ?: fiche["id"]?.toString() ?: "N/A"
                    val dateFiche = fiche["date_fiche"]?.toString() ?: "N/A"
                    val destination = fiche["destination"]?.toString() ?: "N/A"
                    val matriculeCamion = fiche["matricule_camion"]?.toString() ?: ""
                    val typeCamion = fiche["type_camion"]?.toString() ?: ""
                    val nombreOuvriers = fiche["nombre_ouvriers"]?.toString() ?: "0"
                    val numPermis = fiche["numero_permis"]?.toString() ?: "N/A"
                    
                    val detailsRaw = fiche["details"] as? List<*>
                    val details = detailsRaw?.mapNotNull { it as? Map<String, Any> } ?: emptyList()

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF6F2)),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color(0xFFE4D5C5)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Fiche Navette N° $numFiche",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Surface(
                                    color = Color(0xFFE4D5C5),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "EXPLOITÉ",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            
                            LotDetailItem("Date", dateFiche)
                            LotDetailItem("Destination", destination)
                            if (typeCamion.isNotBlank() || matriculeCamion.isNotBlank()) {
                                LotDetailItem("Camion", "$typeCamion ${matriculeCamion.takeIf { it.isNotBlank() }?.let { "($it)" } ?: ""}")
                            }
                            LotDetailItem("Nombre ouvriers", nombreOuvriers)
                            LotDetailItem("N° Permis Associé", numPermis)

                            if (details.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Détails du volume exploité par lot:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(4.dp))

                                details.forEach { detail ->
                                    val lotNum = detail["numero"]?.toString() ?: detail["lot_id"]?.toString() ?: "N/A"
                                    
                                    val volBc = detail["vol_bc"]?.toString()?.toDoubleOrNull() ?: 0.0
                                    val volBi = detail["vol_bi"]?.toString()?.toDoubleOrNull() ?: 0.0
                                    val volBo = detail["vol_bo"]?.toString()?.toDoubleOrNull() ?: 0.0
                                    val nbPerches = detail["nb_perches"]?.toString()?.toDoubleOrNull() ?: 0.0
                                    val nbPerchettes = detail["nb_perchettes"]?.toString()?.toDoubleOrNull() ?: 0.0
                                    val qte = detail["qte"]?.toString()?.toDoubleOrNull() ?: 0.0

                                    Surface(
                                        color = Color.White,
                                        shape = RoundedCornerShape(6.dp),
                                        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFE4D5C5)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(6.dp)) {
                                            Text(
                                                text = "Lot N° $lotNum",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            androidx.compose.foundation.layout.FlowRow(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                if (volBc > 0) LotCompactMetric("Vol BC", "$volBc m³")
                                                if (volBi > 0) LotCompactMetric("Vol BI", "$volBi m³")
                                                if (volBo > 0) LotCompactMetric("Vol BO", "$volBo m³")
                                                if (nbPerches > 0) LotCompactMetric("Perches", "${nbPerches.toInt()}")
                                                if (nbPerchettes > 0) LotCompactMetric("Perchettes", "${nbPerchettes.toInt()}")
                                                if (qte > 0) LotCompactMetric("Qte", "$qte")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class LotExploitationData(
    val lotNum: String,
    val prevuBi: Double = 0.0,
    val prevuBc: Double = 0.0,
    val prevuBo: Double = 0.0,
    val prevuPerches: Double = 0.0,
    val prevuPerchettes: Double = 0.0,
    val autoriseBi: Double = 0.0,
    val autoriseBc: Double = 0.0,
    val autoriseBo: Double = 0.0,
    val autorisePerches: Double = 0.0,
    val autorisePerchettes: Double = 0.0,
    val exploiteBi: Double = 0.0,
    val exploiteBc: Double = 0.0,
    val exploiteBo: Double = 0.0,
    val exploitePerches: Double = 0.0,
    val exploitePerchettes: Double = 0.0
)

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun ExploitationChartViewer(documentId: String) {
    var lotsList by remember { mutableStateOf<List<Map<String, Any>>?>(null) }
    var permisList by remember { mutableStateOf<List<Map<String, Any>>?>(null) }
    var fichesList by remember { mutableStateOf<List<Map<String, Any>>?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(documentId) {
        isLoading = true
        errorMessage = null
        try {
            val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            val docRef = firestore.collection("contrats").document(documentId)

            docRef.collection("lots").get().addOnCompleteListener { lotsTask ->
                if (lotsTask.isSuccessful) {
                    lotsList = lotsTask.result?.documents?.map { doc ->
                        val data = doc.data?.toMutableMap() ?: mutableMapOf()
                        data["document_id_field"] = doc.id
                        data
                    } ?: emptyList()
                }
                
                docRef.collection("permis").get().addOnCompleteListener { permisTask ->
                    if (permisTask.isSuccessful) {
                        permisList = permisTask.result?.documents?.map { doc ->
                            val data = doc.data?.toMutableMap() ?: mutableMapOf()
                            data["document_id_field"] = doc.id
                            data
                        } ?: emptyList()
                    }
                    
                    docRef.collection("fiches_navette").get().addOnCompleteListener { fichesTask ->
                        isLoading = false
                        if (fichesTask.isSuccessful) {
                            fichesList = fichesTask.result?.documents?.map { doc ->
                                val data = doc.data?.toMutableMap() ?: mutableMapOf()
                                data["document_id_field"] = doc.id
                                data
                            } ?: emptyList()
                        } else {
                            errorMessage = fichesTask.exception?.localizedMessage
                        }
                    }
                }
            }
        } catch (e: Exception) {
            isLoading = false
            errorMessage = e.localizedMessage
        }
    }

    if (isLoading) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color(0xFF8B5A2B),
                strokeWidth = 2.5.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text("Compilation des données d'exploitation...", fontSize = 12.sp, color = Color.Black)
        }
    } else if (errorMessage != null) {
        Text(
            text = "Erreur de chargement: $errorMessage",
            fontSize = 11.sp,
            color = Color.Red,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    } else if (lotsList != null && permisList != null && fichesList != null) {
        val dataList = remember(lotsList, permisList, fichesList) {
            val map = mutableMapOf<String, LotExploitationData>()

            lotsList!!.forEach { lot ->
                val num = lot["numero"]?.toString() ?: lot["id"]?.toString() ?: "N/A"
                val prevuBi = lot["vol_bi"]?.toString()?.toDoubleOrNull() ?: 0.0
                val prevuBc = lot["vol_bc"]?.toString()?.toDoubleOrNull() ?: 0.0
                val prevuBo = lot["vol_bo"]?.toString()?.toDoubleOrNull() ?: 0.0
                val prevuPerches = lot["nb_perches"]?.toString()?.toDoubleOrNull() ?: 0.0
                val prevuPerchettes = lot["nb_perchettes"]?.toString()?.toDoubleOrNull() ?: 0.0

                map[num] = LotExploitationData(
                    lotNum = num,
                    prevuBi = prevuBi,
                    prevuBc = prevuBc,
                    prevuBo = prevuBo,
                    prevuPerches = prevuPerches,
                    prevuPerchettes = prevuPerchettes
                )
            }

            permisList!!.forEach { p ->
                val type = p["type"]?.toString() ?: ""
                if (type.lowercase().contains("enlev")) {
                    val detailsRaw = p["details"] as? List<*>
                    val details = detailsRaw?.mapNotNull { it as? Map<String, Any> } ?: emptyList()
                    
                    details.forEach { d ->
                        val num = d["numero"]?.toString() ?: d["lot_id"]?.toString() ?: "N/A"
                        val volBi = d["vol_bi"]?.toString()?.toDoubleOrNull() ?: 0.0
                        val volBc = d["vol_bc"]?.toString()?.toDoubleOrNull() ?: 0.0
                        val volBo = d["vol_bo"]?.toString()?.toDoubleOrNull() ?: 0.0
                        val nbPerches = d["nb_perches"]?.toString()?.toDoubleOrNull() ?: 0.0
                        val nbPerchettes = d["nb_perchettes"]?.toString()?.toDoubleOrNull() ?: 0.0

                        val existing = map[num] ?: LotExploitationData(lotNum = num)
                        map[num] = existing.copy(
                            autoriseBi = existing.autoriseBi + volBi,
                            autoriseBc = existing.autoriseBc + volBc,
                            autoriseBo = existing.autoriseBo + volBo,
                            autorisePerches = existing.autorisePerches + nbPerches,
                            autorisePerchettes = existing.autorisePerchettes + nbPerchettes
                        )
                    }
                }
            }

            fichesList!!.forEach { f ->
                val detailsRaw = f["details"] as? List<*>
                val details = detailsRaw?.mapNotNull { it as? Map<String, Any> } ?: emptyList()
                
                details.forEach { d ->
                    val num = d["numero"]?.toString() ?: d["lot_id"]?.toString() ?: "N/A"
                    val volBi = d["vol_bi"]?.toString()?.toDoubleOrNull() ?: 0.0
                    val volBc = d["vol_bc"]?.toString()?.toDoubleOrNull() ?: 0.0
                    val volBo = d["vol_bo"]?.toString()?.toDoubleOrNull() ?: 0.0
                    val nbPerches = d["nb_perches"]?.toString()?.toDoubleOrNull() ?: 0.0
                    val nbPerchettes = d["nb_perchettes"]?.toString()?.toDoubleOrNull() ?: 0.0

                    val existing = map[num] ?: LotExploitationData(lotNum = num)
                    map[num] = existing.copy(
                        exploiteBi = existing.exploiteBi + volBi,
                        exploiteBc = existing.exploiteBc + volBc,
                        exploiteBo = existing.exploiteBo + volBo,
                        exploitePerches = existing.exploitePerches + nbPerches,
                        exploitePerchettes = existing.exploitePerchettes + nbPerchettes
                    )
                }
            }

            map.values.sortedBy { it.lotNum }
        }

        if (dataList.isEmpty()) {
            Text(
                text = "Aucune donnée d'exploitation disponible pour générer le graphique.",
                fontSize = 11.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        } else {
            var selectedLotNum by remember { mutableStateOf("Tous") }
            var selectedMetric by remember { mutableStateOf("vol_bi") }

            val lotNums = remember(dataList) { listOf("Tous") + dataList.map { it.lotNum } }

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Filtrer par Lot:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(lotNums) { lotNum ->
                        val isSelected = selectedLotNum == lotNum
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0xFF8B5A2B) else Color(0xFFEFE8DE))
                                .border(1.dp, if (isSelected) Color(0xFF8B5A2B) else Color(0xFFEFE8DE).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .clickable { selectedLotNum = lotNum }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Lot: $lotNum",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Catégorie de produit / volume:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val metrics = listOf(
                        "vol_bi" to "Vol BI (m³)",
                        "vol_bc" to "Vol BC (m³)",
                        "vol_bo" to "Vol BO (m³)",
                        "perches" to "Perches (u)",
                        "perchettes" to "Perchettes (u)"
                    )
                    metrics.forEach { (key, label) ->
                        val isSelected = selectedMetric == key
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) Color(0xFF8B5A2B) else Color(0xFFEFE8DE))
                                .clickable { selectedMetric = key }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                val activeData = remember(selectedLotNum, selectedMetric, dataList) {
                    if (selectedLotNum == "Tous") {
                        var prevu = 0.0
                        var autorise = 0.0
                        var exploite = 0.0
                        dataList.forEach { d ->
                            when (selectedMetric) {
                                "vol_bi" -> {
                                    prevu += d.prevuBi
                                    autorise += d.autoriseBi
                                    exploite += d.exploiteBi
                                }
                                "vol_bc" -> {
                                    prevu += d.prevuBc
                                    autorise += d.autoriseBc
                                    exploite += d.exploiteBc
                                }
                                "vol_bo" -> {
                                    prevu += d.prevuBo
                                    autorise += d.autoriseBo
                                    exploite += d.exploiteBo
                                }
                                "perches" -> {
                                    prevu += d.prevuPerches
                                    autorise += d.autorisePerches
                                    exploite += d.exploitePerches
                                }
                                "perchettes" -> {
                                    prevu += d.prevuPerchettes
                                    autorise += d.autorisePerchettes
                                    exploite += d.exploitePerchettes
                                }
                            }
                        }
                        Triple(prevu, autorise, exploite)
                    } else {
                        val d = dataList.firstOrNull { it.lotNum == selectedLotNum }
                        if (d != null) {
                            when (selectedMetric) {
                                "vol_bi" -> Triple(d.prevuBi, d.autoriseBi, d.exploiteBi)
                                "vol_bc" -> Triple(d.prevuBc, d.autoriseBc, d.exploiteBc)
                                "vol_bo" -> Triple(d.prevuBo, d.autoriseBo, d.exploiteBo)
                                "perches" -> Triple(d.prevuPerches, d.autorisePerches, d.exploitePerches)
                                "perchettes" -> Triple(d.prevuPerchettes, d.autorisePerchettes, d.exploitePerchettes)
                                else -> Triple(0.0, 0.0, 0.0)
                            }
                        } else {
                            Triple(0.0, 0.0, 0.0)
                        }
                    }
                }

                val (prevuVal, autoriseVal, exploiteVal) = activeData
                val suffix = if (selectedMetric.startsWith("vol")) " m³" else " u"

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE4D5C5)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Lot ${selectedLotNum} - Comparatif Volumes ($suffix)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        val maxVal = maxOf(prevuVal, autoriseVal, exploiteVal, 1.0)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            val prevuHeightFraction = (prevuVal / maxVal).toFloat().coerceIn(0.03f, 1f)
                            val animatedPrevuHeight by androidx.compose.animation.core.animateFloatAsState(
                                targetValue = prevuHeightFraction,
                                animationSpec = androidx.compose.animation.core.spring()
                            )
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = String.format("%.2f", prevuVal) + suffix,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight(animatedPrevuHeight)
                                        .width(28.dp)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(Color(0xFF8B5A2B))
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Prévu", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8B5A2B))
                            }

                            val autoriseHeightFraction = (autoriseVal / maxVal).toFloat().coerceIn(0.03f, 1f)
                            val animatedAutoriseHeight by androidx.compose.animation.core.animateFloatAsState(
                                targetValue = autoriseHeightFraction,
                                animationSpec = androidx.compose.animation.core.spring()
                            )
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = String.format("%.2f", autoriseVal) + suffix,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight(animatedAutoriseHeight)
                                        .width(28.dp)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(Color(0xFF0F766E))
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Autorisé", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F766E))
                            }

                            val exploiteHeightFraction = (exploiteVal / maxVal).toFloat().coerceIn(0.03f, 1f)
                            val animatedExploiteHeight by androidx.compose.animation.core.animateFloatAsState(
                                targetValue = exploiteHeightFraction,
                                animationSpec = androidx.compose.animation.core.spring()
                            )
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = String.format("%.2f", exploiteVal) + suffix,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight(animatedExploiteHeight)
                                        .width(28.dp)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(Color(0xFF2E7D32))
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Exploité", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        androidx.compose.material3.HorizontalDivider(color = Color(0xFFE4D5C5).copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(10.dp))

                        val pcAutoriseOfPrevu = if (prevuVal > 0) (autoriseVal / prevuVal) * 100.0 else 0.0
                        val pcExploiteOfAutorise = if (autoriseVal > 0) (exploiteVal / autoriseVal) * 100.0 else 0.0
                        val pcExploiteOfPrevu = if (prevuVal > 0) (exploiteVal / prevuVal) * 100.0 else 0.0
                        
                        val restantAExploiter = (autoriseVal - exploiteVal).coerceAtLeast(0.0)
                        val restantAAutoriser = (prevuVal - autoriseVal).coerceAtLeast(0.0)

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Taux d'autorisation par permis", fontSize = 11.sp, color = Color.Gray)
                                Text(
                                    text = String.format("%.1f", pcAutoriseOfPrevu) + " % du prévu",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F766E)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Taux d'exploitation des permis", fontSize = 11.sp, color = Color.Gray)
                                Text(
                                    text = String.format("%.1f", pcExploiteOfAutorise) + " % de l'autorisé",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Taux de réalisation global", fontSize = 11.sp, color = Color.Gray)
                                Text(
                                    text = String.format("%.1f", pcExploiteOfPrevu) + " % du prévu",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            androidx.compose.material3.HorizontalDivider(color = Color(0xFFE4D5C5).copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Volume restant à autoriser", fontSize = 11.sp, color = Color.Gray)
                                Text(
                                    text = String.format("%.2f", restantAAutoriser) + suffix,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF8B5A2B)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Volume restant à exploiter (autorisé)", fontSize = 11.sp, color = Color.Gray)
                                Text(
                                    text = String.format("%.2f", restantAExploiter) + suffix,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BoisTabsSection(documentId: String, borderColor: Color) {
    var isExpanded by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Lots, 1: Permis, 2: Fiches, 3: Suivi

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF6F2)),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Collapsible Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Réduire" else "Développer",
                        tint = Color(0xFF8B5A2B),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Détails Techniques (Lots, Permis, Fiches) 📂",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                Surface(
                    color = Color(0xFFF3E9DD),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (isExpanded) "Masquer" else "Afficher",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8B5A2B),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.material3.HorizontalDivider(color = borderColor.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(8.dp))

                // Custom Tabs / Onglets (4 Tabs now!)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val tabs = listOf(
                        Triple(0, "Lots 🪵", Color(0xFFFDFBF7)),
                        Triple(1, "Permis 📄", Color(0xFFF1FAF5)),
                        Triple(2, "Fiches 🚚", Color(0xFFFAF6F2)),
                        Triple(3, "Suivi 📊", Color(0xFFF5F5F5))
                    )

                    tabs.forEach { (index, title, _) ->
                        val isSelected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) Color(0xFF8B5A2B) else Color(0xFFEFE8DE))
                                .clickable { selectedTab = index }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Display Selected Content
                when (selectedTab) {
                    0 -> LotsSubcollectionViewer(documentId = documentId)
                    1 -> PermisSubcollectionViewer(documentId = documentId)
                    2 -> FichesNavetteSubcollectionViewer(documentId = documentId)
                    3 -> ExploitationChartViewer(documentId = documentId)
                }
            }
        }
    }
}

@Composable
fun ContratAttributeRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun ContratsDatabaseDialog(
    contrats: List<Contrat>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onDismiss: () -> Unit,
    onFlyTo: (Double, Double) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredList = remember(contrats, searchQuery) {
        contrats.filter { item ->
            searchQuery.isBlank() ||
                item.exploitant.contains(searchQuery, ignoreCase = true) ||
                item.name.contains(searchQuery, ignoreCase = true) ||
                item.type.contains(searchQuery, ignoreCase = true) ||
                item.circonscription.contains(searchQuery, ignoreCase = true) ||
                item.adresse.contains(searchQuery, ignoreCase = true) ||
                item.tel.contains(searchQuery, ignoreCase = true) ||
                item.documentId.contains(searchQuery, ignoreCase = true)
        }
    }

    val totalAmountSum = remember(contrats) {
        contrats.sumOf { item -> item.montant.replace(",", ".").replace(" ", "").trim().toDoubleOrNull() ?: 0.0 }
    }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.tertiaryContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Explore,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Base de Données - Conventions 📜",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${contrats.size} conventions | Total montant: ${"%,.2f".format(totalAmountSum)} DA",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = onRefresh,
                            enabled = !isRefreshing,
                            modifier = Modifier.testTag("refresh_contrats_button")
                        ) {
                            if (isRefreshing) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Actualiser avec Firebase",
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("close_contrats_dialog")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fermer",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar and Refresh
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "Rechercher exploitant, convention...",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        fontSize = 11.sp
                                    )
                                }
                                androidx.compose.foundation.text.BasicTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    singleLine = true,
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 12.sp
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("contrats_search_input"),
                                    cursorBrush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)
                                )
                            }
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { searchQuery = "" },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Effacer",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = onRefresh,
                        enabled = !isRefreshing,
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("mobile_refresh_contrats_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        if (isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onTertiary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Actualiser", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Content List
                if (filteredList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Aucune convention d'exploitation trouvée.",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = onRefresh,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Actualiser depuis Firebase", fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredList) { item ->
                            var expanded by remember { mutableStateOf(false) }
                            
                            // Determine type and colors with beautiful, high-contrast, eye-friendly, accessible colors
                            val isBois = item.type.contains("bois", ignoreCase = true) || item.documentId.contains("bois", ignoreCase = true)
                            val containerBg = if (isBois) Color(0xFFFAF6F2) else Color(0xFFF0FAF6) // Soothing light pastel tones (very easy on eyes)
                            val badgeBg = if (isBois) Color(0xFFF3E9DD) else Color(0xFFE2F3EB)     // Soft matching backgrounds for tags
                            val borderColor = if (isBois) Color(0xFFE4D5C5) else Color(0xFFD0EBE0) // Smooth, visible borders
                            val labelText = if (isBois) "Bois 🪵" else "Autres Produits 🌿"
                            
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expanded = !expanded }
                                    .testTag("contrat_card_${item.documentId}"),
                                colors = CardDefaults.cardColors(
                                    containerColor = containerBg
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    borderColor
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = item.exploitant.ifBlank { "Exploitant N/A" },
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Color.Black
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "Convention: ${item.name.ifBlank { "N/A" }} | District: ${item.circonscription.ifBlank { "N/A" }}",
                                                fontSize = 11.sp,
                                                color = Color.Black,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                        
                                        Surface(
                                            color = badgeBg,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = labelText,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Lancement: ${item.date_lancement.ifBlank { "N/A" }} | Délai: ${item.delai_initial.ifBlank { "N/A" }} jours",
                                            fontSize = 11.sp,
                                            color = Color.Black
                                        )
                                        Text(
                                            text = "${item.montant.ifBlank { "0" }} DA",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                    }

                                    if (expanded) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        androidx.compose.material3.HorizontalDivider(
                                            color = borderColor
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        ContratAttributeRow("Exploitant", item.exploitant.ifBlank { "N/A" })
                                        ContratAttributeRow("Réf Convention", item.name.ifBlank { "N/A" })
                                        ContratAttributeRow("Type de Convention", item.type.ifBlank { "N/A" })
                                        ContratAttributeRow("Circonscription", item.circonscription.ifBlank { "N/A" })
                                        ContratAttributeRow("Date Convention", item.date_convention.ifBlank { "N/A" })
                                        ContratAttributeRow("Date Lancement", item.date_lancement.ifBlank { "N/A" })
                                        ContratAttributeRow("Délai Initial", "${item.delai_initial.ifBlank { "N/A" }} jours")
                                        ContratAttributeRow("Identifiant", item.id.ifBlank { "N/A" })

                                        // Support the rich list structures if present
                                        if (isBois) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            androidx.compose.material3.HorizontalDivider(color = borderColor)
                                            Spacer(modifier = Modifier.height(8.dp))
                                            BoisTabsSection(documentId = item.documentId, borderColor = borderColor)
                                            Spacer(modifier = Modifier.height(8.dp))
                                            androidx.compose.material3.HorizontalDivider(color = borderColor)
                                            Spacer(modifier = Modifier.height(8.dp))

                                            Spacer(modifier = Modifier.height(8.dp))
                                            androidx.compose.material3.HorizontalDivider(color = borderColor)
                                            Spacer(modifier = Modifier.height(8.dp))

                                            Spacer(modifier = Modifier.height(8.dp))
                                        } else if (item.lots != null) {
                                            ContratAttributeRow("Lots / Parcelles", item.lots.toString())
                                        }
                                        if (item.nettoyage != null) {
                                            ContratAttributeRow("Détails Nettoyage", item.nettoyage.toString())
                                        }
                                        if (item.paiements != null) {
                                            ContratAttributeRow("Suivi Paiements", item.paiements.toString())
                                        }
                                        if (item.fiches_navette != null && !isBois) {
                                            ContratAttributeRow("Fiches Navette", item.fiches_navette.toString())
                                        }
                                        if (item.permis != null && !isBois) {
                                            ContratAttributeRow("Permis d'Exploiter", item.permis.toString())
                                        }
                                        
                                        ContratAttributeRow("ID Document", item.documentId)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
