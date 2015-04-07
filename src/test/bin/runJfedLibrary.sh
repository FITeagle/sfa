#!/usr/bin/env bash

_version="157"
_hash="aa45b43d"
_file="jFed-src-${_hash}.zip"
_url="http://jfed.iminds.be/releases/master/${_version}/src/$_file"
echo ${_url}
curl -OL ${_url}
unzip ${_file} -d ${_hash}
cd ${_hash}

## this one fails in "jFed Server Scanner CLI" while executing codesigncert
mvn -DskipTests -Dmaven.test.skip=true clean compile package

cd examples
#/Users/willner/fed4fire_clients/credentials/vwall_default_new2.nop.pem
#/Users/willner/fed4fire_clients/credentials/vwall_nop.pem
mvn clean compile exec:java -Dexec.mainClass="be.iminds.ilabt.jfed.examples.ClientWrapperApiExample"
