package com.stream.locomotion.domain.usecase

class SwitchStreamUrl {
    operator fun invoke(urls: List<String>, currentIndex: Int): String? {
        val nextIndex = currentIndex + 1
        return if (nextIndex in urls.indices) urls[nextIndex] else null
    }
}
