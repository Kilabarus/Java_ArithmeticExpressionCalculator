package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.InputMismatchException;

/**
 * Задача 1. Основы процедурного программирования
 * Вариант *
 *
 * Дано: String s - арифметическое выражение, содержащее цифры, знаки "+", "-", "*", "/" и скобки.
 * Написать программу-калькулятор для вычисления результата этого выражения.
 *
 * Данильченко Роман, 9 гр.
 */

public class Main {
    static Stack<Character> operatorsAndParenthesesStack = new Stack<>();
    static Stack<Double> operandsStack = new Stack<>();

    enum ArithmeticExpressionPart {
        Number,
        Operation,
        OpeningParenthesis,
        ClosingParenthesis,
        None
    }

    /**
     * Очищает стеки в случае, если не удалось высчитать результат арифметического выражения
     */
    public static void clearStacks() {
        operandsStack.clear();
        operatorsAndParenthesesStack.clear();
    }

    /**
     * Высчитывает и возвращает результат операции operation над leftOperand и rightOperand
     *
     * @param leftOperand           левый операнд
     * @param rightOperand          правый операнд
     * @param operation             операция над операндами
     * @return                      результат операции
     * @throws ArithmeticException  исключение на случай попытки деления на ноль
     */
    public static Double getOperationResult(Double leftOperand, Double rightOperand, char operation)
            throws ArithmeticException {
        switch (operation) {
            case '+':
                return leftOperand + rightOperand;
            case '-':
                return leftOperand - rightOperand;
            case '*':
                return leftOperand * rightOperand;
            default:
                if (rightOperand == 0) {
                    clearStacks();
                    throw new ArithmeticException("Попытка деления на ноль, работа программы прекращена");
                }

                return leftOperand / rightOperand;
        }
    }

    /**
     * Достает из стека с операндами 2 числа,
     *      производит над ними операцию из стека с операциями, кладёт результат обратно в стек с операндами
     */
    public static void evalLastOperation() {
        Double rightOperand;

        rightOperand = operandsStack.pop();
        operandsStack.push(getOperationResult(operandsStack.pop(), rightOperand, operatorsAndParenthesesStack.pop()));
    }

    /**
     * Высчитывает значение выражения в последних полностью считанных скобках, добавляет его в стек с операндами
     */
    public static void evalLastParenthesesExpression() {
        Double rightOperand;
        char operation;

        while (!operatorsAndParenthesesStack.isEmpty()) {
            operation = operatorsAndParenthesesStack.pop();
            if (operation == '(') {
                // Обработка унарного минуса перед скобкой
                if (operatorsAndParenthesesStack.peek() == '#') {
                    operatorsAndParenthesesStack.pop();
                    operandsStack.push(-operandsStack.pop());
                }

                return;
            }

            rightOperand = operandsStack.pop();
            operandsStack.push(getOperationResult(operandsStack.pop(), rightOperand, operation));
        }
    }

    /**
     * Высчитывает значение выражений до того момента, пока
     *      а) стек с операторами не станет пустым
     *      б) на верху стека с операторами не окажется открывающая скобка
     *      в) приоритет операции operator не станет выше операции, находящейся наверху стека
     *
     * @param operator  операция, с приоритетом которой будут сравниваться операции из стека с операциями
     */
    public static void evalOperationsWithHigherOrEqualPrecedence(char operator) {
        while (!operatorsAndParenthesesStack.isEmpty() && (operatorsAndParenthesesStack.peek() != '(')
                && (getOperatorPrecedence(operator) <= getOperatorPrecedence(operatorsAndParenthesesStack.peek()))) {
            evalLastOperation();
        }
    }

    /**
     * Возвращает приоритет операции operator, приоритеты:
     *      '+', '-' - 1
     *      '*', '/' - 2
     *
     * @param operator  оператор, приоритет которого нужно вычислить
     * @return          приоритет операции operator
     */
    public static int getOperatorPrecedence(char operator) {
        return (operator == '+' || operator == '-' ? 1 : 2);
    }

