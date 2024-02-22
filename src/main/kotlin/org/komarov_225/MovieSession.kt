package org.komarov_225

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime


const val TEN = 10

class MovieSession(var movie: Movie, var date: LocalDate, var startTime: LocalTime) {

    var seats: Array<IntArray> = Array(TEN) { IntArray(TEN) { 0 } }
    private var availableSeats: Int = seats.size*seats[0].size
    @JsonIgnore
    val movieName: String = movie.getTitle()
    @JsonIgnore
    val endTime: LocalTime = startTime + Duration.ofMinutes(movie.getDuration().toLong())

    constructor():this(Movie(), LocalDate.now(), LocalTime.now())

    fun buyATicket(row: Int, seatNumber: Int) {
        if (row < 0 || row > 10 ) {
            println("Неверный номер ряда!($row > 10)")
        } else if (seatNumber < 0 || seatNumber > 10) {
            println("Неверный номер места!($seatNumber > 10)")
        } else if (seats[row-1][seatNumber-1] == 1) {
            println("Это место уже куплено!($row, $seatNumber)")
        } else {
            seats[row-1][seatNumber-1] = 1
            println("Билет с местом на ряду $row с номером $seatNumber успешно куплен.")
            availableSeats--
        }
    }

    fun returnATicket(row: Int, seatNumber: Int ) {
        if (LocalDate.now() > date  || (LocalDate.now() == date && LocalTime.now() > startTime)) {
            println("Этот сеанс уже начался! Нельзя вернуть билет после начала сеанса.")
            return
        }
        if (row < 0 || row > 10 ) {
            println("Неверный номер ряда!($row > 10)")
        } else if (seatNumber < 0 || seatNumber > 10) {
            println("Неверный номер места!($seatNumber > 10)")
        } else if (seats[row-1][seatNumber-1] == 0) {
            println("Это место свободно!($row, $seatNumber)")
        } else {
            seats[row-1][seatNumber-1] = 0
            println("Билет с местом на ряду $row с номером $seatNumber успешно возвращен.")
            availableSeats++
        }
    }

    fun showTickets() {
        print("\u001B[31m")
        print("[номер места]")
        print("\u001B[0m")
        println(" - место занято")
        print("\u001B[34m")
        print("[номер места]")
        print("\u001B[0m")
        println(" - место свободно")
        println("----------------------------------------------------")
        println("Фильм: ${movie.getTitle()}   Дата: $date    Время: $startTime")
        println("----------------------------------------------------")
        for (row in seats.indices) {
            print("| Ряд ")
            if (row < 9) {
                print(" ")
            }
            print("${row+1}: ")
            for (seat in seats[row].indices) {
                if (seats[row][seat] == 1) {
                    print("\u001B[31m")
                } else {
                    print("\u001B[34m")
                }
                print("[${seat+1}] ")
                print("\u001B[0m")
            }
            println("|")
        }
        println("----------------------------------------------------")
    }

    private fun checkDate(year: Int, month: Int, day: Int): Boolean {
        return try {
            LocalDate.of(year, month, day)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun setupDate() : Boolean {
        var newDate: List<Int>
        while (true) {
            println("Введите дату в формате ДД-ММ-ГГГГ")
            val input: String = readln()
            try {
                newDate = input.split("-").map { it.toInt() }
            } catch (e: Exception) {
                val check = promptForRetryOrCancel("даты")
                if (check == "n") {
                    println("Ввод даты был прерван.")
                    return false
                }
                continue
            }
            if (newDate.size != 3) {
                val check = promptForRetryOrCancel("даты")
                if (check == "n") {
                    println("Ввод даты был прерван.")
                    return false
                }
                continue
            }
            if(!checkDate(newDate[2], newDate[1], newDate[0])) {
                val check = promptForRetryOrCancel("даты")
                if (check == "n") {
                    println("Ввод даты был прерван.")
                    return false
                }
                continue
            }
            date = LocalDate.of(newDate[2], newDate[1], newDate[0])
            println("Дата сеанса успешно установлена ($date).")
            break
        }
        return true
    }

    fun setupStartTime() : Boolean {
        var newStartTime: List<Int>
        while (true) {
            println("Введите время начала фильма в формате ЧЧ:ММ")
            val input: String = readln()
            try {
                newStartTime = input.split(":").map { it.toInt() }
            } catch (e: Exception) {
                val check = promptForRetryOrCancel("времени")
                if (check != "y") {
                    println("Ввод времени был прерван.")
                    return false
                }
                continue
            }
            if (newStartTime.size != 2 || newStartTime[0] > 23 || newStartTime[1] > 59
                || newStartTime[0] < 0 || newStartTime[1] < 0 ) {
                val check = promptForRetryOrCancel("времени")
                if (check != "y") {
                    println("Ввод времени был прерван.")
                    return false
                }
                continue
            }
            startTime = LocalTime.of(newStartTime[0], newStartTime[1])
            println("Время сеанса успешно установлено ($startTime).")
            return true
        }
    }

    override fun toString(): String {
        val title = movie.getTitle()
        val duration = movie.getDuration()
        val seatsInfo = if (availableSeats > 0) ": $availableSeats" else " нет"
        return String.format("%-30s (%d мин.)\t%s %s Свободных мест%s", "\"$title\"", duration, date, startTime, seatsInfo)
    }

}
