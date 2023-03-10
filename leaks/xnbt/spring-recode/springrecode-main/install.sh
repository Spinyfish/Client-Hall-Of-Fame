#!/bin/sh

clear
echo "Installing MavenMCP"

# 1.12.1 Version
MCP_LK="http://www.modcoderpack.com/files/mcp918.zip"
MC_VEV="1.8.8"

# Downloading Beta MCP
# TODO Check if there is already and tmp folder
echo "Downloading MCP..."
rm -rf tmp/
mkdir tmp
cd tmp
wget $MCP_LK -O mcp.zip

# Unzipping MCP
echo "Unzipping MCP..."
unzip mcp.zip -d .
rm -f mcp.zip

# Executing MCP
echo "Executing MCP..."
python ./runtime/decompile.py %*
cd ..

# Copy the sources from temp to main
echo "Copying Sources..."
mkdir -p src/main/java
cp -r tmp/src/minecraft/ src/main/java


echo "Rearrange..."
mv src/main/java/Start.java src/main/java/net/minecraft/Start.java

echo "Copying workspace..."
mkdir workspace/
cp -r tmp/jars/ workspace/

# Clean Up
ECHO "Clean Up..."
rm -rf tmp

echo "Done."