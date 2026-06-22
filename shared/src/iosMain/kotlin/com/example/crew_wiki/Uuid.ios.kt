package com.example.crew_wiki

import platform.Foundation.NSUUID

actual fun randomUuid(): String = NSUUID().UUIDString()
