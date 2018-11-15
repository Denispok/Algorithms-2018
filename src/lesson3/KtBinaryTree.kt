package lesson3

import java.util.SortedSet
import kotlin.NoSuchElementException

// Attention: comparable supported but comparator is not
class KtBinaryTree<T : Comparable<T>> : AbstractMutableSet<T>(), CheckableSortedSet<T> {

    private var root: Node<T>? = null

    override var size = 0
        private set

    private class Node<T>(val value: T) {

        var left: Node<T>? = null

        var right: Node<T>? = null
    }

    override fun add(element: T): Boolean {
        val closest = find(element)
        val comparison = if (closest == null) -1 else element.compareTo(closest.value)
        if (comparison == 0) {
            return false
        }
        val newNode = Node(element)
        when {
            closest == null -> root = newNode
            comparison < 0 -> {
                assert(closest.left == null)
                closest.left = newNode
            }
            else -> {
                assert(closest.right == null)
                closest.right = newNode
            }
        }
        size++
        return true
    }

    override fun checkInvariant(): Boolean =
            root?.let { checkInvariant(it) } ?: true

    private fun checkInvariant(node: Node<T>): Boolean {
        val left = node.left
        if (left != null && (left.value >= node.value || !checkInvariant(left))) return false
        val right = node.right
        return right == null || right.value > node.value && checkInvariant(right)
    }

    /**
     * Удаление элемента в дереве
     * Средняя
     */
    /*
    * Трудоемкость алгоритма = O(logN)
    * Ресурсоемкость алгоритма = O(1)
    */
    override fun remove(element: T): Boolean {
        val (node, nodeParent) = findWithParent(element) ?: return false

        fun replaceNodeBy(newNode: Node<T>?): Boolean {
            when {
                nodeParent == null -> root = newNode
                nodeParent.right == node -> nodeParent.right = newNode
                nodeParent.left == node -> nodeParent.left = newNode
            }
            size--
            return true
        }

        when {
            node.right == null && node.left == null -> return replaceNodeBy(null)
            node.right == null -> return replaceNodeBy(node.left)
            node.left == null -> return replaceNodeBy(node.right)
        }

        if (node.right!!.left == null) {
            node.right!!.left = node.left
            replaceNodeBy(node.right)
        } else {
            var removingNodeValue = node.right!!.left!!
            while (removingNodeValue.left != null) removingNodeValue = removingNodeValue.left!!
            remove(removingNodeValue.value)
            size++
            replaceNodeBy(Node(removingNodeValue.value).apply {
                left = node.left
                right = node.right
            })
        }
        return true
    }

    override operator fun contains(element: T): Boolean {
        val closest = find(element)
        return closest != null && element.compareTo(closest.value) == 0
    }

    private fun find(value: T): Node<T>? =
            root?.let { find(it, value) }

    private fun find(start: Node<T>, value: T): Node<T> {
        val comparison = value.compareTo(start.value)
        return when {
            comparison == 0 -> start
            comparison < 0 -> start.left?.let { find(it, value) } ?: start
            else -> start.right?.let { find(it, value) } ?: start
        }
    }

    private fun findWithParent(value: T): Pair<Node<T>, Node<T>?>? =
            root?.let { findWithParent(it, value) }

    // if no such element return null, if element == root return Pair(root, null)
    private fun findWithParent(start: Node<T>, value: T): Pair<Node<T>, Node<T>?>? {
        val comparison = value.compareTo(start.value)
        return when {
            comparison < 0 -> start.left?.let { if (it.value.equals(value)) Pair(it, start) else findWithParent(it, value) }
            comparison > 0 -> start.right?.let { if (it.value.equals(value)) Pair(it, start) else findWithParent(it, value) }
            else -> Pair(start, null)
        }
    }

    inner class BinaryTreeIterator : MutableIterator<T> {

        private var current: Node<T>? = null

        /**
         * Поиск следующего элемента
         * Средняя
         */
        private fun findNext(): Node<T>? {
            var newCurrent: Node<T> = current ?: return root
            if (newCurrent.left != null) newCurrent = newCurrent.left!!
            else if (newCurrent.right != null) newCurrent = newCurrent.right!!
            else {
                var parent: Node<T>
                while (true) {
                    parent = findWithParent(newCurrent.value)?.second ?: return null
                    if (parent.right == newCurrent) {
                        newCurrent = parent
                        continue
                    }
                    if (parent.right != null) {
                        newCurrent = parent.right!!
                        break
                    }
                    if (parent.left == newCurrent) {
                        newCurrent = parent
                        continue
                    }
                }
            }
            return newCurrent
        }

        override fun hasNext(): Boolean = findNext() != null

        override fun next(): T {
            current = findNext()
            return (current ?: throw NoSuchElementException()).value
        }

        /**
         * Удаление следующего элемента
         * Сложная
         */
        override fun remove() {
            TODO()
        }
    }

    override fun iterator(): MutableIterator<T> = BinaryTreeIterator()

    override fun comparator(): Comparator<in T>? = null

    /**
     * Для этой задачи нет тестов (есть только заготовка subSetTest), но её тоже можно решить и их написать
     * Очень сложная
     */
    override fun subSet(fromElement: T, toElement: T): SortedSet<T> {
        TODO()
    }

    /**
     * Найти множество всех элементов меньше заданного
     * Сложная
     */
    override fun headSet(toElement: T): SortedSet<T> {
        TODO()
    }

    /**
     * Найти множество всех элементов больше или равных заданного
     * Сложная
     */
    override fun tailSet(fromElement: T): SortedSet<T> {
        TODO()
    }

    override fun first(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.left != null) {
            current = current.left!!
        }
        return current.value
    }

    override fun last(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.right != null) {
            current = current.right!!
        }
        return current.value
    }
}
