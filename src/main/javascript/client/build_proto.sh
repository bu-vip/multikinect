#!/bin/sh

# Packs protos into json, hooked into webpack
SRC_ROOT="../../../../"
PROTO_DIR="../../proto"
OUT_FILE="public/protos.json"
node_modules/protobufjs/bin/pbjs -t json -p $SRC_ROOT -o $OUT_FILE \
  "$PROTO_DIR/frame.proto" \
  "$PROTO_DIR/realtime.proto"
