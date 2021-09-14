#!/bin/sh

#
# Allow skipping of unset of LIBPATH, which seems to cause problems on some
#  platforms. Removing the unset BREAKS other platforms. Sigh.
#
if [ -r startds.env ]
then
   . startds.env
else
   unset LD_LIBRARY_PATH
   unset LIBPATH
   unset SHLIB_PATH
fi

unset JAVA_HOME
unset J_HOME
unset HPPARMS
unset ICAPATH
unset POSTPROCESS
unset CLASSPATH
unset USECLASSPATH
unset PLAT

#
# Legacy
#
setenv() { eval $1="$2"; export $1; }

#
# Use envvars to callout actual binary. Since we have to assume
#  Bourne Shell, can't simply unalias (that is the issue here ...
#  someone has rm aliased to have confirmations ... sigh)
#
setenv RM_CMD   rm
setenv GREP_CMD grep
setenv SED_CMD  sed

if   [ -x /bin/rm ] ; then
  setenv RM_CMD   /bin/rm
elif [ -x /usr/bin/rm ] ; then
  setenv RM_CMD   /usr/bin/rm
fi

if   [ -x /bin/grep ] ; then
  setenv GREP_CMD   /bin/grep
elif [ -x /usr/bin/grep ] ; then
  setenv GREP_CMD   /usr/bin/grep
fi

if   [ -x /bin/sed ] ; then
  setenv SED_CMD   /bin/sed
elif [ -x /usr/bin/sed ] ; then
  setenv SED_CMD   /usr/bin/sed
fi

# Determine the program installation point.

# Absolute path is available? Remove the trailing name.
PGM=$0

# if [ "x${PGM##/*}" = "x" ]
echo $PGM | $GREP_CMD "^/" >/dev/null
if [ $? = 0 ]
then
  # InstallPoint=${PGM%/*}
  InstallPoint=`echo $PGM | $SED_CMD 's!/[^/]*$!!'`

# Relative path has no directory? Use current directory.
else
  # if [ "${PGM%/*}" = "$PGM" ]
  echo $PGM | $GREP_CMD "/" >/dev/null
  if [ $? != 0 ]
  then
    InstallPoint=`pwd`

  # Relative path includes a directory.
  else
    CWD=`pwd`
    # cd "${PGM%/*}"
    cd `echo $PGM | $SED_CMD 's!/[^/]*$!!'`
    InstallPoint=`pwd`
    cd "$CWD"
  fi
fi

# Determine the platform. Flatten it (only alphanumeric)
OS=`uname | tr 'abcdefghijklmnopqrstuvwxyz' 'ABCDEFGHIJKLMNOPQRSTUVWXYZ' | $SED_CMD 's/[^A-Z0-9]//g'`

# SunOS: sparc handled as SUNSP, all others SUNOS
if [ "$OS" = "SUNOS" ]
then
  ARCH=`uname -p`
  if [ "$ARCH" = "sparc" ]
  then
    PLAT="SUNSP"
  else
    PLAT=$OS
  fi

# Linux: x86 handled as LIN86, all others LINUX
elif [ "$OS" = "LINUX" ]
then
  ARCH=`uname -m`
  # if [ "${ARCH#*86}" != "$ARCH" ]
  echo $ARCH | $GREP_CMD 86 >/dev/null
  if [ $? = 0 ]
  then
    PLAT="LIN"
  else
    PLAT=$OS
  fi

# Mac OS X
elif [ "$OS" = "DARWIN" ]
then
  PLAT="MACOSX"

# No special handling for AIX. Other platforms are
# considered unsupported. Most unix variants should
# work without change. We'll expect to find
# JREPATH and JREPARMS in the edesign ini file as
# <PLAT>JREPATH and <PLAT>JREPARMS. JREPARMS is
# necessary only if directed by the installation applet.
else
  PLAT=$OS
fi

# Read in the edesign.ini file located in the installation point.
$SED_CMD "s/^\([^=]*=\)\(.*\)$/\1'\2'/" $InstallPoint/edesign.ini > /tmp/edesign.ini.$$
. /tmp/edesign.ini.$$
$RM_CMD /tmp/edesign.ini.$$

# Ok, now evaluate JREPATH and JREPARMS to get the platform
# specific path and parms.
JREPATH=\$"$PLAT"JREPATH
JREPARMS=\$"$PLAT"JREPARMS
USECLASSPATH=\$"$PLAT"USECLASSPATH
eval "THISJREPATH=\`echo $JREPATH | $SED_CMD 's!%2[fF]!/!g'\`"
eval "THISJREPARMS=$JREPARMS"
eval "THISUSECLASSPATH=$USECLASSPATH"

# Bad edesign.ini file?
if [ "x$THISJREPATH" = "x" ]
then
  echo $InstallPoint/edesign.ini file is missing $JREPATH variable.
  exit 1
fi

