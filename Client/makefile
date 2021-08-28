CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -pthread

all: BGRSclient
	g++ -o bin/BGRSclient bin/connectionHandler.o bin/ConnectionReader.o bin/Client.o bin/KeyboardReader.o $(LDFLAGS)

BGRSclient: bin/connectionHandler.o bin/ConnectionReader.o bin/Client.o bin/KeyboardReader.o

bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/ConnectionReader.o: src/ConnectionReader.cpp
	g++ $(CFLAGS) -o bin/ConnectionReader.o src/ConnectionReader.cpp

bin/Client.o: src/Client.cpp
	g++ $(CFLAGS) -o bin/Client.o src/Client.cpp

bin/KeyboardReader.o: src/KeyboardReader.cpp
	g++ $(CFLAGS) -o bin/KeyboardReader.o src/KeyboardReader.cpp

.PHONY: clean
clean:
	rm -f bin/*