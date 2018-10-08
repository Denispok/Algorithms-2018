@file:Suppress("UNUSED_PARAMETER")

package lesson1

import java.io.File
import java.io.FileWriter
import java.util.regex.Pattern


/**
 * Сортировка времён
 *
 * Простая
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле с именем inputName содержатся моменты времени в формате ЧЧ:ММ:СС,
 * каждый на отдельной строке. Пример:
 *
 * 13:15:19
 * 07:26:57
 * 10:00:03
 * 19:56:14
 * 13:15:19
 * 00:40:31
 *
 * Отсортировать моменты времени по возрастанию и вывести их в выходной файл с именем outputName,
 * сохраняя формат ЧЧ:ММ:СС. Одинаковые моменты времени выводить друг за другом. Пример:
 *
 * 00:40:31
 * 07:26:57
 * 10:00:03
 * 13:15:19
 * 13:15:19
 * 19:56:14
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */

/* Трудоёмкость алгоритма = O(N^2) так как сортировка вставками имеет сложность O(N^2) + выполняется проверка
 * на соответсвие паттерну со сложностью О(N) (берется старшая степень)
 * Ресурсоёмкость алгоритма = О(N) так как мы храним в памяти только массив О(N), вспомогательные переменные для
 * сортировки О(1) и массив О(1)
 */
fun sortTimes(inputName: String, outputName: String) {
    val array = File(inputName).readLines().toTypedArray()

    val p = Pattern.compile("^\\d{2}:\\d{2}:\\d{2}\$")
    array.forEach { if (!p.matcher(it).matches()) throw IllegalArgumentException() }
    insertionSort(array)

    FileWriter(outputName, false).use { writer ->
        for (i in array) writer.write(i + '\n')
    }
}

/**
 * Сортировка адресов
 *
 * Средняя
 *
 * Во входном файле с именем inputName содержатся фамилии и имена жителей города с указанием улицы и номера дома,
 * где они прописаны. Пример:
 *
 * Петров Иван - Железнодорожная 3
 * Сидоров Петр - Садовая 5
 * Иванов Алексей - Железнодорожная 7
 * Сидорова Мария - Садовая 5
 * Иванов Михаил - Железнодорожная 7
 *
 * Людей в городе может быть до миллиона.
 *
 * Вывести записи в выходной файл outputName,
 * упорядоченными по названию улицы (по алфавиту) и номеру дома (по возрастанию).
 * Людей, живущих в одном доме, выводить через запятую по алфавиту (вначале по фамилии, потом по имени). Пример:
 *
 * Железнодорожная 3 - Петров Иван
 * Железнодорожная 7 - Иванов Алексей, Иванов Михаил
 * Садовая 5 - Сидоров Петр, Сидорова Мария
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */

/* Трудоёмкость алгоритма = O(NlogN) так как добавление элементов в streets происходить за О(NlogN)
 * ведь метод put SortedMap работает за О(logN), people.sort() также выполняется за О(NlogN) так как используется
 * быстрая сортировка (binary sort) => общая сложность O(NlogN)
 * Ресурсоёмкость алгоритма = О(N) так как в худшем случае хранение данных в SortedMap занимает О(N)
 */
class Streets {
    val streetMap = sortedMapOf<String, Street>()
    fun add(str: String) {
        val list = str.split(" ")
        val person = list[0] + " " + list[1]
        val street = list[3]
        val home = list[4].toInt()
        streetMap.getOrPut(street) { Street() }.add(person, home)
    }
}

class Street {
    val homes = sortedMapOf<Int, Home>()
    fun add(person: String, home: Int) = homes.getOrPut(home) { Home() }.add(person)
}

class Home {
    var people: MutableList<String> = arrayListOf()
    fun add(person: String) = people.add(person)
    fun sort() = people.sort()
}


fun sortAddresses(inputName: String, outputName: String) {
    val p = Pattern.compile("^\\S+ \\S+ - \\S+ \\d+\$")
    val streets = Streets()

    File(inputName).readLines().forEach {
        if (!p.matcher(it).matches()) throw IllegalArgumentException()
        streets.add(it)
    }

    streets.streetMap.forEach { it.value.homes.forEach { it.value.sort() } }

    FileWriter(outputName, false).use { writer ->
        for (i in streets.streetMap) {
            for (j in i.value.homes) {
                writer.write(i.key + " " + j.key + " - ")
                for (k in 0 until j.value.people.size - 1) {
                    writer.write(j.value.people[k] + ", ")
                }
                writer.write(j.value.people.last() + "\n")
            }
        }
    }
}

/**
 * Сортировка температур
 *
 * Средняя
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле заданы температуры различных участков абстрактной планеты с точностью до десятых градуса.
 * Температуры могут изменяться в диапазоне от -273.0 до +500.0.
 * Например:
 *
 * 24.7
 * -12.6
 * 121.3
 * -98.4
 * 99.5
 * -12.6
 * 11.0
 *
 * Количество строк в файле может достигать ста миллионов.
 * Вывести строки в выходной файл, отсортировав их по возрастанию температуры.
 * Повторяющиеся строки сохранить. Например:
 *
 * -98.4
 * -12.6
 * -12.6
 * 11.0
 * 24.7
 * 99.5
 * 121.3
 */

/* Трудоёмкость алгоритма = O(NlogN)
 * Ресурсоёмкость алгоритма = О(N)
 */
fun sortTemperatures(inputName: String, outputName: String) {
    val list = File(inputName).readLines().toMutableList()

    list.sortWith(Comparator { o1, o2 ->
        if (o1[0] == '-' && o2[0] != '-') return@Comparator -1
        if (o2[0] == '-' && o1[0] != '-') return@Comparator 1
        if (o1[0] == '-' && o2[0] == '-') {
            if (o1.length > o2.length) return@Comparator -1
            if (o1.length < o2.length) return@Comparator 1
            return@Comparator -o1.compareTo(o2)
        }
        if (o1.length > o2.length) return@Comparator 1
        if (o1.length < o2.length) return@Comparator -1
        return@Comparator o1.compareTo(o2)
    })

    FileWriter(outputName, false).use { writer ->
        for (i in list) writer.write(i + '\n')
    }
}

/**
 * Сортировка последовательности
 *
 * Средняя
 * (Задача взята с сайта acmp.ru)
 *
 * В файле задана последовательность из n целых положительных чисел, каждое в своей строке, например:
 *
 * 1
 * 2
 * 3
 * 2
 * 3
 * 1
 * 2
 *
 * Необходимо найти число, которое встречается в этой последовательности наибольшее количество раз,
 * а если таких чисел несколько, то найти минимальное из них,
 * и после этого переместить все такие числа в конец заданной последовательности.
 * Порядок расположения остальных чисел должен остаться без изменения.
 *
 * 1
 * 3
 * 3
 * 1
 * 2
 * 2
 * 2
 */
fun sortSequence(inputName: String, outputName: String) {
    TODO()
}

/**
 * Соединить два отсортированных массива в один
 *
 * Простая
 *
 * Задан отсортированный массив first и второй массив second,
 * первые first.size ячеек которого содержат null, а остальные ячейки также отсортированы.
 * Соединить оба массива в массиве second так, чтобы он оказался отсортирован. Пример:
 *
 * first = [4 9 15 20 28]
 * second = [null null null null null 1 3 9 13 18 23]
 *
 * Результат: second = [1 3 4 9 9 13 15 20 23 28]
 */
fun <T : Comparable<T>> mergeArrays(first: Array<T>, second: Array<T?>) {
    TODO()
}