    /**
     * Проверяет, является ли передаваемый символ ch цифрой
     *
     * @param ch    символ, который необходимо проверить
     * @return      true, если символ является цифрой
     *              false в противном случае
     */
    public static boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    /**
     * Высчитывает значение арифметического выражения, содержащегося в strWithExpression
     *
     * @param strWithExpression         строка, содержащая арифметическое выражение
     * @return                          значение арифметического выражения
     * @throws InputMismatchException   исключение на случай невалидности арифметического выражения
     */
    public static Double evaluateExpression(String strWithExpression)
        throws InputMismatchException {
        char iChar;
        // StringBuilder вместо String потому что String в java - immutable тип,
        //      а когда нужно будет считать из строки число, в цикле нужно будет посимвольно добавлять новые цифры к уже имеющимся
        StringBuilder strWithNumber = new StringBuilder();
        // Последняя считанная часть арифметического выражения, её отслеживание необходимо для обработки ошибок
        ArithmeticExpressionPart lastReadPart = ArithmeticExpressionPart.None;

        for (int i = 0, strLength = strWithExpression.length(); i < strLength; ++i) {
            // Считывание и добавление в стек числа
            if (isDigit(strWithExpression.charAt(i))) {
                strWithNumber.setLength(0);

                do {
                    strWithNumber.append(strWithExpression.charAt(i++));
                } while ((i < strLength) && isDigit(strWithExpression.charAt(i)));

                // Обработка унарного минуса перед числом
                if (operatorsAndParenthesesStack.isEmpty() || (operatorsAndParenthesesStack.peek() != '#')) {
                    operandsStack.push(Double.parseDouble(strWithNumber.toString()));
                } else {
                    operatorsAndParenthesesStack.pop();
                    operandsStack.push(-Double.parseDouble(strWithNumber.toString()));
                }

                lastReadPart = ArithmeticExpressionPart.Number;
            }

            if (i >= strLength)
                break;

            // Считывание и добавление в стек операций и скобок
            iChar = strWithExpression.charAt(i);
            switch (iChar) {
                case '(' -> {
                    // Обработка случаев "..()", "..(*", "..(/"
                    if ((strWithExpression.charAt(i + 1) == ')') || (strWithExpression.charAt(i + 1) == '*') || (strWithExpression.charAt(i + 1) == '/')) {
                        clearStacks();
                        throw new InputMismatchException(
                            "Введенное выражение: " + strWithExpression + "\n" +
                            "Ошибка на " + (i + 2) + "-й позиции\n" +
                            "Возможные причины ошибки:\n" +
                            "\t1. Введены пустые скобки \"()\";\n" +
                            "\t2. После открывающих скобок ожидалась либо цифра, либо унарный '-' или '+'"
                        );
                    }

                    // Обработка случаев "..n(..)", "..(..)(..)", где n - какое-то число
                    if ((lastReadPart == ArithmeticExpressionPart.Number) || (lastReadPart == ArithmeticExpressionPart.ClosingParenthesis)) {
                        operatorsAndParenthesesStack.push('*');
                    }

                    operatorsAndParenthesesStack.push(iChar);
                    lastReadPart = ArithmeticExpressionPart.OpeningParenthesis;
                }
                case ')' -> {
                    evalLastParenthesesExpression();
                    lastReadPart = ArithmeticExpressionPart.ClosingParenthesis;
                }
                case '-', '+' -> {
                    // Обработка случаев "..@", "..@)", где @ - либо '+', либо '-',
                    //      а также случаев с 3 подряд идущими символами операций
                    if (((i + 1) < strLength) && (isDigit(strWithExpression.charAt(i + 1)) || (strWithExpression.charAt(i + 1) == '(')
                            || ((lastReadPart != ArithmeticExpressionPart.Operation) && ((strWithExpression.charAt(i + 1) != '+') || (strWithExpression.charAt(i + 1) != '-'))))) {
                        // Обработка унарного '+' или '-'
                        if (((i == 0) || (lastReadPart == ArithmeticExpressionPart.Operation) || (lastReadPart == ArithmeticExpressionPart.OpeningParenthesis))) {
                            if (iChar == '-') {
                                operatorsAndParenthesesStack.push('#');
                            }
                            // Унарный плюс просто пропускаем
                        } else {
                            // Обработка бинарного '+' или '-'
                            evalOperationsWithHigherOrEqualPrecedence(iChar);
                            operatorsAndParenthesesStack.push(iChar);
                            lastReadPart = ArithmeticExpressionPart.Operation;
                        }
                    } else {
                        clearStacks();
                        throw new InputMismatchException(
                                "Введенное выражение: " + strWithExpression + "\n" +
                                "Ошибка на " + (i + 2) + "-й позиции\n" +
                                "Возможные причины ошибки:\n" +
                                "\t1. Выражение оканчивается знаком операции;\n" +
                                "\t2. После знака операции '" + iChar + "' ожидалась либо цифра, либо открывающая скобка;\n" +
                                "\t3. Встречено 3 подряд идущих знака операции"
                        );
                    }
                }
                case '*', '/' -> {
                    // Обработка случаев "..@", "..$@", "..@)", где @ - либо '*', либо '/', $ - какое-то число или открывающая скобка
                    if (((i + 1) == strLength) || !(lastReadPart == ArithmeticExpressionPart.Number || lastReadPart == ArithmeticExpressionPart.ClosingParenthesis)
                        || (strWithExpression.charAt(i + 1) == ')')) {
                        clearStacks();
                        throw new InputMismatchException(
                            "Введенное выражение: " + strWithExpression + "\n" +
                            "Ошибка на " + (i + 1) + "-й позиции\n" +
                            "Возможные причины ошибки:\n" +
                            "\t1. Выражение оканчивается знаком операции;\n" +
                            "\t2. Перед знаком операции " + iChar + " ожидалась либо цифра, либо закрывающая скобка"
                        );
                    }

                    evalOperationsWithHigherOrEqualPrecedence(iChar);
                    operatorsAndParenthesesStack.push(iChar);
                    lastReadPart = ArithmeticExpressionPart.Operation;
                }
            }
        }

        // Высчитывание оставшегося в стеке выражения, последнее число в стеке - результат выражения
        while (operandsStack.count() > 1) {
            evalLastOperation();
        }

        return operandsStack.pop();
    }

