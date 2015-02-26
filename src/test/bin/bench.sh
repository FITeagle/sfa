#!/usr/bin/env bash

warmups=3
sleeps=1
repetitions=100
command="java -cp jfed_cli/probe-cli.jar be.iminds.ilabt.jfed.ui.commandline.probe.CommandLineClient -k conf/cli.pem --accept-self-signed"
command_am="$command --api AM3 -u https://localhost:8443/sfa/api/am/v3"
command_sa="$command --api PROTOGENI_SA -u https://localhost:8443/sfa/api/sa/v1"

echo -n "GetVersion warmup in progress..."

for i in $(seq 1 $warmups); do
  ( $command_am --output-file "getversion.out" GetVersion ) 
  cat getversion.out
done
echo ""
echo "GetVersion measurements in progress..."
for i in $(seq 1 $repetitions); do
  sleep $sleeps
  echo -n "$i "
  T="$(/usr/local/opt/coreutils/libexec/gnubin/date +%s%3N)"
  ( $command_am --output-file "getversion.out" GetVersion ) > /dev/null 2>&1 
  T="$(($(/usr/local/opt/coreutils/libexec/gnubin/date +%s%3N)-T))"
  echo ${T}
done


echo -n "GetCredential warmup in progress..."
for i in $(seq 1 $warmups); do
  echo -n "."
  ( $command_sa --output-file "user-credentials.out" GetCredential )
  cat user-credentials.out
done
echo ""
echo "GetCredential measurements in progress..."
for i in $(seq 1 $repetitions); do
  sleep $sleeps
  echo -n "$i "
  T="$(/usr/local/opt/coreutils/libexec/gnubin/date +%s%3N)"
  ( $command_sa --output-file "user-credentials.out" GetCredential ) > /dev/null 2>&1 
  T="$(($(/usr/local/opt/coreutils/libexec/gnubin/date +%s%3N)-T))"
  echo ${T}
done


echo -n "ListResources warmup in progress..."
for i in $(seq 1 $warmups); do
  echo -n "."
  ( $command_am --output-file "listresources.out" ListResources user-credentials open-multinet 1 ) 
  cat listresources.out
done
echo ""
echo "ListResources measurements in progress..."
for i in $(seq 1 $repetitions); do
  sleep $sleeps
  echo -n "$i "
  T="$(/usr/local/opt/coreutils/libexec/gnubin/date +%s%3N)"
  ( $command_am --output-file "listresources.out" ListResources user-credentials open-multinet 1 ) > /dev/null 2>&1 
  T="$(($(/usr/local/opt/coreutils/libexec/gnubin/date +%s%3N)-T))"
  echo ${T}
done


echo -n "Register warmup in progress..."
for i in $(seq 1 $warmups); do
  echo -n "."
  ( $command_sa --output-file "slice-credentials.out" Register user-credentials urn:publicid:IDN+localhost+slice+mytestslice Slice )
  cat slice-credentials.out
done
echo ""
echo "Register measurements in progress..."
for i in $(seq 1 $repetitions); do
  sleep $sleeps
  echo -n "$i "
  T="$(/usr/local/opt/coreutils/libexec/gnubin/date +%s%3N)"
  ( $command_sa --output-file "slice-credentials.out" Register user-credentials urn:publicid:IDN+localhost+slice+mytestslice Slice ) > /dev/null 2>&1 
  T="$(($(/usr/local/opt/coreutils/libexec/gnubin/date +%s%3N)-T))"
  echo ${T}
done



echo -n "Allocate warmup in progress..."
for i in $(seq 1 $warmups); do
  echo -n "."
  ( $command_am --output-file "allocate.out" Allocate slice-credentials urn:publicid:IDN+emulab.net+slice+mytestslice test.rspec ) 
  cat allocate.out
done
echo ""
echo "Allocate measurements in progress..."
for i in $(seq 1 $repetitions); do
  sleep $sleeps
  echo -n "$i "
  T="$(/usr/local/opt/coreutils/libexec/gnubin/date +%s%3N)"
  ( $command_am --output-file "allocate.out" Allocate slice-credentials urn:publicid:IDN+emulab.net+slice+mytestslice test.rspec ) > /dev/null 2>&1 
  T="$(($(/usr/local/opt/coreutils/libexec/gnubin/date +%s%3N)-T))"
  echo ${T}
done


echo -n "Provision warmup in progress..."
urn=$(grep sliverUrn allocate|sed "s/.*sliverUrn='\([^']*\)'.*/\1/")
for i in $(seq 1 $warmups); do
  echo -n "."
  ( $command_am --output-file "provision.out" Provision $urn slice-credentials open-multinet 1 )
  cat provision.out
done
echo ""
echo "Provision measurements in progress..."
for i in $(seq 1 $repetitions); do
  sleep $sleeps
  echo -n "$i "
  T="$(/usr/local/opt/coreutils/libexec/gnubin/date +%s%3N)"
  ( $command_am --output-file "provision.out" Provision $urn slice-credentials open-multinet 1 ) > /dev/null 2>&1 
  T="$(($(/usr/local/opt/coreutils/libexec/gnubin/date +%s%3N)-T))"
  echo ${T}
done


echo -n "Status warmup in progress..."
for i in $(seq 1 $warmups); do
  echo -n "."
  ( $command_am --output-file "status.out" Status $urn slice-credentials )
  cat status.out
done
echo ""
echo "Status measurements in progress..."
for i in $(seq 1 $repetitions); do
  sleep $sleeps
  echo -n "$i "
  T="$(/usr/local/opt/coreutils/libexec/gnubin/date +%s%3N)"
  ( $command_am --output-file "status.out" Status $urn slice-credentials ) > /dev/null 2>&1 
  T="$(($(/usr/local/opt/coreutils/libexec/gnubin/date +%s%3N)-T))"
  echo ${T}
done


#echo -n "Delete warmup in progress..."
#for i in $(seq 1 $warmups); do
#  echo -n "."
#  ( $command_am --output-file "delete.out" Delete $urn slice-credentials )
#  cat delete.out
#done
#echo ""
#echo "Delete measurements in progress..."
#for i in $(seq 1 $repetitions); do
#  sleep $sleeps
#  echo -n "$i "
#  T="$(/usr/local/opt/coreutils/libexec/gnubin/date +%s%3N)"
#  ( $command_am --output-file "delete.out" Delete $urn slice-credentials ) > /dev/null 2>&1 
#  T="$(($(/usr/local/opt/coreutils/libexec/gnubin/date +%s%3N)-T))"
#  echo ${T}
#done
