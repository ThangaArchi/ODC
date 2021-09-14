#!/bin/ksh
#
# Startup script for backend EDU environment to establish XChanbel (whiteboard)
#

# 
# EDJARDIR gets overridden by postinstall
#
export EDJARDIR=/mnt/edesign/jars

#
# EDUJARDIR is a static value. Hopefully we have chosen wisely
#
export EDUJARDIR=/mnt/edesign/jars

export EDFRONTDOORURL=http://iceland.fishkill.ibm.com/cc

hname1=$(hostname)
hname2=$(echo $DISPLAY | cut -f2 -d:)
hname=$hname1":"$hname2
export DISPLAY=$hname
#echo Display has been set to $DISPLAY
cookie=$(xauth  list $DISPLAY | awk '{ print $3}')
#echo cookie is $cookie
export CLASSPATH=$EDUJARDIR/EDODCXChannel.jar:$EDJARDIR/EDODCXChannel.jar:$EDUJARDIR/https.jar:$EDJARDIR/https.jar
#echo $CLASSPATH
java oem.edge.ed.odc.xchannel.XChannel $DISPLAY "$cookie" $EDFRONTDOORURL
