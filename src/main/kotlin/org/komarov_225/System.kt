package org.komarov_225
import org.mindrot.jbcrypt.BCrypt
import java.time.LocalDate
import java.time.LocalTime
import kotlin.contracts.contract

class System(var workers: MutableList<Worker>, var sessions: MutableList<MovieSession>) {
    constructor(): this(mutableListOf(), mutableListOf())
    fun authorizateWorker(): Int {
        while (true) {
            println("Авторизация в системе.")
            println("Выберите действие:")
            println("1. Войти в систему")
            println("2. Зарегистрироваться")
            println("3. Отмена.")
            val input = readln()
            when (input) {
                "1" -> {
                    return loginWorker()
                }
                "2" -> {
                    return registerWorker()
                }
                "3" -> {
                    return -1
                }
                else -> {
                    println("Неверный ввод, попробуйте еще раз.")
                }
            }
        }

    }
    private fun registerWorker(): Int {
        var login: String
        while (true) {
            println("Введите логин пользователя:")
            login = readln()
            val worker = workers.find { it.getLogin() == login }
            if (worker != null || login == "") {
                if (worker != null) {
                    println("Пользователь с таким логином уже зарегистрирован в системе.")
                } else {
                    println("Логин не может быть пустым.")
                }
                val check = promptForRetryOrCancel("логина")
                if (check == "n") {
                    return 1
                }
            } else {
                break
            }
        }
        var password: String
        while (true) {
            println("Введите пароль пользователя:")
            password = readln()
            if (password == "") {
                println("Пароль не может быть пустым.")
                val check = promptForRetryOrCancel("пароля")
                if (check == "n") {
                    return 1
                }
            } else {
                break
            }
        }
        val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
        val newWorker = Worker(login, hashedPassword)
        workers.add(newWorker)
        println("Пользователь с логином $login успешно зарегистрирован.")
        return 0
    }

    private fun correctLogin(login: String): Boolean {
        val worker = workers.find { it.getLogin() == login }
        if (worker == null) {
            println("Пользователь с таким логином не зарегистрирован в системе.")
            return false
        }
        return true
    }

    private fun authenticateWorker(login: String, password: String): Boolean {
        val worker = workers.find { it.getLogin() == login }
        if (worker == null) {
            println("Пользователь с таким логином не зарегистрирован в системе.")
            return false
        }
        val authenticateSuccess = BCrypt.checkpw(password, worker.getPassword())
        if (!authenticateSuccess) {
            println("Неверный пароль.")
        }
        return authenticateSuccess
    }

    private fun loginWorker(): Int {
        var login: String
        while (true) {
            println("Введите логин пользователя:")
            login = readln()
            if (correctLogin(login)) {
                break
            }
            val check = promptForRetryOrCancel("логина")
            if (check != "y") {
                println("Вход в систему был прерван.")
                return 1
            }
        }

        while (true) {
            println("Введите пароль пользователя:")
            val password = readln()
            val loginSuccess = authenticateWorker(login, password)
            if (loginSuccess) {
                break
            }
            val check = promptForRetryOrCancel("пароля")
            if (check != "y") {
                println("Вход в систему был прерван.")
                return 1
            }
        }
        println("Успешный вход.")
        return 0
    }
    private fun checkCrossing(newSession: MovieSession): Boolean {
        while (true) {
            if (newSession.setupDate() && newSession.setupStartTime()) {
                val crossedSessionsNumbers =  mutableListOf<Int>()
                for (i in 0..< sessions.size) {
                    if (sessions[i].date == newSession.date) {
                        if ((newSession.startTime < sessions[i].endTime && newSession.startTime > sessions[i].startTime) ||
                            (newSession.endTime < sessions[i].endTime && newSession.endTime > sessions[i].startTime)) {
                            crossedSessionsNumbers.add(i+1)
                        }
                    }
                }
                if (crossedSessionsNumbers.size != 0) {
                    println("Время нового сеанса пересекается со следующими сеансами:")
                    for (number in crossedSessionsNumbers) {
                        println(sessions[number])
                    }
                    println("Удалить эти сеансы(1), переназначить время сеанса(2) или отменить добавление сеанса(3)?")
                    val flag = readln()
                    if (flag == "1") {
                        for (number in crossedSessionsNumbers) {
                            sessions.removeAt(number-1)
                        }
                        println("Сеансы были удалены.")
                    } else if (flag == "2") {
                        continue
                    } else {
                        return false
                    }
                }
                return true
            } else {
                return false
            }
        }
    }
    fun addSession() {
        println("Запущен процесс добавления сеанса в систему.")
        var filmName: String
        while (true) {
            println("Введите название фильма:")
            filmName = readln()
            if (filmName == "") {
                println("Название фильма не может быть пустым.")
                val check = promptForRetryOrCancel("названия фильма")
                if (check == "n") {
                    return
                }
            } else {
                break
            }
        }
        val filmDuration: Int = getCorrectInt("длительность фильма в минутах")
        if (filmDuration == -1) {
            return
        }
        val movie = Movie(filmName, filmDuration)
        val newSession = MovieSession(movie, LocalDate.now(), LocalTime.now())
        if (checkCrossing(newSession)) {
            sessions.add(newSession)
            println("Сеанс успешно добавлен в расписание.")
        } else {
            println("Ошибка добавления сеанса.")
        }
    }

