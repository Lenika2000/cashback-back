## Лабораторная работа #1
Бизнес логика программных систем (ИТМО, ПИиКТ-СиППО, 3 курс).
> Вариант №115: https://letyshops.com/

Описать бизнес-процесс в соответствии с нотацией BPMN 2.0, после чего реализовать его в виде приложения на базе Spring Boot.

### Порядок выполнения работы:

1. Выбрать один из бизнес-процессов, реализуемых сайтом из варианта задания.
2. Утвердить выбранный бизнес-процесс у преподавателя.
3. Специфицировать модель реализуемого бизнес-процесса в соответствии с требованиями `BPMN 2.0`.
4. Разработать приложение на базе `Spring Boot`, реализующее описанный на предыдущем шаге бизнес-процесс. Приложение должно использовать СУБД `PostgreSQL` для хранения данных, для всех публичных интерфейсов должны быть разработаны `REST API`.
5. Разработать набор curl-скриптов, либо набор запросов для REST клиента Insomnia для тестирования публичных интерфейсов разработанного программного модуля. Запросы Insomnia оформить в виде файла экспорта.
6. Развернуть разработанное приложение на сервере `helios`.

## Лабораторная работа #2

### Доработать приложение из лабораторной работы #1, реализовав в нём управление транзакциями и разграничение доступа к операциям бизнес-логики в соответствии с заданной политикой доступа.

1. Управление транзакциями необходимо реализовать следующим образом:

- Переработать согласованные с преподавателем прецеденты (или по согласованию с ним разработать новые), объединив взаимозависимые операции в рамках транзакций.
- Управление транзакциями необходимо реализовать с помощью Spring JTA.
- В реализованных (или модифицированных) прецедентах необходимо использовать программное управление транзакциями.
- В качестве менеджера транзакций необходимо использовать Bitronix.

2. Разграничение доступа к операциям необходимо реализовать следующим образом:

- Разработать, специфицировать и согласовать с преподавателем набор привилегий, в соответствии с которыми будет разграничиваться доступ к операциям.
- Специфицировать и согласовать с преподавателем набор ролей, осуществляющих доступ к операциям бизнес-логики приложения.
- Реализовать разработанную модель разграничений доступа к операциям бизнес-логики на базе Spring Security. Информацию об учётных записах пользователей необходимо сохранять в реляционую базу данных, для аутентификации использовать JWT.

Правила выполнения работы:

Все изменения, внесённые в реализуемый бизнес-процесс, должны быть учтены в описывающей его модели, REST API и наборе скриптов для тестирования публичных интерфейсов модуля.

Доработанное приложение необходимо развернуть на сервере helios.
