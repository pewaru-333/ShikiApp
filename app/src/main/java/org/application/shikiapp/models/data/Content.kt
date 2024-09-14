package org.application.shikiapp.models.data

abstract class Content {
    abstract val id: Long
    abstract val name: String
    abstract val russian: String?
    abstract val image: Image
    abstract val url: String
    abstract val kind: String?
    abstract val airedOn: String?
    abstract val releasedOn: String?
}