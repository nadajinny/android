package com.example.crew_wiki

import java.util.UUID

actual fun randomUuid(): String = UUID.randomUUID().toString()
