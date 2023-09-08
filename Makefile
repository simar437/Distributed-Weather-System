JAVAC = javac
JAVA = java
SRC = src

JACKSON_DIR = lib/jackson
AGGREGATION_SERVER = AggregationServer
CONTENT_SERVER = ContentServer
OUT_DIR := out/production/A2_DS

default: compile

compile:
	$(JAVAC) -cp "$(JACKSON_DIR)/*" $(SRC)/*.java -d "$(OUT_DIR)"


aggregation_server: compile
	$(JAVA) -cp "$(JACKSON_DIR)/*;$(OUT_DIR)" $(AGGREGATION_SERVER)

content_server:
	$(JAVA) -cp "$(JACKSON_DIR)/*;$(OUT_DIR)" $(CONTENT_SERVER)

