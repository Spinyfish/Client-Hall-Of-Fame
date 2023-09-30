#!/bin/bash

java_path=./jre/bin/java

chmod +x $java_path
$java_path -XX:+DisableAttachMechanism -jar Launcher.jar
