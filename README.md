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

APIs
----

    https://localhost:8443/sfa/api/am/v3
    https://localhost:8443/sfa/api/sa/v1
    

Configuration
----
 If you want to get the correct URL to your SFA-API when you call a GetVersion, you should add the URL where your Server  is accessable to the 'sfa.properties'-file in /home/$user/.fiteagle/ .
 
 For Example your domain is https://federation.your-domain.com ,just replace 'localhost' at the field "url" with
 'https://federation.your-domain.com'.
 
Testing
-------

### SFA acceptance tests
   ./src/test/bin/runJfed.sh
   
### Complete clean setup and test
   curl -fksSL https://raw.githubusercontent.com/FITeagle/sfa/master/src/test/bin/acceptanceTest.sh | bash -s

Remark
------

This module is using an own patched version of the redstone xmlrpc to support method without a prefix.
