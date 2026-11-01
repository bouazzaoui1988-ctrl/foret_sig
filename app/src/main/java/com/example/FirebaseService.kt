package com.example

import android.location.Location
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.database.FirebaseDatabase

class FirebaseService(
    private val firestore: FirebaseFirestore?,
    private val realtimeDb: FirebaseDatabase?
) {
    // Utility to sanitize email for RTDB keys (keys cannot contain '.', '#', '$', '[', ']', or '/')
    private fun sanitizeEmail(email: String): String {
        return email.replace(".", ",")
            .replace("@", "_")
            .replace("#", "_")
            .replace("$", "_")
            .replace("[", "_")
            .replace("]", "_")
            .replace("/", "_")
    }

    fun saveSharedPosition(email: String, location: Location, onComplete: (Boolean) -> Unit = {}) {
        val data = mapOf(
            "email" to email,
            "latitude" to location.latitude,
            "longitude" to location.longitude,
            "timestamp" to System.currentTimeMillis()
        )

        var successCount = 0
        var totalCount = 0
        if (realtimeDb != null) totalCount++
        if (firestore != null) totalCount++

        if (totalCount == 0) {
            onComplete(false)
            return
        }

        val checkCompletion = { success: Boolean ->
            if (success) successCount++
            onComplete(successCount > 0)
        }

        val key = sanitizeEmail(email)
        
        if (realtimeDb != null) {
            realtimeDb.getReference("shared_positions").child(key)
                .setValue(data)
                .addOnSuccessListener {
                    Log.d("FirebaseService", "Position sent to Realtime Database: $data")
                    checkCompletion(true)
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseService", "Failed to send position to Realtime Database: ${e.localizedMessage}")
                    checkCompletion(false)
                }
        }

        if (firestore != null) {
            firestore.collection("shared_positions").document(key)
                .set(data)
                .addOnSuccessListener {
                    Log.d("FirebaseService", "Position sent to Firestore: $data")
                    checkCompletion(true)
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseService", "Failed to send position to Firestore: ${e.localizedMessage}")
                    checkCompletion(false)
                }
        }
    }

    fun deleteSharedPosition(email: String, onComplete: (Boolean) -> Unit = {}) {
        var successCount = 0
        var totalCount = 0
        if (realtimeDb != null) totalCount++
        if (firestore != null) totalCount++

        if (totalCount == 0) {
            onComplete(false)
            return
        }

        val checkCompletion = { success: Boolean ->
            if (success) successCount++
            onComplete(successCount > 0)
        }

        val key = sanitizeEmail(email)

        if (realtimeDb != null) {
            realtimeDb.getReference("shared_positions").child(key)
                .removeValue()
                .addOnSuccessListener {
                    Log.d("FirebaseService", "Position deleted from Realtime Database")
                    checkCompletion(true)
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseService", "Failed to delete position from Realtime Database: ${e.localizedMessage}")
                    checkCompletion(false)
                }
        }

        if (firestore != null) {
            firestore.collection("shared_positions").document(key)
                .delete()
                .addOnSuccessListener {
                    Log.d("FirebaseService", "Position deleted from Firestore")
                    checkCompletion(true)
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseService", "Failed to delete position from Firestore: ${e.localizedMessage}")
                    checkCompletion(false)
                }
        }
    }

    fun saveVectorElement(element: VectorElement, onComplete: (Boolean) -> Unit = {}) {
        if (firestore == null) {
            onComplete(false)
            return
        }

        firestore.collection("vector_elements").document(element.id)
            .set(element)
            .addOnSuccessListener {
                Log.d("FirebaseService", "Vector element saved to Firestore")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseService", "Failed to save vector element to Firestore: ${e.localizedMessage}")
                onComplete(false)
            }
    }

    fun deleteVectorElement(elementId: String, onComplete: (Boolean) -> Unit = {}) {
        if (firestore == null) {
            onComplete(false)
            return
        }

        firestore.collection("vector_elements").document(elementId)
            .delete()
            .addOnSuccessListener {
                Log.d("FirebaseService", "Vector element deleted from Firestore")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseService", "Failed to delete vector element from Firestore: ${e.localizedMessage}")
                onComplete(false)
            }
    }
}