# If the JREPARMS were not in the edesign.ini file, default them.
if [ "x$THISJREPARMS" = "x" ]
then
  THISJREPARMS="-mx32M"
fi

# Started as a browser helper app or to run dropboxftp, dropbox gui, or web conferences?
if [ $# -gt 0 ]
then
  # Starting dropboxftp?
  if [ "x$1" = "x-dropboxftp" ]
  then
    shift 1
    THISJREPARMS="$THISJREPARMS -DTERM=\"$TERM\""
    PGM="DROPCMDLINE"
    EXTRAPARMS="-startdir \"`pwd`\" $*"

  # Starting dropbox gui?
  elif [ "x$1" = "x-dropbox" ]
  then
    shift 1

    # Optional -url URL parameter present?
    if [ $# -gt 0 ]
    then
      if [ "x$1" = "x-url" ]
      then
        DBOXURL="$2"
      else
        echo "Unknown option: $1"
        echo "Valid options: -dropbox [-url URL]"
        exit 1
      fi

    # DBOXURL variable missing from edesign.ini?
    elif [ "x$DBOXURL" = "x" ]
    then
      echo $InstallPoint/edesign.ini file is missing DBOXURL variable.
      exit 1
    fi

    # Starting tunnel with a command file
    PGM="TUNNEL"
    EXTRAPARMS=/tmp/startds.opts.$$
    POSTPROCESS="$RM_CMD -f $EXTRAPARMS"
    echo $PGM >$EXTRAPARMS
    echo "-URL $DBOXURL" | $SED_CMD 's!%2[fF]!/!g' >>$EXTRAPARMS
    echo "-CH_TUNNELCOMMAND XFR" >>$EXTRAPARMS
    echo "-THE_END" >>$EXTRAPARMS

  # Starting web conferences?
  elif [ "x$1" = "x-conf" ]
  then
    shift 1

    # Optional -url URL parameter present?
    if [ $# -gt 0 ]
    then
      if [ "x$1" = "x-url" ]
      then
        CONFURL="$2"
      else
        echo "Unknown option: $1"
        echo "Valid options: -conf [-url URL]"
        exit 1
      fi

    # CONFURL variable missing from edesign.ini?
    elif [ "x$CONFURL" = "x" ]
    then
      echo $InstallPoint/edesign.ini file is missing CONFURL variable.
      exit 1
    fi

    # Starting tunnel with a command file
    PGM="TUNNEL"
    EXTRAPARMS=/tmp/startds.opts.$$
    POSTPROCESS="$RM_CMD -f $EXTRAPARMS"
    echo $PGM >$EXTRAPARMS
    echo "-URL $CONFURL" | $SED_CMD 's!%2[fF]!/!g' >>$EXTRAPARMS
    echo "-CH_TUNNELCOMMAND NEWODC" >>$EXTRAPARMS
    echo "-THE_END" >>$EXTRAPARMS

  # A helper app to the browser.
  else
    read PGM < "$1"
    EXTRAPARMS="$1"
  fi

# No parms indicates we are started by the signed applet.
else
  read PGM
  unset EXTRAPARMS
fi

# For ICA we do special things to get the right program and parms
echo $PGM | $GREP_CMD ICA >/dev/null
if [ $? = 0 ]
then
  if [ "x$ICAPATH" != "x" ]
  then
    ICAPATH=`echo $ICAPATH | $SED_CMD 's!%2[fF]!/!g'`
    ICAROOT=`echo $ICAPATH | $SED_CMD 's!/[^/]*$!!'`
    THISJREPATH=$ICAPATH
    THISJREPARMS=
    HPPARMS=
    THISCLASS=
    THISCLASSPATH=
  else
    CLASS=\$"$PGM"CLASS
    eval "THISCLASS=$CLASS"
    if [ "x$THISCLASS" = "x" ]
    then
      THISCLASS=com.citrix.JICA
    fi

    CLASSP=\$"$PGM"CLASSPATH
    eval "THISCLASSPATH=\`echo $CLASSP | $SED_CMD 's!;!:!g'\`"
    if [ "x$THISCLASSPATH" = "x" ]
    then
      THISCLASSPATH=JICAJ.jar
    fi
  fi

  # ICA can't read parms from stdin, so we get an
  # additional line which contains ClassParms=...
  # We eval this line to create the ClassParms
  # environment variable. ICA is always started by
  # the tunnel application (we are never a helper app
  # per above).
  read LINE
  eval $LINE

  w=1200; h=980; ip=""; icap=""; addr=""; title=""; rtitle=""
  ipclause=0;

  for i in $ClassParms
  do
    opt=`echo $i | cut -f1 -d: | tr 'ABCDEFGHIJKLMNOPQRSTUVWXYZ' 'abcdefghijklmnopqrstuvwxyz'`
    val=`echo $i | cut -f2- -d:`

    case $opt in
      -width)          w=$val
                       ipclause=0;;
      -height)         h=$val
                       ipclause=0;;
      -initialprogram) ip=$val
                       ipclause=1;;
      -address)        addr=$val
                       ipclause=0;;
      -icaportnumber)  icap=$val
                       ipclause=0;;
      -title)          title="$val"; rtitle=`echo $val | tr '\%\[\]' 'xxx'`
                       ipclause=0;;
      *)               if [ $ipclause = 1 ]
                       then
                         ip="$ip $i"
                       else
                         echo "Unknown parm $i"
                       fi;;
    esac
  done

  EXTRAPARMS=/tmp/start.$$.ica
  POSTPROCESS="$RM_CMD -f $EXTRAPARMS"
  $SED_CMD "s%REPLACEWIDTH%$w%g
       s%REPLACEHEIGHT%$h%g
       s%REPLACEINITIALPGM%$ip%g
       s%REPLACEADDR%$addr%g
       s%REPLACEICAPORT%$icap%g
       s%REPLACETITLE%$rtitle%g" < $InstallPoint/template.ica > $EXTRAPARMS

  if [ "x$ICAPATH" = "x" ]
  then
    EXTRAPARMS="$EXTRAPARMS -title:$title"
  fi

