MyTube
======

![Web interface](http://sismics.com/mytube/mytube.jpg)

What is MyTube?
---------------

MyTube is a lightweight app aimed to provide an alternative to the [YouTube subscriptions feed](https://www.youtube.com/feed/subscriptions) and the [Watch Later playlist](https://www.youtube.com/playlist?list=WL).

MyTube is written in Java, and may be run on any operating system with Java support.

Features
--------

- Grid view
- Responsive UI
- Based on YouTube v3 API
- Searchable

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/mytube-1.0.0-SNAPSHOT-runner`
