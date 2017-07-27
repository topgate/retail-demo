#!/bin/sh -eux

# appsの依存パッケージインストール
cd apps && npm install && cd ..

# functionsの依存パッケージインストール
cd functions && npm install && cd ..