    fun removeSession() {
        println("Запущен процесс удаления сеанса из системы.")
        showSessions()
        val sessionNumber = getCorrectInt("номер сеанса, который хотите удалить")
        if (sessionNumber > 0 && sessionNumber <= sessions.size) {
            println("Сеанс ${sessions[sessionNumber - 1].movieName} успешно удален из расписания.")
            sessions.removeAt(sessionNumber - 1)
        } else {
            println("Некорректный номер сеанса.")
        }
    }

    fun editSession() {
        println("Запущен процесс изменения информации о сеансе.")
        showSessions()
        val sessionNumber = getCorrectInt("номер сеанса, который хотите изменить")
        if (sessionNumber > 0 && sessionNumber <= sessions.size) {
            while (true) {
                println("Выберите действие:")
                println("1. Изменить дату сеанса")
                println("2. Изменить время сеанса")
                println("3. Выйти в меню")
                val check = readln()
                when (check) {
                    "1" -> {
                        sessions[sessionNumber-1].setupDate()
                    }
                    "2" -> {
                        sessions[sessionNumber-1].setupStartTime()
                    }
                    "3" -> {
                        return
                    }
                    else -> {
                        val input = promptForRetryOrCancel("действия")
                        if (input == "y") {
                            continue
                        } else {
                            return
                        }
                    }
                }
            }
        } else {
            println("Некорректный номер сеанса.")
        }

    }

    fun sellATicket() {
        println("Запущен процесс продажи билетов на сеанс.")
        showSessions()
        val sessionNumber = getCorrectInt("номер сеанса, на который хотите купить билет.")
        if (sessionNumber > 0 && sessionNumber <= sessions.size) {
            val (row, seat) = getRowAndSeat(sessions[sessionNumber-1])
            if (row == -1 && seat == -1) {
                return
            }
            sessions[sessionNumber-1].buyATicket(row, seat)
        } else {
            println("Некорректный номер сеанса.")
        }
    }

    private fun getRowAndSeat(session: MovieSession): Pair<Int, Int> {
        session.showTickets()
        val row = getCorrectInt("номер ряда (1 < N < 10)")
        if (row == -1) {
            return Pair(-1,-1)
        }
        val seat = getCorrectInt("номер места (1 < N < 10)")
        if (seat == -1) {
            return Pair(-1,-1)
        }
        return Pair(row,seat)
    }
    fun returnATicket() {
        println("Запущен процесс возврата билетов на сеанс.")
        showSessions()
        val sessionNumber = getCorrectInt("номер сеанса, на который хотите вернуть билет")
        if (sessionNumber > 0 && sessionNumber <= sessions.size) {
            val (row, seat) = getRowAndSeat(sessions[sessionNumber-1])
            if (row == -1 && seat == -1) {
                return
            }
            sessions[sessionNumber-1].returnATicket(row, seat)
        } else {
            println("Некорректный номер сеанса.")
        }
    }

    private fun getCorrectInt(data: String): Int {
        var result: Int
        while (true) {
            try {
                println("Введите $data: ")
                result = readln().toInt()
            } catch (e: Exception) {
                println("Неверный формат.")
                println("Введите 'y' для повторного ввода, 'n' для отмены:")
                val check = readln()
                if (check != "y") {
                    println("Ввод был прерван.")
                    return -1
                }
                continue
            }
            break
        }
        return result
    }

    fun showSessions() {
        if (sessions.size > 0) {
            println("\t\tДоступные сеансы: ")
            for (i in 0..< sessions.size) {
                println("${i+1}. ${sessions[i]}")
            }
        } else {
            println("Доступных сеансов нет.")
        }
    }
}