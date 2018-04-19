#!/bin/sh
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5006 -jar lib/MCU-1.0.jar
