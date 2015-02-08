[![Build Status](https://travis-ci.org/FITeagle/sfa.svg?branch=master)](https://travis-ci.org/FITeagle/sfa)
[![Coverage Status](https://coveralls.io/repos/FITeagle/sfa/badge.svg?branch=master)](https://coveralls.io/r/FITeagle/sfa?branch=master)

Slice-based Federation Architecture
===================================

Requirements
------------
 1. Successfully deployed core modules
 2. Successfully deployed resource adapter module (e.g. Motor)
 3. Running SPARQL server

Installation
------------
    mvn clean wildfly:deploy 

API
---

    https://localhost:8443/sfa/api/am/v3
    https://localhost:8443/sfa/api/sa/v1

Testing
-------

    ./src/test/bin/runJfed.sh

Remark
------

This module is using an own patched version of the redstone xmlrpc to support method without a prefix.