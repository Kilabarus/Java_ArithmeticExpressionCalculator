package com.company;

/**
 * Узел, содержащий поле _value типа E и ссылку _next на следующий узел
 *
 * @param <E>   тип элементов
 */
public class Node<E> {
    private final E _value;
    private Node<E> _next = null;

    /**
     * Конструктор, устанавливает значение поля _value равным передаваемому параметру value
     *
     * @param value переменная, значение которой требуется присвоить полю _value
     */
    Node(E value) {
        _value = value;
    }

    /**
     * Геттер, возвращающий значение поля _value
     *
     * @return  значение поля _value
     */
    public E getValue() {
        return _value;
    }

    /**
     * Сеттер, устанавливающий ссылку _next на следующий элемент
     *
     * @param next  ссылка на узел, на который необходимо указывать ссылке _next
     */
    public void setNext(Node<E> next) {
        _next = next;
    }

    /**
     * Геттер, возвращающий ссылку на следующий узел
     *
     * @return  узел, на который указывает ссылка _next
     */
    public Node<E> getNext() {
        return _next;
    }
}
