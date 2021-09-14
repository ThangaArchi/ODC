#!/bin/sh
#
# dropboxftp startup script
#
INSTPOINT=.
curdir=`pwd`
cd $INSTPOINT
./startds.sh -jreparm -DTERM="$TERM" -cmdline DROPCMDLINE -startdir "$curdir" $1 $2 $3 $4 $5 $6 $7 $8 $9