# For all other programs, we expect to find statements in the
# edesign.ini file as <PGM>CLASS and <PGM>CLASSPATH which define
# the class and classpath to use.
else
  CLASS=\$"$PGM"CLASS
  CLASSP=\$"$PGM"CLASSPATH
  eval "THISCLASS=$CLASS"
  eval "THISCLASSPATH=\`echo $CLASSP | $SED_CMD 's!;!:!g'\`"

  # Bad edesign.ini file?
  if [ "x$THISCLASS" = "x" ]
  then
    echo $InstallPoint/edesign.ini file is missing $CLASS variable.

    if [ "x$POSTPROCESS" != "x" ]
    then
      eval "$POSTPROCESS"
    fi

    exit 1

  elif [ "x$THISCLASSPATH" = "x" ]
  then
    echo $InstallPoint/edesign.ini file is missing $CLASSPATH variable.

    if [ "x$POSTPROCESS" != "x" ]
    then
      eval "$POSTPROCESS"
    fi

    exit 1
  fi
fi

# Determine which kind of JRE we are starting to set the CLASSPATH properly

# if THISJREPATH is relative, then it is our JRE 
echo $THISJREPATH | $GREP_CMD "^/" >/dev/null
if [ $? != 0 ]
then
  CLASSPARMS="-cp $THISCLASSPATH"

else
  # Get the JRE program name
  JRE=`echo $THISJREPATH | $SED_CMD 's!^/.*/!!'`

  # if JRE name is jre, use -cp
  echo $JRE | $GREP_CMD "jre" >/dev/null
  if [ $? = 0 ]
  then
    CLASSPARMS="-cp $THISCLASSPATH"

  else
    # if JRE name is java, use CLASSPATH environment variable
    echo $JRE | $GREP_CMD "java" >/dev/null
    if [ $? = 0 ]
    then
      CLASSPARMS=
      setenv CLASSPATH $THISCLASSPATH
    else
      # Not a familiar JRE name, use edesign.ini variables.
      # if <PLAT>USECLASSPATH is set, use CLASSPATH
      # otherwise <PLAT>JREPARMS should end with the classpath parm
      # and the CLASSES will follow.
      if [ "$THISUSECLASSPATH" = "YES" ]
      then
        CLASSPARMS=
        setenv CLASSPATH $THISCLASSPATH
      else
        CLASSPARMS=$THISCLASSPATH
      fi
    fi
  fi
fi

if [ "$THISJREPATH" = "$ICAPATH" ]
then
  CLASSPARMS=
fi

# Ok, let's start the client...
setenv LD_LIBRARY_PATH ".:$LD_LIBRATY_PATH"
setenv SHLIB_PATH ".:$SHLIB_PATH"
setenv LIBPATH ".:$LIBPATH"
cd "$InstallPoint"

#
# Set ulimit to at least 100 (our Sun boxes come 64 stock) May fail on HP
#
numfiles=`ulimit -n 2> /dev/null`
if [ $? = 0 -a "x$numfiles" != x ] 
then
   echo "$numfiles" | head -1 | $GREP_CMD '^[0-9][0-9]*$' > /dev/null 2>&1
   isnum=$?
   if [ $isnum = 0 ] 
   then
      if [ 100 -gt "$numfiles" ] 
      then
         ulimit -n 100 2> /dev/null
      fi
   fi
fi

$THISJREPATH $HPPARMS $THISJREPARMS $CLASSPARMS $THISCLASS $EXTRAPARMS
RC=$?

if [ "x$POSTPROCESS" != "x" ]
then
  eval "$POSTPROCESS"
fi

exit $RC
