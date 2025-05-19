package com.example.yigo.model

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage

object SupabaseService {

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = "https://gvnnxnwwnxsnwdrliinh.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imd2bm54bnd3bnhzbndkcmxpaW5oIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0NTkzMTY1OSwiZXhwIjoyMDYxNTA3NjU5fQ.Yb4nfoWS50fOLJEwyBMfLREIDyWcVucH-lQsUpXo5pQ"
        ) {
            install(io.github.jan.supabase.auth.Auth)
            install(io.github.jan.supabase.postgrest.Postgrest)
            install(io.github.jan.supabase.storage.Storage)
        }
    }

    val auth get() = client.auth
    val database get() = client.postgrest
    val storage get() = client.storage
}