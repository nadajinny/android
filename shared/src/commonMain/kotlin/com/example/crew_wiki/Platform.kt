package com.example.crew_wiki

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform