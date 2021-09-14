#!/bin/ksh
#                        Copyright Header Check                              #
#    --------------------------------------------------------------------    #
#                                                                            #
#      OCO Source Materials                                                  #
#                                                                            #
#      Product(s): PROFIT                                                    #
#                                                                            #
#      (C)Copyright IBM Corp. 2003-2004                                      #
#                                                                            #
#      All Rights Reserved                                                   #
#      US Government Users Restricted Rigts                                  #
#                                                                            #
#      The source code for this program is not published or otherwise        #
#      divested of its trade secrets, irrespective of what has been          #
#      deposited with the US Copyright Office.                               #
#                                                                            #
#    --------------------------------------------------------------------    #
#      Please do not remove any of these commented lines  20 lines           #
#    --------------------------------------------------------------------    #
#                        Copyright Footer Check                              #

# ---------------------------------------------------------------------------
#      Variable settings
# ---------------------------------------------------------------------------

unset SHLIB_PATH
unset LIBPATH
unset LD_LIBRARY_PATH
unset CLASSPATH

#
# Default CONFIGSCRIPT to nothing. If that does not get replaced by master apply, then
#  no injection will be done
#
MASTER_ODC_CONFIGSCRIPT=

#
# All vars that used to be in EDODCPostinstall. Just copy/paste and keep this section 
#  the same in all .sh files. Its a pain, but quick for now ;-)
#
export ODCEARDIR="/web/installedApps/edesign4Network/odc.ear"
export ODCWARDIR=$ODCEARDIR/ODC.war
export EDJAVAPATH="/usr/java14/bin:/usr/WebSphere/AppServer/java/bin"
export EDINSTDIR=$ODCWARDIR
export EDBINDIR=$EDINSTDIR/bin
export EDLIBDIR=$EDINSTDIR/jars
export EDSOLIBDIR=$EDINSTDIR/jars
export EDJARDIR=$EDINSTDIR/jars
export EDPROPDIR=$ODCEARDIR/properties  
export EDCLASSTOP=$ODCWARDIR/WEB-INF/classes
export EDMODPROPDIR="/web/edesign/Mappingfiles"
export LOCALCIPHER="/web/edesign/ciphers63/odccipher.key"
export EDB2URL="jdbc:db2://edesign4.fishkill.ibm.com:50000/edodc"
export EDB2DRIVER="com.ibm.db2.jcc.DB2Driver"
export DB2INSTANCE="db2inst1"
export DB2INSTALL="/usr/opt/db2_08_01"
export EDB2PWDIR="/web/edesign/db2pw"
export EDB2INSTANCE=edesign
export EDB2JARS=/usr/opt/db2_08_01/java/db2jcc.jar:/usr/opt/db2_08_01/java/db2jcc_license_cu.jar
export EDB2LIBDIR=$DB2INSTALL/lib
export EDB2DBOXURL="jdbc:db2://edesign4.fishkill.ibm.com:50000/dropbox"
export EDB2DBOXDRIVER="$EDB2DRIVER"
export DBOXDB2INSTANCE=$DB2INSTANCE
export DB2DBOXINSTALL=$DB2INSTALL
export EDB2DBOXPWDIR="/web/edesign/db2pw"
export EDB2DBOXINSTANCE=edesign
export EDB2DBOXJARS="$EDB2JARS"
export EDB2DBOXLIBDIR="$EDB2LIBDIR"
export AMTDB2URL="jdbc:db2://edesign4.fishkill.ibm.com:50000/edodc"
export AMTDB2DRIVER="$EDB2DRIVER"
export AMTDB2INSTANCE=$DB2INSTANCE
export AMTDB2INSTALL=$DB2INSTALL
export AMTDB2PWDIR="/web/edesign/db2pw"
export AMTDB2SCHEMA=ignored
export AMTDB2JARS="$EDB2JARS"
export AMTDB2LIBDIR="$EDB2LIBDIR"
export DROPBOXSTDERRDIR="/web/dropboxlogs"
export DROPBOXLOGLIFETIME=5
export DROPBOXPROPDIR=$EDPROPDIR
export MAILGATEWAY="edamail.fishkill.ibm.com"
export ALERTADDRESSES="crichton@us.ibm.com"
export DROPBOXLOGDIRECTORY="/web/dropboxlog4j"
export FULLPATHTOKLOG=/usr/afsws/bin/klog
export GRIDBOXTOPDIR="/afs/eda/u/edesign2/grid"
export GRIDBOXSTDERRDIR="/afs/eda/u/edesign2/grid/gridboxlogs"
export GRIDBOXLOGLIFETIME=5
export GRIDBOXPROPDIR=$EDPROPDIR
export GRIDBOXLOGDIRECTORY="/afs/eda/u/edesign2/grid/log4jdir"
export DROPBOXEXTRAPARMS="-setSendPolicy 1 -setAllocationPolicy balanced -noemailsend"
export GRIDBOXEXTRAPARMS="-kloginfo eda.fishkill.ibm.com /web/edesign/gridboxpw -setSendPolicy 1 -log4jpropfile dropboxlog4j.properties"
export DSMPSERVEREXTRAPARMS=""
export EDFRONTDOORURL="http://edesign4.fishkill.ibm.com/technologyconnect/odc"

#
# Put JAVA search path at FRONT
#
export PATH="$EDJAVAPATH:$PATH"

#
# This will do something IIF its set in the master file. The intent is for an 
#  external script being sourced to finish/customize local setup
#
${MASTER_ODC_CONFIGSCRIPT}

unset PATCHJARS

# ---------------------------------------------------------------------------
#      Start of Logic
# ---------------------------------------------------------------------------

#
# Build up patch list in order (1-9) If named > 9, needs to honor sort order 
#  (like switch to A-Z, then a-z, or something)
#
PATCHJARS=""
if [ -d "$EDJARDIR/patches" ] ; then
   Patches=$(find "$EDJARDIR/patches" -name 'EDODCFTPServer*.jar' | sort -u -r)
   for patch in  $Patches ; do
      PATCHJARS="$PATCHJARS$patch:"
   done
fi
   
if [ ! -z "$PATCHJARS" ] ; then
   print -u2 -- "--Start Patches-------------------------------"
   print -u2    "$PATCHJARS"
   print -u2 -- "--End   Patches-------------------------------"
fi


if [ ! -r "$EDJARDIR/EDODCFTPServer.jar" ] ; then
   echo "EDODCFTPClient.jar not found. Cannot start FileTransfer server"
   exit 3
fi   

USERPW=""
[ -r userpw ] && USERPW=" -userpw userpw"
      
      
JAVAOPTS=""
CMD=java
   
#
# activation and mail jars are needed to use MailLogger for log4j alerts. Put 
#  orig CLASSPATH AFTER my code classes/properties, but BEFORE any mail/db2 type 
#  pathing CLASSPATH is unset at the start, so the only way it would be updated is
#  if the CONFIGSCRIPT changed it
#   
#
[[ ! -z "$CLASSPATH" ]] && CLASSPATH=":$CLASSPATH"

export CLASSPATH="$PATCHJARS$EDJARDIR/EDODCFTPServer.jar"
   
echo "Starting FileTransfer server."
$CMD $JAVAOPTS oem.edge.ed.odc.ftp.server.FTPServer -port 5050 $USERPW "$@"
