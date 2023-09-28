JAVAC = javac
JAVA = java
SRC = src

# Determine the appropriate path separator based on the OS
ifeq ($(OS), Windows_NT)
    PATH_SEPARATOR = ;
else
    PATH_SEPARATOR = :
endif

JACKSON_DIR = lib/jackson
AGGREGATION_SERVER = AggregationServer
CONTENT_SERVER = ContentServer
OUT_DIR := out
CLIENT = GETClient

default: compile

compile:
	$(JAVAC) -cp "$(JACKSON_DIR)/*" $(SRC)/*.java -d "$(OUT_DIR)"

aggregation_server:
	$(JAVA) -cp "$(JACKSON_DIR)/*$(PATH_SEPARATOR)$(OUT_DIR)" $(AGGREGATION_SERVER) $(ARGS)

content_server:
	$(JAVA) -cp "$(JACKSON_DIR)/*$(PATH_SEPARATOR)$(OUT_DIR)" $(CONTENT_SERVER) $(ARGS)

client:
	$(JAVA) -cp "$(JACKSON_DIR)/*$(PATH_SEPARATOR)$(OUT_DIR)" $(CLIENT) $(ARGS)

.PHONY: test
test:
	bash test/test1.sh
	bash test/test2.sh
	bash test/test3.sh
	bash test/test4.sh
	bash test/test5.sh
