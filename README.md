
# TP Programacion con Objetos Avanzada

#### Modelo de clases


#### Modelo de datos



## API Reference

#### Create new user

```
  POST /api/user
```
Body
```
{
"name":  "Jason",
"email":  "test@test.com",
"points":  50
}
```
Response
```
{
"userId":  7
}
```

#### Get user by id

```
  GET /api/user/${userId}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `userId`      | `long` | Id of user |

Response
```
{
"id":  5,
"name":  "Jason",
"email":  "test@test.com",
"points":  30
}
```

#### Create new item

```
  POST /api/item
```
Body
```
{
"name": "balde",
"value": 5
}
```
Response
```
{
"itemId":  7
}
```

#### Get item by id

```
  GET /api/item/${itemId}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `itemId`      | `long` | Id of item|

Response
```
{
"id": 2,
"name": "balde",
"value": 5
}
```

#### Create new transaction

```
  POST /api/transaction
```
Body
```
{
"user_id": 5,
"items": [1, 1]
}
```
Response
```
{
"transactionId":  7
}
```

#### Get transaction by id

```
  GET /api/transaction/${transactionId}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `transactionId`      | `long` | Id of transaction|

Response
```
{
"id": 6,
"userId": 4,
"items": [1, 1],
"value": 20,
"status": "OPEN"
}
```
