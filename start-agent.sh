#!/bin/bash

ETCD_HOST=etcd
ETCD_PORT=2379
ETCD_URL=http://$ETCD_HOST:$ETCD_PORT
WEIGHT_SMALL=3
WEIGHT_MEDIUM=7
WEIGHT_LARGE=8

echo ETCD_URL = $ETCD_URL

if [[ "$1" == "consumer" ]]; then
  echo "Starting consumer agent..."
  java -jar \
       -Xms2560M \
       -Xmx2560M \
       -Dtype=consumer \
       -Dserver.port=20000 \
       -Detcd.url=$ETCD_URL \
       -Dlogs.dir=/root/logs \
       /root/dists/mesh-agent.jar
elif [[ "$1" == "provider-small" ]]; then
  echo "Starting small provider agent..."
  java -jar \
       -Xms512M \
       -Xmx512M \
       -Dtype=provider \
       -Ddubbo.protocol.port=20880 \
       -Dserver.port=30000 \
       -Detcd.url=$ETCD_URL \
       -Dweight=$WEIGHT_SMALL \
       -Dlogs.dir=/root/logs \
       /root/dists/mesh-agent.jar
elif [[ "$1" == "provider-medium" ]]; then
  echo "Starting medium provider agent..."
  java -jar \
       -Xms1536M \
       -Xmx1536M \
       -Dtype=provider \
       -Ddubbo.protocol.port=20880 \
       -Dserver.port=30000 \
       -Detcd.url=$ETCD_URL \
       -Dweight=$WEIGHT_MEDIUM \
       -Dlogs.dir=/root/logs \
       /root/dists/mesh-agent.jar
elif [[ "$1" == "provider-large" ]]; then
  echo "Starting large provider agent..."
  java -jar \
       -Xms2560M \
       -Xmx2560M \
       -Dtype=provider \
       -Ddubbo.protocol.port=20880 \
       -Dserver.port=30000 \
       -Detcd.url=$ETCD_URL \
       -Dweight=$WEIGHT_LARGE \
       -Dlogs.dir=/root/logs \
       /root/dists/mesh-agent.jar
else
  echo "Unrecognized arguments, exit."
  exit 1
fi
