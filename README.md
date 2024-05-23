# yt2rss - слушаем YouTube в аудио

## Описание
`yt2rss` - это проект, который позволяет конвертировать поток видео из YouTube канала в RSS фид с аудио-версиями этих выпусков. 
Это упрощает отслеживание новых видео на YouTube через RSS ридеры и позволяет прослушивать их в приложениях для подкастов.

## Функциональность
- Конвертация YouTube каналов в RSS фиды содержащие ссылки на поток аудио файлов со звуковой дорожкой этих каналов.
- Аутентификация через OAuth2 с поддержкой нескольких провайдеров (Гитхаб, Яндекс).

## Составные части
Разработка состоит из двух сервисов: y2rss и y2dnl.
Часть служащая для закачки и преобразования файла реализована сервисом https://github.com/GePi/y2dnl.

`y2rss`:
- реализует интерфейс пользователя;
- управляет списком каналов и эпизодов в них;
  - формирует список эпизодов каналов и управляет им;
  - формирует файлы RSS с аудио-версиями выпусков для каждого из каналов;
- реализует API для сервиса y2dnl;
  - `/whatdownload` - отдает список эпизодов которые необходимо скачать;
  - `/downloaded` - принимает список скаченных эпизодов со ссылками на аудио-файлы.

## Технологии
Java 17, Postgree SQL(посредством Spring Data JPA), Spring Boot 3.0.6, Docker, Maven, Liquibase, Lombok, jsoup, OAuth2(посредством Spring Security), шаблонизатор Thymeleaf. 

## Настройка
Для корректной работы приложения требуется установленная база данных на платформе PostgreSQL.
Подробности можно найти в файле [docker-compose.yml](src%2Fmain%2Fresources%2Fdocker%2Fdb%2Fdocker-compose.yml).

Для настройки базы данных и обеспечения работы приложения необходимо заполнить настройки ряда переменных (секретов).
Полный список этих переменных приведен в файле [.env.sample](src%2Fmain%2Fresources%2Fdocker%2Fdb%2F.env.sample).

Настройки могут быть заданы либо как переменные окружения операционной системы (env), 
либо посредством создания и заполнения файла секретов [.env](src%2Fmain%2Fresources%2Fdocker%2Fdb%2F.env).

Для обеспечения прозрачного переключения между файлом .env и переменными окружения в проекте 
используется библиотека [me.paulschwarz:spring-dotenv](https://github.com/paulschwarz/spring-dotenv).



 