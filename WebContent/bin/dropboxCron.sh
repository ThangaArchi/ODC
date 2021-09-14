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
#
# Intent of this file is to be run as CRON job once a night
# 
#   1) Will enforce expiration and cleanup of unreferenced files
#   2) Will check FileAllocation areas (DB2 data) against actuals
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

# ============================= Start =============================

function alert {
   typeset dce_monitor_file="/var/local/etc/dce_monitor";
   typeset monitor="$1"
   typeset event="$2"
   typeset object="$3"
   shift 3
   typeset data="$@"
    
   print "vvvvvv Alert vvvvvv Alert vvvvv Alert vvvvv Alert vvvvv Alert vvvvv"
   
   if [ -r "$dce_monitor_file" ] ; then
      dce_monitor_file -m  "$monitor" -e "$event" -o "$object" -d "$data"
   else 
      print "Can't send real alert, $dce_monitor_file does NOT exist"
   fi
   
   out="\tmonitor: $monitor\n\tevent:   $event\n\tobject:  $object\n\tdata:    $data"
   print "!!Dropbox Alert!!\n$out"
   echo "!!Dropbox Alert!!\n\n$out" | \
            mail -s "!! Dropbox Alert !!" $ALERTADDRESSES
   
   print "^^^^^^ Alert ^^^^^^ Alert ^^^^^ Alert ^^^^^ Alert ^^^^^ Alert ^^^^^"
}


