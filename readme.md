# About
NSU network course 2nd lab

[Task](http://fit.ippolitov.me/CN_2/2021/2.html)

## Requirements
Java 11 is necessary

## Usage

### Build
```bash
./gradlew shadowJar
```

### Run client
```bash
java -jar Client/build/libs/Client.jar [file to send] [server ip] [server port]
```

### Run server
```bash
java -jar Server/build/libs/Server.jar [port]
```
