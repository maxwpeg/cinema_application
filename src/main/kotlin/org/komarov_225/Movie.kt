package org.komarov_225

class Movie (private val title: String, private val duration: Int) {

    constructor(): this("", 0)

    fun getTitle(): String {
        return title
    }

    fun getDuration(): Int {
        return duration
    }
}