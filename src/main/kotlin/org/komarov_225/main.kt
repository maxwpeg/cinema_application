package org.komarov_225

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import java.io.File

var SUCESSFUL_DESERIALISATION: Boolean = true
fun promptForRetryOrCancel(data: String): String {
    println("Неверный формат введенных данных.")
    println("Введите 'y' для повторного ввода $data, 'n' для отмены:")
    return readln()
}

fun deserealizeSystem(): System {
    println("Добро пожаловать в систему управления кинотеатром.")
    while (true) {
        println("Введите путь для десериализации системы или " +
                "нажмите Enter для директории по умолчанию (system.json в директории проекта):")
        var currentPath = readln()
        if (currentPath == "") {
            currentPath = "system.json"
        }
        try {
            val objectMapper: ObjectMapper = ObjectMapper().registerModule(JavaTimeModule())
            println("Десереализация из $currentPath...")
            val system: System = objectMapper.readValue(File(currentPath), System::class.java)
            println("Успешная десереализация.")
            return system
        } catch (e: Exception) {
            val check = promptForRetryOrCancel("десереализации")
            if (check == "n") {
                SUCESSFUL_DESERIALISATION = false
                return System()
            } else if(check == "y") {
                continue
            }
        }
    }

}

fun serializeSystem(system: System) {
    println("Введите путь для сериализации системы или " +
            "нажмите Enter для директории по умолчанию (system.json в директории проекта):")
    var currentPath = readln()
    if (currentPath == "") {
        currentPath = "system.json"
    }
    val objectMapper: ObjectMapper = ObjectMapper().registerModule(JavaTimeModule())
    val json: String = objectMapper.writeValueAsString(system)
    println("Cериализация в $currentPath...")
    File(currentPath).writeText(json)
    println("Успешная сериализация.")
}

fun main() {
    val system: System = deserealizeSystem()
    if (!SUCESSFUL_DESERIALISATION) {
        println("Завершение работы системы...")
        return
    }
    while (true) {
        val flag = system.authorizateWorker()
        if (flag == 0) {
            break
        } else if(flag == 1) {
            continue
        }
        return
    }
    while (true) {
        println("Выберите действие:")
        println("1. Просмотреть расписание сеансов")
        println("2. Добавить сеанс")
        println("3. Удалить сеанс")
        println("4. Изменить сеанс")
        println("5. Продать билет")
        println("6. Оформить возврат билета")
        println("7. Сменить пользователя")
        println("8. Завершить работу системы")
        val input = readln()
        println()
        when(input) {
            "1" -> {
                system.showSessions()
            }
            "2" -> {
                system.addSession()
            }
            "3" -> {
                system.removeSession()
            }
            "4" -> {
                system.editSession()
            }
            "5" -> {
                system.sellATicket()
            }
            "6" -> {
                system.returnATicket()
            }
            "7" -> {
                system.authorizateWorker()
            }
            "8" -> {
                break
            }
            else -> {
                println("Неверный ввод, попробуйте еще раз.")
            }
        }
        println()
    }
    serializeSystem(system)
    println("Программа завершила свою работу.")
}