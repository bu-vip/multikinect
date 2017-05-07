#!/bin/sh

rm dist.zip
npm build
zip -r dist.zip dist
