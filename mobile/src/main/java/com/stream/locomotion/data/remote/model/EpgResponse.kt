package com.stream.locomotion.data.remote.model


data class EpgResponse(
        var channels: List<EpgChannel> = emptyList(),
        var programs: List<EpgProgram> = emptyList()
)

data class EpgChannel(
        var displayName: String = "",
        var icon: EpgIcon? = null
)

data class EpgProgram(
        var title: String = "",
        var description: String = ""
)

data class EpgIcon(
        var src: String = ""
)
