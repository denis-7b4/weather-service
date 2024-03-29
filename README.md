# Weather-service

## REST weather service test task

REST сервис хранения и предоставления информации о температуре в городах.

- перечень городов (с уточнение страны, где он находится) на англ. языке задается в настройках сервиса;
- температура для городов из заданного списка запрашивается из трех публично доступных REST сервисов погоды
  (конкретные сервисы-провайдеры погоды найти самостоятельно);
- значения, полученные от сервисов для одного и того города в текущий момент, усредняются и складываются в БД
  (можно in-memory database) с текущим timestamp;
- периодичность опроса сервисов задается в настройках приложения;
- сервис должен предоставлять REST-ендпоинт, через который, указав город/страну и дату, можно получить все
  имеющиеся в БД в эту дату значения температуры для данного города;
- если дата в предыдущем запросе не указана, возвращается последнее известное значение для города
  (ака "температура сейчас");

Результат присылать в виде репозитория git с историей коммитов (без squash и удаления веток).
На логику сервиса должны быть написаны, как минимум, юнит-тесты (JUnit, Mockito). В юнит-тестах учесть краевые сценарии.

Стек - Spring Boot, Java 8.
