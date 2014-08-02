[![Build Status](https://travis-ci.org/FITeagle/sfa.svg?branch=master)](https://travis-ci.org/FITeagle/sfa)

Slice-based Federation Architecture
===================================

Installation
------------
    mvn clean install wildfly:deploy 

API
---

    https://localhost:8443/sfa/api/am/v3
    https://localhost:8443/sfa/api/sa/v1

Hint
----

This module is using a patched version of the redstone xmlrpc to support method without a prefix.