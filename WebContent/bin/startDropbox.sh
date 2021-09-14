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

#
# Careful NOT to print anything to stdout! That is direct connected to client
#  if daemonStartup. If started via inetd, then so is stderr. Clamp that off
#

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
# Where we shall stuff STDOUT messages
#
if [ ! -d $DROPBOXSTDERRDIR ] ; then
   mkdir -p $DROPBOXSTDERRDIR
fi

#
# Redirect stderr to a dynamically created file if running in DaemonMode
#
DAEMONSTARTUP=
LOGFILEUNIQ="$(date +'%Y.%m.%d.%H.%M.%S')-$$"
LOGFILENAME="dboxstderr.$LOGFILEUNIQ"
LOGFILE="$DROPBOXSTDERRDIR/$LOGFILENAME"
A=$(echo "$@" | fgrep -w -- -daemonStartup | wc -l | awk '{print $1}')
if [ "x$A" != x0 ] ; then
   exec 2> "$LOGFILE"
   
   #
   # Clean up logs not changed in the past DROPBOXLOGLIFETIME days
   #
   find $DROPBOXSTDERRDIR -name 'dboxstderr.*' -a -type f -a \
                          -mtime +$DROPBOXLOGLIFETIME | xargs rm -f 
                          
   DAEMONSTARTUP="-daemonStartup"
fi


#
# We need KLOG in path
#
whence klog >/dev/null 2>&1
if [ $? != 0 ] ; then
   if [ -r "$FULLPATHTOKLOG" ] ; then
      export PATH=$PATH:$(dirname "$FULLPATHTOKLOG")
   elif [ -r /usr/afsws/bin/klog ] ; then
      export PATH=$PATH:/usr/afsws/bin
   fi
fi

#
# If we can't find the jar, just complain and bag out
#
if [ ! -r "$EDJARDIR/EDODCDropboxServer.jar" ] ; then
   print -u2 "EDODCDropboxServer.jar not found. Cannot start DropboxServer"
   exit 3
fi   

if [ "x$1" = x-? ] ; then
   print -u2 "Usage: $(basename $0) [-userpw file] [-port port] [...]"
   exit 4
fi 
   
#   
# This is only used for non-daemon mode (non-inetd that is)
#
port="5060"
   
print -u2 "Starting Dropbox Tokenbased Server. Port $port"
   
#
# Build up patch list in order (1-9) If named > 9, needs to honor sort order 
#  (like switch to A-Z, then a-z, or something)
#
PATCHJARS=""
if [ -d "$EDJARDIR/patches" ] ; then
   Patches=$(find "$EDJARDIR/patches" -name 'EDODCDropboxServer*.jar' | sort -u -r)
   for patch in  $Patches ; do
      PATCHJARS="$PATCHJARS$patch:"
   done
fi
      
if [ ! -z "$PATCHJARS" ] ; then
   print -u2 -- "--Start Patches-------------------------------"
   print -u2    "$PATCHJARS"
   print -u2 -- "--End   Patches-------------------------------"
fi
   
CMD=java
JAVAOPTS=""
   
#
# activation and mail jars are needed to use MailLogger for log4j alerts. Put 
#  orig CLASSPATH AFTER my code classes/properties, but BEFORE any mail/db2 type 
#  pathing CLASSPATH is unset at the start, so the only way it would be updated is
#  if the CONFIGSCRIPT changed it
# 
#
[ ! -z "$CLASSPATH" ] && CLASSPATH=":$CLASSPATH"

export CLASSPATH="$PATCHJARS$EDJARDIR/EDODCDropboxServer.jar:$EDMODPROPDIR:$EDPROPDIR$CLASSPATH:$EDB2DBOXJARS:$EDJARDIR/https.jar:$EDJARDIR/JakartaRegex.jar:$EDJARDIR/ant.jar:$EDJARDIR/log4j.jar:$EDJARDIR/activation.jar:$EDJARDIR/mail.jar:$DROPBOXPROPDIR"
   
export LD_LIBRARY_PATH="$LD_LIBRARY_PATH:$EDB2DBOXLIBDIR:$EDLIBDIR:$EDSOLIBDIR"
export LIBPATH="$LIBPATH:$EDB2DBOXLIBDIR:$EDLIBDIR:$EDSOLIBDIR"
export SHLIB_PATH="$SHLIB_PATH:$EDB2DBOXLIBDIR:$EDLIBDIR:$EDSOLIBDIR"
      
#
# Put $DAEMONSTARTUP up front as we know it does some things, like redirect
#  stdout/err so we don't pollute the client stream. It is specified again
#  in the $@, but it will be ignored. Want $@ last so it will override all
#  other options
#
JAVAOPTS="$JAVAOPTS -DDROPBOXLOG4JLOG=$DROPBOXLOGDIRECTORY/log.$LOGFILEUNIQ \
                    -DMAILGATEWAY=$MAILGATEWAY -DALERTADDRESSES=$ALERTADDRESSES"
exec $CMD -mx60M $JAVAOPTS oem.edge.ed.odc.dropbox.server.DropboxServer   \
                $DAEMONSTARTUP                                            \
                -port $port -tokencipher $LOCALCIPHER                     \
                -db2 "$EDB2DBOXDRIVER"   "$EDB2DBOXURL"                   \
                     "$EDB2DBOXINSTANCE" "$EDB2DBOXPWDIR"                 \
                -amtdb "$AMTDB2DRIVER"     "$AMTDB2URL"                   \
                       "$AMTDB2SCHEMA"     "$AMTDB2PWDIR"                 \
                -smtpserver "$MAILGATEWAY"                                \
                 $DROPBOXEXTRAPARMS                                       \
                "$@" 
