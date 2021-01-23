## testing framework
There are several basic abstract classes that define the global behavior for all inherited tests:
- TestsConsolePrinter is needed for a beautiful display of the results of each test;
- AbstractTestExecutor is a required ancestor for all tests, which allows you to run tests from code. Also, this class sets a global limit on the execution time of each test;
- BasicStdTest heir to AbstractTestExecutor, allows you to intercept messages displayed on the screen in tested methods in each test.

______________
Здесь находится несколько базовых абстрактных классов, которые задают глобально поведение для всех тестов-наследников:
- TestsConsolePrinter нужен для красивого вывода результатов работы каждого теста;
- AbstractTestExecutor является обязательным предком для всех тестов, что позволяет запускать тесты из кода. Также этот класс задает глобальное ограничение на время выполнения каждого теста;
- BasicStdTest наследник AbstractTestExecutor, позволяет перехватывать в каждом тесте выводимые сообщения на экран в тестируемых методах.