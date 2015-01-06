#!/usr/bin/env bash

_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd ${_DIR}

_version="gcf-2.7"
_url="http://software.geni.net/local-sw/real_download"
_data="software=${_version}.tar.gz&accept=I+have+read+and+accept+the+GPO+terms+of+service+and+disclaimer"
_path="omni"

if [ ! -d "${_path}" ]; then 
  curl "${_URL}" | tar -zx
fi

mkdir -p "${_path}"
curl -s --data "${_data}" "${_url}"|tar xz -C "${_path}"

export OMNI_HOME="$(pwd)"/"${_path}"/"${_version}"
export PATH=$OMNI_HOME/src:$PATH
export CONFIG=$(pwd)/conf/omni.cfg
export REQUEST=$(pwd)/conf/test.rspec
export PYTHONPATH=$OMNI_HOME/src

cd $OMNI_HOME/acceptance_tests/AM_API/
rm -f acceptance.log
./am_api_accept.py --sleep-time 2 -V3 --rspec-file $REQUEST -c $CONFIG $@


