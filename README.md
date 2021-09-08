# DB
You can use the the following command to start db using docker
```shell
cd docker
docker-compose -f mysql.yml up
```

# Start the app
This is a spring boot app. you can import it to intellij and start it from there
or you can use the command line to start as spring boot app.

**Please provide the environment variables used in application.properties before starting the app**

# using the the following command to test the lock acquire and release

```shell
## you can trigger the following command multiple times to start more threads, which might make it easier to reproduce the issue
curl --location --request GET 'http://localhost:8080/test/lock/parallel?loopForSeconds=60&totalThread=10'
```

```shell
## release the lock
curl --location --request GET 'localhost:8080/test/lock/release'
```

Steps to reproduce:
1. trigger the first endpoint 3-4 times
2. hit the release endpoint multiple times, you will see multiple thread will hold the lock at the same time.

3. example:
```text
=== release the lock, size 2
=== hold the lock, thread 82
=== release the lock, size 1
=== hold the lock, thread 75
=== release the lock, size 1
=== hold the lock, thread 92
=== hold the lock, thread 98
=== hold the lock, thread 57
```
