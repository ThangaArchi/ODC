#! /bin/ksh
#
# STM code - Start the proxy
#
export EDINSTDIR=/afs/eda/build/edge/2.7
export EDBINDIR=$EDINSTDIR/bin/oem/edge/ed/odc
export EDPROPDIR=$EDINSTDIR/properties/oem/edge/ed/odc

export STMJAR=$EDINSTDIR/build/oem/edge/ed/odc/jars/STMProxy/STMProxy.jar
export CLASSPATH=$STMJAR:$EDPROPDIR:$CLASSPATH
export PATH=$PATH:$EDBINDIR

PORT=4444

export LIBPATH=$LIBPATH:$EDINSTDIR/content/lib

exec java oem.edge.ed.odc.stm.proxy $PORT
