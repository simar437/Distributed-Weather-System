JAVAC = javac
JAVA = java
SRC = src

JACKSON_DIR = lib/jackson
AGGREGATION_SERVER = AggregationServer
CONTENT_SERVER = ContentServer
OUT_DIR := out
CLIENT = GETClient

default: compile

compile:
	$(JAVAC) -cp "$(JACKSON_DIR)/*" $(SRC)/*.java -d "$(OUT_DIR)"


aggregation_server: compile
	$(eval ARGS := $(filter-out $@,$(MAKECMDGOALS)))
	$(JAVA) -cp "$(JACKSON_DIR)/*;$(OUT_DIR)" $(AGGREGATION_SERVER) $(ARGS)

content_server:
	$(eval ARGS := $(filter-out $@,$(MAKECMDGOALS)))
	$(JAVA) -cp "$(JACKSON_DIR)/*;$(OUT_DIR)" $(CONTENT_SERVER) $(ARGS)

client:
	$(eval ARGS := $(filter-out $@,$(MAKECMDGOALS)))
	$(JAVA) -cp "$(JACKSON_DIR)/*;$(OUT_DIR)" $(CLIENT) $(ARGS)