    /**
     * Проверяет, содержит ли строка одинаковое количество открывающих и закрывающих скобок,
     *      а также проверяет, правильность их расстановки
     *
     * @param strWithParentheses    строка с арифметическим выражением, содержащим скобки
     * @return                      true, если кол-во открывающих и закрывающих скобок идентично и они правильно расставлены
     *                              false в противном случае
     */
    public static boolean containsEqualNumberOfParentheses(String strWithParentheses) {
        int numOfOpenedParentheses = 0;

        for (int i = 0, strLength = strWithParentheses.length(); (i < strLength) && (numOfOpenedParentheses >= 0); ++i) {
            switch (strWithParentheses.charAt(i)) {
                case '(' -> ++numOfOpenedParentheses;
                case ')' -> --numOfOpenedParentheses;
            }
        }

        // Если достигли конца строки, то все открывающие скобки должны были быть компенсированы закрывающими
        // Если вышли до того, как дошли до конца строки, значит закрывающая скобка стояла до незакрытой открывающей
        return numOfOpenedParentheses == 0;
    }

    /**
     * Реализует ввод строки и проверяет её содержимое на соответствие арифметическому выражению.
     * Перед вводом строки выводит передаваемую строку-сообщение msg, если она не пустая и не null*
     *
     * @param   msg                 сообщение, которое выводится пользователю перед считыванием строки
     * @return                      введённая пользователем непустая строка, удовлетворяющая критериям синтаксиса арифметического выражения
     * @throws  java.io.IOException обработка исключения ввода-вывода
     */
    public static String inputStringWithArithmeticExpression(String msg)
            throws java.io.IOException {
        BufferedReader bR = new BufferedReader(new InputStreamReader(System.in));
        String strWithExpression;

        if (msg != null && !msg.isEmpty()) {
            System.out.println(msg);
        }

        strWithExpression = bR.readLine();
        // Убираем из введенной строки все пробелы
        strWithExpression = strWithExpression.replaceAll("\\s+", "");
        // Проверяем введенную строку на пустоту,
        //      на содержание символов, не относящихся к арифметическому выражению, и на одинаковое количество открывающих и закрывающих скобок
        while (strWithExpression.isEmpty()
                || !(strWithExpression.matches("[()0123456789+\\-*/]+") && containsEqualNumberOfParentheses(strWithExpression))) {
            System.out.println("Арифметическое выражение введено с ошибкой, повторите ввод");
            strWithExpression = bR.readLine();
            strWithExpression = strWithExpression.replaceAll("\\s+", "");
        }

        return strWithExpression;
    }

    public static void main(String[] args)
        throws IOException {
        try {
            String strWithExpression = inputStringWithArithmeticExpression(
                    "Введите арифметическое выражение, содержащее цифры, знаки \"+\", \"-\", \"*\", \"/\" и скобки\n" +
                         "Пример: -(6 * -2 + (1 + 1)(2))+ -2 * -(2 + 3) + ((-4) / 2)");

            System.out.println("Результат введенного выражения: " + evaluateExpression(strWithExpression));
        }
        catch (InputMismatchException | ArithmeticException e) {
            System.out.println(e.getMessage());
        }
    }
}
