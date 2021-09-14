# ERROR CODES
# 101 - Could not decode the token

# 102 - Could not find an available afs id to map the user to
# 103 - ISPROD value is neither I nor P
# 104 - Invalid Grid Dir
# 105 - Could not read adminID
# 106 - Could not login as adminID
# 107 - Could Not change to the grid dir
# 108 - Could Not change to the linked path
# 109 - Could Not set acls for the user
# 110 - Could Not login with the user id
# 111 - Could Not find the projects dir in the mapped userids profile
# 112 - Could Not create the sym links in the users profile
# 113 - Could not copy the XAUTH File
# 114 - Could not LaunchPubApp
# 115 - Could not remove XAUTH
# 116 - Could not change to install dir
# 117 - Could not change to grid dir
# 118 - Could not unset the acls for the userid
# 119 - Could not return the id to the idmanager 
# 120 - Missing required Argument(s)
# 121 - Could Not Open File
# 122 - Could Not Update Group
# 123 - Could Not Change Owner
xterm -e
set -x
tokenvalue=$1
echo $DISPLAY
echo token value is  $tokenvalue  
export PATH=/usr/java14/bin:$PATH
#export CLASSPATH=./EDODCRemoteViewer.jar
error="Error"
echo `pwd` | read installDir
echo installdir is $installDir
export CLASSPATH=$installDir"/EDODCRemoteViewer.jar"
export INSTALLDIR=$installDir
dirname $installDir | read installHome
echo installHome is $installHome


echo  Original Tokens `/usr/afsws/bin/tokens`

echo Tokens after unlogging `/usr/afsws/bin/tokens`

# Decoding the token
echo `java oem.edge.ed.odc.remoteviewer.ValidatePubApp token $tokenvalue decodekey PubAppTest_Decode.key` | read decodedToken 
#decodedToken="XTERM@crichton"
#decodedToken="XCLOCK@crichton"
#decodedToken="AixTerm4@jeetrao@us.ibm.com"
#decodedToken="XCLOCK@jvrao"
#decodedToken="AIXTERM@test1@remote.com"
echo decodedtoken is $decodedToken

# Splitting into userid and pubApp

echo $decodedToken |cut -f1 -d @   | read pubApp
#echo $decodedToken |cut -f2 -d @   | read userid
echo $decodedToken |cut -f 2,3 -d @   | read userid
echo userid $userid appname $pubApp

# Checking for error in decoding the token
if [ $pubApp = $error ]
then
#echo test error is $userid >&2
#exit 101
java oem.edge.ed.odc.remoteviewer.ErrMsgDlg "Could not decode the token $decodedToken"
exit 0
fi

echo `grep "^ISPROD=" $installHome/$pubApp/PubApp.properties `| cut -f2 -d = | read deployedEnv 
echo `grep "^ISAFS=" $installHome/$pubApp/PubApp.properties `| cut -f2 -d = | read fileSystemtype 
if [ $deployedEnv = 'I' ]
then
	echo `grep "^IgridDir=" $installHome/$pubApp/PubApp.properties `| cut -f2 -d = | read gridDir 

elif [ $deployedEnv = 'P' ]
then
	echo `grep "^PgridDir=" $installHome/$pubApp/PubApp.properties `| cut -f2 -d = | read gridDir 
else
#	echo Delpoyed environment Unrecognized
#	exit 103
       java oem.edge.ed.odc.remoteviewer.ErrMsgDlg "Delpoyed environment Unrecognized"
        exit 0
fi


echo gridDir is $gridDir

if [[ -z $gridDir ]]
then
	#echo Invalid Grid Dir
 	#exit 104
        java oem.edge.ed.odc.remoteviewer.ErrMsgDlg "Invalid Grid Dir"
        exit 0
fi



# Mapping the userid to the afs userid to be used
#echo `grep $userid $mappingfile `| cut -f2 -d : | read login 
echo `pwd` 
$installDir/idManager obtainid $pubApp $installHome |  read mappedID
#$installDir/idManager obtainid $gridDir | read mappedID
echo mapped id is $mappedID
echo $mappedID | cut -f1 -d : | read login

if [ $login = $error	]
then
echo $mappedID | cut -f2 -d : | read errnum
echo $mappedID | cut -f3 -d : | read errormsg
echo $userid does not have the required permissions 
#echo $errormsg
#exit $errnum
        #java -cp /home/pubApp/COMMON/oem/edge/ed/odc/remoteviewer oem.edge.ed.odc.remoteviewer.ErrMsgDlg "$errormsg"
        java oem.edge.ed.odc.remoteviewer.ErrMsgDlg "All the sessions for this viewer are currently in use, Please try again later"
        exit 0