{

  KLOGBINDIR=$(dirname $FULLPATHTOKLOG)
  export PATH=$KLOGBINDIR:$PATH
  
  # Unlogging was actually not good, cause dropboxCron is running in same
  # tokenspace as dropbox instances
  #
  # unlog

  echo "Start -------------------- dropboxCron -------------------- Start"
  date
  print "\n==> Cleaning Expired packages and Unreferened Files\n"
  $EDBINDIR/startDropbox -cleanAll
  rc=$?
  if [ $rc != 0 ] ; then
     alert "DropboxCron" "Irregularity" "$(hostname)" "CleanAll failed with RC = $rc"
  fi

  date
  print "\n==> Obtaining Allocation Info, and running consistency checks\n"
  
  # Format of area info  
  #
  #  @FILEAREA!PRIORITY!FSTYPE!STATE!DIRECTORY!MAXSPACE!USEDSPACE
  #
  fadata="$($EDBINDIR/startDropbox -allocationInfo 2>&1 | grep '^@FILEAREA:!')"
  rc=$?
  if [ $rc != 0 ] ; then
     alert "DropboxCron" "Irregularity" "$(hostname)" \
           "Getting dropbox allocation info failed with RC = $rc"
  else
     for i in $fadata ; do
        export IFS='!'
        echo "$i" | \
        while read junk priority fstype state directory maxspace usedspace ; do
        
           export priority fstype state directory maxspace usedspace
           
           unset IFS
           print "==============="
           left=$(($maxspace-$usedspace))
           MEG=1048576
           print "Processing area[$directory]\n          prior[$priority] state[$state] fstype[$fstype]\n            tot[$maxspace] used[$usedspace] left[$left]\n           totM[$(($maxspace/$MEG))] usedM[$(($usedspace/$MEG))] leftM[$(($left/$MEG))]"
           
           if [ -r "$directory" ] ; then
        
             #
             # Calculate the actual space used by adding it up. Remove any
             #  files not considered in our format (number.number).
             #
              actualspaceused="$(find "$directory" -type f -print       | \
                                grep '[0-9][0-9]*\.[0-9][0-9]*$'        | \
                                xargs ls -l                             | \
                        awk 'BEGIN{a=0;} {a += $5} END{printf("%ld", a)}')"
              if [ "$actualspaceused" != "$usedspace" ] ; then
                 if [ $actualspaceused -gt $usedspace ] ; then
                    alert "dropboxCron" "Irregularity" "$(hostname)" \
                           "Space used GREATER THAN expected!!\n"    \
                           "\tActualSpaceUsed[$actualspaceused]\n"   \
                           "\tDB2SpaceUsed   [$usedspace]\n"         \
                           "\tDirectory      [$directory]"
                 else 
                   #
                   # Space is reserved before the file store occurs (makes
                   #  sense, right?), and so there CAN be more space in DB2
                   #  marked used than in the FS, If NO uploads going on, it
                   #  should NOT be the case. No good check for now.
                   #
                    print "Space used LESS THAN expected. Can be OK\n"    \
                           "\tActualSpaceUsed[$actualspaceused]\n"   \
                           "\tDB2SpaceUsed   [$usedspace]\n"         \
                           "\tDirectory      [$directory]"
                 fi
              fi
              
             # 
             # If in AFS, check how full we are 
             #
              if [ "x$fstype" = x2 ] ; then
                 fs lq "$directory" | tail +2 | {
                    read vol quota used pcntused partitionused 
                    spaceleftK=$(($quota-$used))
                    print "Actual AFS Usage for [$directory]"
                    print "\tVol  [$vol] quota[$quota""K] used[$used""K]\n\tpcent[$pcntused] partition[$partitionused] Left[$spaceleftK""K]"
                    
                    pcntused="${pcntused%"%"}"
                    
                    if [ "$pcntused" -gt 95 ] ; then
                       alert "dropboxCron" "AreaFull" "$(hostname)"     \
                              "FileAllocation Space very full\n"        \
                              "\tActualSpaceUsed[$actualspaceused]\n"   \
                              "\tPercent Used   [$pcntused]\n"          \
                              "\tDirectory      [$directory]"
                    fi
                    
                    spaceleft=$(($spaceleftK*1024))
                    DB2SpaceLeft=$(($maxspace-$usedspace))
                    
                    if [ "$DB2SpaceLeft" -gt "$spaceleft" ] ; then
                       alert "dropboxCron" "AreaFull" "$(hostname)"           \
                              "DB2 Space left > actual space left in Quota\n" \
                              "\tDB2SpaceLeft [$DB2SpaceLeft]\n"              \
                              "\tFileSys Left [$spaceleft]\n"                 \
                              "\tDirectory      [$directory]"
                    fi
                 }
              fi
              
           else 
              alert "DropboxCron" "Irregularity" "$(hostname)"    \
                    "FileAllocation directory $directory does NOT exist!"
           fi
           unset IFS
        done
     done
  fi
  
  date
  print "\n==> Combining Log4j Logs\n"
  
  if [ -r "$DROPBOXLOGDIRECTORY" -a -d "$DROPBOXLOGDIRECTORY" ] ; then
     LOGFILEUNIQ="$(date +'%Y.%m.%d.%H.%M.%S')-$$"  
     export LOGARCHIVE="$DROPBOXLOGDIRECTORY/archive/log.archive.$LOGFILEUNIQ"

     mkdir -p $(dirname $LOGARCHIVE) 2> /dev/null
     LOGFILES="$(find "$DROPBOXLOGDIRECTORY" -type f -a \
                                             ! -name 'log.archive*' -print | \
          sort|xargs fgrep -l "==== *EXITING* -- LOG DONE -- *EXITING* ====")"
     numdone=0
     if [ "xLOGFILES" != x ] ; then
        err=false
        for logfile in $LOGFILES ; do
           print "Reclaiming log $logfile"
           {
             echo "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
             echo "~ Log - $logfile"
             echo "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
             cat $logfile
           } | tee -a "$LOGARCHIVE" 2>&1 > /dev/null
           if [ $? != 0 ] ; then
              err=true
              print "Error appending log to archive file. Stop"
              break
           fi
           numdone=$(($numdone+1))
        done
        
        if [ $err = false ] ; then
           compress "$LOGARCHIVE"
           for logfile in $LOGFILES ; do
              print "Removing log $logfile"
              rm -f $logfile
              if [ $? != 0 ] ; then
                 alert "DropboxCron" "Irregularity" "$(hostname)" \
                       "Unable to remove logfile while archiving\n\t$logfile"
              fi
           done
        fi
        
        if [ $err = true ] ; then
           rm -f "$LOGARCHIVE"
           numdone=0
        fi
     fi
     print "Number logfiles archived = $numdone [$LOGARCHIVE"".Z]"
     
  else 
     alert "DropboxCron" "Irregularity" "$(hostname)" \
           "Dropbox LOG4J directory does not exist or not readible by me\nENV =>\n\n$(env)"
  fi
  
  
  date
  echo "End   -------------------- dropboxCron --------------------   End"
  
  # Unlogging was actually not good, cause dropboxCron is running in same
  # tokenspace as dropbox instances
  #unlog

} 2>& 1 | tee -a $DROPBOXSTDERRDIR/dropboxCron.stderr

