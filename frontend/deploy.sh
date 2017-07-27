#!/bin/sh -eux

cd apps
./build-prod.sh
cd ../

firebase -P prod deploy