fi

if [ $fileSystemtype = Y ]
then 

#Opening the adminID file to get the admin userid and password
#adminIDfile="$installHome/$pubApp/adminID"
adminIDfile="$gridDir/common/adminID"
adminID="adminID"
echo `grep $adminID $adminIDfile `| cut -f2 -d : | read adminlogin

if [[ -z $adminID ]]
then
        #echo Could Not read admin id  
        #exit 105
        java oem.edge.ed.odc.remoteviewer.ErrMsgDlg "Could not read adminID"
        exit 0

fi

# KLOGing with the adminID and the decoded password from the adminID file
grep "^$adminID:" $adminIDfile  | cut -f3 -d :  | java oem.edge.ed.odc.remoteviewer.Base64DecoderForPubApp | /usr/afsws/bin/klog $adminlogin -cell eda -pipe

if [ "$?" -ne "0" ]
then
        #echo Could Not login with the admin id
        #exit 106
        java oem.edge.ed.odc.remoteviewer.ErrMsgDlg "Could Not login with the admin id"
        exit 0
fi

echo `pwd`| read pwd
echo this is the present working order $pwd

echo Tokens after Klogging as admin `/usr/afsws/bin/tokens`

fi # DO THIS ONLY IF AFS

# Getting a list of all the projects the ccid should have access to
 
cd $gridDir
cd ccid
cd $userid
if [ "$?" -ne "0" ]
then
        #echo Could Not change to the grid dir
        #exit 107
        java oem.edge.ed.odc.remoteviewer.ErrMsgDlg "Could Not change to the grid dir"
        exit 0
fi


if [ $fileSystemtype = Y ]
then

