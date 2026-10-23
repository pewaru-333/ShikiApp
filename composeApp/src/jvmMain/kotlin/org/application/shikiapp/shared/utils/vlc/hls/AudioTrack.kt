package org.application.shikiapp.shared.utils.vlc.hls

internal data class AudioTrack(val group: String, val name: String) {
    fun matches(trackName: String?): Boolean {
        val label = trackName?.substringBeforeLast(" - [") ?: return false
        return label == name || label.endsWith(" $name")
    }

    fun isPrimary(trackName: String?) = trackName?.startsWith("$group ") == true

    companion object {
        private val attribute = Regex("""(?:^|,)([A-Z0-9-]+)=("[^"]*"|[^,]*)""")

        fun fromManifest(manifest: String): List<AudioTrack> {
            val audioInfoList = ArrayList<Map<String, String>>()
            var declaredGroup: String? = null

            for (rawLine in manifest.lineSequence()) {
                val line = rawLine.trim()

                if (line.startsWith("#EXT-X-MEDIA:")) {
                    val audioInfo = attributes(line)
                    if (audioInfo["TYPE"] == "AUDIO") {
                        audioInfoList.add(audioInfo)
                    }
                } else if (declaredGroup == null && line.startsWith("#EXT-X-STREAM-INF:")) {
                    declaredGroup = attributes(line)["AUDIO"]
                }
            }

            val group = declaredGroup
                ?: audioInfoList.firstOrNull()?.get("GROUP-ID")
                ?: return emptyList()

            return buildList {
                val names = HashSet<String>()
                for (rendition in audioInfoList) {
                    if (rendition["GROUP-ID"] != group) continue

                    val name = rendition["NAME"] ?: continue
                    if (names.add(name)) {
                        add(AudioTrack(group, name))
                    }
                }
            }
        }

        private fun attributes(line: String) = attribute.findAll(line.substringAfter(':'))
            .associate { it.groupValues[1] to it.groupValues[2].removeSurrounding("\"") }
    }
}
