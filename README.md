# EV Charging Station Management System

Spring Boot 3 + MySQL + JWT.

## How to run
1. Install Java 17 and MySQL. Change the DB username/password in `src/main/resources/application.properties`.
2. Run `mvn spring-boot:run` (the database `ev_charging` is created automatically).

## Endpoints
| Method | URL | Who |
|---|---|---|
| POST | /api/auth/register | anyone |
| POST | /api/auth/login | anyone |
| GET | /api/stations, /api/stations/{id} | anyone |
| POST | /api/stations | STATION_OWNER, ADMIN |
| PUT | /api/stations/{id} | owner of the station, ADMIN |
| POST | /api/bookings | logged-in user |
| GET | /api/bookings/user/{userId} | that user, ADMIN |
| PUT | /api/bookings/{id}/cancel | booking owner, ADMIN |

Send the token as header `Authorization: Bearer <token>`.

## Sample requests
Register: `{"name":"Ravi","email":"ravi@mail.com","password":"123456","role":"CUSTOMER"}`
Add station: `{"stationName":"City Charge","location":"Erode","latitude":11.34,"longitude":77.72,"totalSlots":4,"pricePerKwh":12.5}`
Book: `{"stationId":1,"slotTime":"2030-01-01T10:00:00","kwh":20}`
