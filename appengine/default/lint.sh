#!/bin/sh
gofmt -w .
golint ./... | grep -v "_string.go"
go vet ./...