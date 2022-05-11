# Transactions Statistics

Problem Statement :

We would like to have a RESTful API for our transaction statistics. The main use case for the 
API is to calculate real time statistics for the last 30 seconds of transactions. The API needs 
the following endpoints:

Specs

POST /transactions

Every Time a new transaction happened, this endpoint will be called.

Body:

{

"amount": 20.5,

"timestamp": 1652270553728

}


Where:

● amount - transaction amount

● timestamp - transaction time in epoch in millis in UTC time zone (this is not currenttimestamp) Returns: Empty body with either 201 or 204.

● 201 - in case of success

● 204 - if transaction is older than 30 seconds

Where:

● amount is a double specifying the amount

● time is a long specifying unix time format in milliseconds

GET /statistics
This is the main endpoint of this task, this endpoint have to execute in constant time and
memory (O(1)). It returns the statistic based on the transactions which happened in the last 30
seconds.

Returns:
{

"sum": x,

"avg": y,

"max":z,

"min": a,

"count": b

}

Where:

● sum is a double specifying the total sum of transaction value in the last 30 seconds

● avg is a double specifying the average amount of transaction value in the last 30
seconds

● max is a double specifying single highest transaction value in the last 30 seconds

● min is a double specifying single lowest transaction value in the last 30 seconds

● count is a long specifying the total number of transactions happened in the last 30
seconds


Requirements

For the rest api, the biggest and maybe hardest requirement is to make the GET /statistics
execute in constant time and space. The best solution would be O(1). It is very recommended to
tackle the O(1) requirement as the last thing to do as it is not the only thing which will be rated in
the code challenge.

Other requirements, which are obvious, but also listed here explicitly:

● The API have to be threadsafe with concurrent requests

● The API have to function properly, with proper result

● The project should be buildable, and tests should also complete successfully. e.g. If
maven is used, then mvn clean install should complete successfully.

● The API should be able to deal with time discrepancy, which means, at any point of time,
we could receive a transaction which have a timestamp of the past

● Make sure to send the case in memory solution without database (including in-memory
database)

● Endpoints have to execute in constant time and memory (O(1))

● Please complete the challenge using Java

****************************************************************************************************************************************

Solution : 

Java project which maintains cache of last 30 seconds transactions in a form of seconds or could be millis seconds.
Each time frame either sec or milli seconds holds all the transactions information happend in that time frame.
Time frames are calculated based on timestamp. Each time frame can be modified separatly without taking lock but for modifying same time frame object required a lock.
Liked hashmap is used to get the funtinality of remove eldest entry from cache.
At any point of time cache is having size, atmost equal to its capacity. Capacity is defined between 30 to 30000. (30 frame of a minute in seconds to 30000 frame in milli seconds).

To compile and build project run : 

    mvn clean install 

To run application use : 

    mvn spring-boot:run

To Get statistics :

    GET http://localhost:8080/transactions


To Post statistics :

    POST http://localhost:8080/transactions

    Body : raw type with JSON(application/json)

    {
      "amount": 20.5,
      "timestamp": 1652270553728
    }



