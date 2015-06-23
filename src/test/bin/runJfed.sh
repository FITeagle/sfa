#!/usr/bin/env bash

_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd ${_DIR}

_VERSION="master/422"
_URL="http://jfed.iminds.be/releases/${_VERSION}/jar/jfed_cli.tar.gz"
_PATH="jfed_cli"

if [ ! -d "${_PATH}" ]; then
  curl "${_URL}" | tar -zx
fi

java \
  -jar "${_PATH}/automated-testing.jar" \
  -c be.iminds.ilabt.jfed.lowlevel.api.test.TestAggregateManager3 \
  --authorities-file conf/cli.authorities \
  --debug \
   -p conf/cli.localhost.properties \
  --group createsliver
#  --group nonodelogin

RET=$?
echo "jfed error code ${RET}"

DIR=$(ls -td test-result*|head -n1)
if [[ $(grep " failheader" -c ./${DIR}/result.html) > 0 ]]; then
  echo "test failed!"
  #cat ./${DIR}/result.html
else
  echo "test OK"
fi
open "./${DIR}/result.html"
exit $RET
