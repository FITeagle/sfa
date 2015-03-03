#!/usr/bin/env bash

TARGET=$(mktemp -d 2>/dev/null || mktemp -d -t "fiteagle")
REPO="~/.m2/repository/org/fiteagle"

echo "ARE YOU SURE TO DELETE '${TARGET}' and '${REPO}' and kill all java processes? Press 'Y'"
if [ -z "${key}" ]; then read key; fi
if [ "Y" != "${key}" ]; then exit 1; fi

echo "Killing all java processes..."
killall java 2>/dev/null
sleep 5
killall -9 java 2>/dev/null

rm -rf ${REPO};
rm -rf ${TARGET};
mkdir -p ${TARGET};
cd ${TARGET}
export WILDFLY_HOME="${TARGET}/server/wildfly"
curl -fsSL fiteagle.org/bootstrap | bash -s init deployFT2 deployFT2sfa
cd sfa
./src/test/bin/runJfed.sh

echo "You might now want to delete ${TARGET}."
