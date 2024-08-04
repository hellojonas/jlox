APP := Lox
CLASSPATH := target
MAIN_SRC := com/hjonas/lox/$(APP).java
MAIN := com.hjonas.lox.Lox
BIN := $(CLASSPATH)/com/app/$(APP).class

run: $(BIN)
	java -cp $(CLASSPATH) $(MAIN)

$(BIN):
	javac -d $(CLASSPATH) $(MAIN_SRC)

build:
	javac -d $(CLASSPATH) $(MAIN_SRC)

clean:
	rm -rf ./target
