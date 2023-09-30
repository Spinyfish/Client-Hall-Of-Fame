#!/bin/bash

java_path=./jre/zulu-8.jdk/Contents/Home/bin/java

chmod +x $java_path
$java_path -XX:+DisableAttachMechanism -jar Launcher.jar
