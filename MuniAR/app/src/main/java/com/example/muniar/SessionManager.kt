package com.example.muniar

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("muniar_prefs", Context.MODE_PRIVATE)

    fun saveLogin(username: String) {
        prefs.edit()
            .putBoolean("isLoggedIn", true)
            .putString("username", username)
            .putBoolean("isOffline", false)
            .apply()
    }

    fun saveOffline() {
        prefs.edit()
            .putBoolean("isLoggedIn", false)
            .putBoolean("isOffline", true)
            .apply()
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("isLoggedIn", false)
    }

    fun isOffline(): Boolean {
        return prefs.getBoolean("isOffline", false)
    }

    fun getUsername(): String? {
        return prefs.getString("username", null)
    }

    // 🔥 DOWNLOAD PERSISTENCE (SAFE)

    fun saveDownloadedProject(projectName: String) {
        val set = getDownloadedProjects().toMutableSet()
        set.add(projectName.lowercase()) // normalize

        prefs.edit()
            .putStringSet("downloaded_projects", set)
            .apply()
    }

    fun getDownloadedProjects(): Set<String> {
        return prefs.getStringSet("downloaded_projects", emptySet())
            ?.toSet() ?: emptySet() // return copy
    }

    fun isProjectDownloaded(projectName: String): Boolean {
        return getDownloadedProjects().contains(projectName.lowercase())
    }
}