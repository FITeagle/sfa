[![Build Status](https://travis-ci.org/FITeagle/sfa.svg?branch=master)](https://travis-ci.org/FITeagle/sfa)

Slice-based Federation Architecture
===================================

Requirements
------------
 1. Successfully installed tripletStoreAccessor module
 2. Successfully deployed resourceAdapterManager module
 3. Successfully deployed federationManager module
 4. Successfully deployed reservation module
 5. Successfully deployed orchestrator module
 6. Successfully installed adapters/abstract module
 7. Successfully deployed resource adapter module (e.g. Motor)


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