echo `ls -l | tr -s "\t" " " | cut -f11 -d" "` | read  projectlinks
echo $projectlinks
temp=$projectlinks 
print ${#temp}
i=0
 while [ temp ]
 do
	path=${temp%% *}
        echo path is $path
        cd $path
       	if [ "$?" -ne "0" ]
	then
        	#echo Could Not change to the linked path
        	#exit 108
        java oem.edge.ed.odc.remoteviewer.ErrMsgDlg "Could Not change to the linked path"
        exit 0

	fi
	echo `pwd` | read realpath
        echo realpath is $realpath
        assignedProject[i=i+1]=${realpath#$gridDir/}
        
	
	temp=${temp#${path} }
 	if [ $path = $temp ]
	then
		break
	exit
	fi

done
echo i is $i 

# Setting the acls for the project dirs in the grid home

cd $gridDir
/usr/afsws/bin/fs setacl . $login read 
j=0
while [ i -gt j ]
do
	projdir=${assignedProject[j=j+1]%%/*}
	echo projdir is $projdir
	$pwd/_set_acl $projdir $login rl
	if [ "$?" -ne "0" ]
	then
        	#echo Could Not set acls for the id in the grid dir
        	#exit 109
        java oem.edge.ed.odc.remoteviewer.ErrMsgDlg "Could Not set acls for the id in the grid dir"
        exit 0

	fi
done
print ${assignedProject[@]}



echo `pwd`
cd $pwd
passwdfile=/etc/passwd 


# KLOGing with the userid and the decoded password from the mapping file 

#grep "^$userid:" $mappingfile  | cut -f3 -d :  | java oem.edge.ed.odc.remoteviewer.Base64DecoderForPubApp | /usr/afsws/bin/klog -setpag $login -cell eda -pipe


echo $mappedID | cut -f2 -d : | java oem.edge.ed.odc.remoteviewer.Base64DecoderForPubApp | /usr/afsws/bin/klog -setpag $login -cell eda -pipe
if [ "$?" -ne "0" ]
then
        #echo Could Not login with the user id
        #exit 110
        java oem.edge.ed.odc.remoteviewer.ErrMsgDlg "Could Not login with the user id"
        exit 0
fi



else # ELSE  IF IT IS UNIX FILE SYSTEM



echo `ls  | tr -s "\t" " " | cut -f11 -d" "` | read  projectlinks

echo $projectlinks
echo `ls -l | tr -s "\t" " " | cut -f11 -d" "` | read  projectlinks1
echo $projectlinks1
temp=$projectlinks1
# CHECK IF USER HAS NO PROJECTS
if [[ -z $temp ]]
	then
        cd $installDir
          $installDir/idManager returnid $pubApp $installHome $login
         java oem.edge.ed.odc.remoteviewer.ErrMsgDlg "Error: No projects could be added to the users session, Please make sure that the user is entitled to atleast one project"
	exit 0
    fi
print ${#temp}
i=0
 while [ temp ]
 do
        path=${temp%% *}
        echo path is $path
        realpath=${path#../../}
	echo $realpath
        assignedProject[i=i+1]=$realpath


        temp=${temp#${path} }
        if [ $path = $temp ]
        then
                break
        exit
        fi

done
echo i is $i

# ADDING THE LOGINID TO THE RESPECTIVE GROUPS
temp=$projectlinks
#groupmapfile=$installHome/$pubApp/groupmap
groupmapfile=$gridDir/common/groupmap
 while [ temp ]
 do
        path=${temp%% *}
	grep $path $groupmapfile  | cut -f1 -d :  | read projname

	echo changed projname is $projname
        if [ -z "$projname" ] 
        then
	basename $projlinkdir | read projname
        fi

        $installDir/UpdateGroup + $login $projname | read retmsg
echo returned message from UpdateGroup is $retmsg
echo $retmsg | cut -f1 -d : | read message

if [ $message = $error	]
then
echo $retmsg | cut -f2 -d : | read errnum
echo $retmsg | cut -f3 -d : | read errormsg
#echo $errormsg
#exit $errnum
        cd $installDir
	$installDir/idManager returnid $pubApp $installHome $login		
	java oem.edge.ed.odc.remoteviewer.ErrMsgDlg "Error: No projects could be added to the users session, Please make sure that the user is entitled to atleast one project"
        exit 0
fi

	temp=${temp#${path} }
	if [ $path = $temp ]
	then
       	 break
	exit
	fi	
done


fi

passwdfile="/etc/passwd"
grep "^$login:" $passwdfile  | cut -f6 -d :  | read logindir
echo logindir is $logindir
cd $logindir
cd projects

if [ "$?" -ne "0" ]
then
        #echo Could Not find the projects dir in the mapped userids profile 
        #exit 111
        java oem.edge.ed.odc.remoteviewer.ErrMsgDlg "Could Not find the projects dir in the mapped userids profile"
        exit 0

fi

rm -f *

echo groupmapfile is $groupmapfile
k=0
while [ i -gt k ]
do
       echo griddir is $gridDir
        projlinkdir=$gridDir"/"${assignedProject[k=k+1]}
        echo projlinkdir is $projlinkdir
 	#projname=${projlinkdir##*/}
	basename $projlinkdir | read projname
	echo orginal projname is $projname
	#grep $projname $groupmapfile  | cut -f2 -d :  | read projname

	echo Creating Links in `pwd`
	ln -s $projlinkdir $projname
	if [ "$?" -ne "0" ]
	then
       		#echo Could not create the sym links in the users dir
        	#exit 112
        java oem.edge.ed.odc.remoteviewer.ErrMsgDlg "Could not create the sym links in the users dir"
        exit 0
	fi
        $installDir/lchangeowner $login $projname | read retmsg
echo returned message from lchangewoner is $retmsg
echo $retmsg | cut -f1 -d : | read message

if [ $message = $error	]
then
echo $retmsg | cut -f2 -d : | read errnum
echo $retmsg | cut -f3 -d : | read errormsg
#echo $errormsg
#exit $errnum
        java oem.edge.ed.odc.remoteviewer.ErrMsgDlg "$errormsg"
        exit 0
fi
  
done


# Checking the tokens obtained
echo `/usr/afsws/bin/tokens`

# Copying the current userid's .Xauthority file to the Klogged user id's home

dest="$logindir/public/.Xauthority$$"
echo `whoami` | read currentlogin

grep "^$currentlogin:" $passwdfile  | cut -f6 -d :  | read homedir
echo logindir is $logindirecho $dest
cp "$homedir/.Xauthority" $dest 
if [ "$?" -ne "0" ]
then
	#echo Could not copy the XAUTH File 
        #exit 113
        java oem.edge.ed.odc.remoteviewer.ErrMsgDlg "Could not copy the XAUTH File"
        exit 0

fi
chmod 777 $dest
$installDir/changeowner $login $dest | read retmsg
echo returned message from changewoner is $retmsg
echo $retmsg | cut -f1 -d : | read message

if [ $message = $error	]
then
echo $retmsg | cut -f2 -d : | read errnum
echo $retmsg | cut -f3 -d : | read errormsg
#echo $errormsg
#exit $errnum
        java oem.edge.ed.odc.remoteviewer.ErrMsgDlg "$errormsg"
        exit 0
fi


echo home $HOME
cp $installHome/$pubApp/setprofile.sh $logindir
chmod 770 $logindir/setprofile.sh
$installDir/changeowner $login $logindir/setprofile.sh | read retmsg
echo returned message from changewoner is $retmsg
echo $retmsg | cut -f1 -d : | read message

if [ $message = $error	]
then
echo $retmsg | cut -f2 -d : | read errnum
echo $retmsg | cut -f3 -d : | read errormsg
#echo $errormsg
#exit $errnum
        java oem.edge.ed.odc.remoteviewer.ErrMsgDlg "$errormsg"
        exit 0
fi

cd $installDir
# Starting the program to start the viewer
$installDir/LaunchPubApp $login $pubApp $dest
echo $?
if [ "$?" -ne "0" ]
then
        #echo Could not LaunchPubApp
        #exit 114
        java oem.edge.ed.odc.remoteviewer.ErrMsgDlg "Could not LaunchPubApp"
        exit 0
fi

echo `whoami` 

# Removing the .Xauthority file from the Klogged user id's home
rm $dest
if [ "$?" -ne "0" ]
then
        #echo Could not remove XAUTH
        #exit 115
        java oem.edge.ed.odc.remoteviewer.ErrMsgDlg "Could not remove XAUTH"
        exit 0
fi



if [ $fileSystemtype = Y ]
then

# Removing the tokens for the Klogged userid's cell
/usr/afsws/bin/unlog

cd $pwd
if [ "$?" -ne "0" ]
then
        #echo Could not change to install dir
        #exit 116
        java oem.edge.ed.odc.remoteviewer.ErrMsgDlg "Could not change to install dir"
        exit 0

fi
echo $pwd
echo $adminlogin
# KLOGing with the adminID and the decoded password from the adminID file
grep "^$adminID:" $adminIDfile  | cut -f3 -d :  | java oem.edge.ed.odc.remoteviewer.Base64DecoderForPubApp | /usr/afsws/bin/klog $adminlogin -cell eda -pipe


cd $gridDir
if [ "$?" -ne "0" ]
then
        #echo Could not change to grid dir
        #exit 117
        java oem.edge.ed.odc.remoteviewer.ErrMsgDlg "Could not change to grid dir"
        exit 0

fi
/usr/afsws/bin/fs setacl . $login none

j=0
while [ i -gt j ]
do
	projdir=${assignedProject[j=j+1]%%/*}
        echo $projdir
        $pwd/_set_acl $projdir $login none
	if [ "$?" -ne "0" ]
	then
        #	echo Could not unset the acls for the userid
        #exit 118
        java oem.edge.ed.odc.remoteviewer.ErrMsgDlg "Could not unset the acls for the userid"
        exit 0

	fi
 
done

else # CLEANUP FOR UNIX FILE SYSTEM

# DELETING THE LOGINID FROM THE RESPECTIVE GROUPS
temp=$projectlinks
 while [ temp ]
 do
        path=${temp%% *}
	grep $path $groupmapfile  | cut -f1 -d :  | read projname

	echo changed projname is $projname
        if [ -z "$projname" ] 
        then
	basename $projlinkdir | read projname
        fi
        $installDir/UpdateGroup - $login $projname | read retmsg
echo returned message from UpdateGroup is $retmsg
echo $retmsg | cut -f1 -d : | read message

if [ $message = $error	]
then
echo $retmsg | cut -f2 -d : | read errnum
echo $retmsg | cut -f3 -d : | read errormsg
#echo $errormsg
#exit $errnum
        java oem.edge.ed.odc.remoteviewer.ErrMsgDlg "$errormsg"
        exit 0
fi

        temp=${temp#${path} }
        if [ $path = $temp ]
        then
         break
        exit
        fi
done


fi


# Removing the setprofile.sh
rm $logindir/setprofile.sh

cd $installDir

$installDir/idManager returnid $pubApp $installHome $login
#$installDir/idManager returnid $gridDir $login
	if [ "$?" -ne "0" ]
        then
        #        echo Could not return the id to the idmanager
        #exit 119
        java oem.edge.ed.odc.remoteviewer.ErrMsgDlg "Could not return the id to the idmanager"
        exit 0
        fi

