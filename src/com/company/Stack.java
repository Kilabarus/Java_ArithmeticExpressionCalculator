package com.company;

import java.util.EmptyStackException;

/**
 * Структура данных, реализующая принцип LIFO
 * 
 * @param <E>   тип элементов
 */
public class Stack<E> {
    // Ссылка на верхний элемент стека
    private Node<E> _top = null;
    // Кол-во элементов в стеке
    private int _count = 0;

    /**
     * Проверяет, пуст ли стек
     * 
     * @return  true, если стек пуст
     *          false в противном случае
     */
    public boolean isEmpty() {
        return _top == null;
    }

    /**
     * Возвращает кол-во элементов в стеке
     * 
     * @return кол-во элементов в стеке
     */
    public int count() { return _count; }

    /**
     * Добавляет элемент value в стек
     * 
     * @param value элемент, который требуется добавить в стек
     */
    public void push(E value) {
        if (_top == null) {
            _top = new Node<>(value);
        } else {
            Node<E> newTop = new Node<>(value);
            newTop.setNext(_top);
            _top = newTop;
        }

        ++_count;
    }

    /**
     * Возвращает, не удаляя, верхний элемент стека
     * 
     * @return                      верхний элемент стека
     * @throws EmptyStackException  исключение на случай попытки просмотра верхнего элемента пустого стека
     */
    public E peek()
        throws EmptyStackException {
        if (_top == null) {
            throw new EmptyStackException();
        }

        return _top.getValue();
    }

    /**
     * Удаляет верхний элемент из стека и возвращает его
     * 
     * @return                      извлеченный верхний элемент стека
     * @throws EmptyStackException  исключение на случай попытки достать верхний элемент пустого стека
     */
    public E pop()
        throws EmptyStackException {
        if (_top == null) {
            throw new EmptyStackException();
        }

        E res = _top.getValue();
        _top = _top.getNext();

        --_count;
        return res;
    }

    /**
     * Полностью очищает стек
     */
    public void clear() {
        _top = null;
        _count = 0;
    }
}
