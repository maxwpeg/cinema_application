package org.komarov_225

import com.fasterxml.jackson.annotation.JsonProperty

class Worker(
    @JsonProperty("login") private val login: String,
    @JsonProperty("password") private val password: String
) {
    constructor() : this("", "")

    fun getPassword(): String {
        return password
    }

    fun getLogin(): String {
        return login
    }
}
