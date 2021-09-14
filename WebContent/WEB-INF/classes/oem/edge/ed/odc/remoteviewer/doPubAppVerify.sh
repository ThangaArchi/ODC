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
tokenvalue=$1
echo $DISPLAY
echo token value is $tokenvalue  
export PATH=/usr/java14/bin:$PATH
export CLASSPATH=./EDODCRemoteViewer.jar
error="Error"
echo `pwd` | read installDir
echo installdir is $installDir
export INSTALLDIR=$installDir
dirname $installDir | read installHome
echo installHome is $installHome


echo  Original Tokens `/usr/afsws/bin/tokens`

echo Tokens after unlogging `/usr/afsws/bin/tokens`

# Decoding the token
echo `java oem.edge.ed.odc.remoteviewer.ValidatePubApp token $tokenvalue decodekey PubAppTest_Decode.key` | read decodedToken 
#decodedToken="XTERM@crichton"
decodedToken="XCLOCK@test1@remote.com"
#decodedToken="XTERM@test1@remote.com"
echo decodedtoken is $decodedToken

# Splitting into userid and pubApp

echo $decodedToken |cut -f1 -d @   | read pubApp
#echo $decodedToken |cut -f2 -d @   | read userid
echo $decodedToken |cut -f 2,3 -d @   | read userid
echo userid $userid appname $pubApp

# Checking for error in decoding the token
if [ $pubApp = $error ]
then
echo test error is $userid >&2
exit 101
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
	echo Delpoyed environment Unrecognized
	exit 103
fi


echo gridDir is $gridDir

if [[ -z $gridDir ]]
then
	echo Invalid Grid Dir
 	exit 104
fi



# Mapping the userid to the afs userid to be used
#echo `grep $userid $mappingfile `| cut -f2 -d : | read login 
echo `pwd` 
$installDir/UserManager obtainid $pubApp $installDir |  read mappedID
echo mapped id is $mappedID
echo $mappedID | cut -f1 -d : | read projcount
echo $mappedID | cut -f2 -d : | read userid
echo $mappedID | cut -f3 -d : | read pubApp
let count=0
let pcount=4
while (( $count < $projcount)); do
echo $mappedID | cut -f$pcount -d : | read projname[$count]
echo projname is ${projname[$count]} and projcount is $pcount
let count=count+1
let pcount=pcount+1
done



if [ $userid = $error	]
then
echo $mappedID | cut -f2 -d : | read errnum
echo $mappedID | cut -f3 -d : | read errormsg
echo $errormsg
exit $errnum
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
        echo Could Not read admin id  
        exit 105
fi

# KLOGing with the adminID and the decoded password from the adminID file
grep "^$adminID:" $adminIDfile  | cut -f3 -d :  | java oem.edge.ed.odc.remoteviewer.Base64DecoderForPubApp | /usr/afsws/bin/klog $adminlogin -cell eda -pipe

if [ "$?" -ne "0" ]
then
        echo Could Not login with the admin id
        exit 106
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
        echo Could Not change to the grid dir
        exit 107
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
        	echo Could Not change to the linked path
        	exit 108
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
        	echo Could Not set acls for the id in the grid dir
        	exit 109
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
        echo Could Not login with the user id
        exit 110
fi



else # ELSE  IF IT IS UNIX FILE SYSTEM

#groupmapfile=$installHome/$pubApp/groupmap
groupmapfile=$gridDir/common/groupmap

echo `ls  | tr -s "\t" " " | cut -f11 -d" "` | read  projectlinks

echo $projectlinks
echo `ls | tr -s "\t" " " | cut -f11 -d" "` | read  projectlinks1
echo $projectlinks1
temp=$projectlinks1
print ${#temp}
i=0
for ppath in $temp
 do
        #path=${temp%% *}
        #echo path is $path
        #realpath=${path#../../}
	#echo $realpath
        #assignedProject[i=i+1]=$realpath

        #temp=${temp#${path} }
let count=0
let projexistcount=0
while (( $count < $projcount)); do

        if [ ${projname[$count]} = $ppath ]
        then
                let projexistcount=projexistcount+1
                projname[$count]=` `
          break
        fi
let count=count+1
done

done
echo i is $i

let count=0
while (( $count < $projcount)); do

cd $gridDir
cd ccid
cd $userid
if [ "$?" -ne "0" ]
then
        echo Could Not change to the grid dir
        exit 107
fi

        if [ -n "${projname[$count]}" ] 
        then
	let linkcreated=0
        cd ../..
        pwd 
	echo `ls  | tr -s "\t" " " | cut -f11 -d" "` | read prjprntdirs
	let dircount=1
        for dname in $prjprntdirs 
        do
            # echo dname is $dname

        if [ $dircount = $dname ]
        then
                let dircount=dircount+1
                cd $dname
		echo `ls  | tr -s "\t" " " | cut -f11 -d" "` | read projectdirs
                for projectname in $projectdirs 
                 do

		echo  projname is ${projname[$count]} and local proj is $projectname
		if [ ${projname[$count]} = $projectname ]
		then
			echo ${projname[$count]} is exist in `pwd`
                        cd $projectname
                        echo `pwd` | read prjpath
                        echo pwd is $prjpath
                        cd ../../ccid/$userid
                        #ln -s $prjpath $projectname
                        ln -s ../../$dname/$projectname $projectname
			if [ "$?" -ne "0" ]
			then
				echo Error in creating link for $prjpath
				exit 107
			fi
			let lcount=0
			grep $projectname $groupmapfile | wc -l | read lcount
			       if [ $lcount = 0 ]
				then
					echo $projectname:$projectname >> $groupmapfile
				fi
                        chgrp -h ctxanon $projectname
                        echo User $userid is successfully tested for project $projectname
                        cd $prjpath/..
                        linkcreated=1
                        
		  break
		fi
                done
                cd ..
        fi

 	if [ $linkcreated = 1 ]
	then
		break
	fi

        done
         
 	if [ $linkcreated = 0 ]
	then
         
          $installDir/CreateGroup + ${projname[$count]}

if [ "$?" -ne "0" ]
then
        echo Error in new group creation ${projname[$count]}
        exit 107
fi
	  mkdir $dircount
          chgrp ${projname[$count]} $dircount

if [ "$?" -ne "0" ]
then
        echo Error in change group of folder $dircount to ${projname[$count]}
        exit 107
fi
          cd $dircount
          mkdir ${projname[$count]}
          chgrp ${projname[$count]} ${projname[$count]}

if [ "$?" -ne "0" ]
then
        echo Error in change group of folder ${projname[$count]} to ${projname[$count]}
        exit 107
fi

          cd ${projname[$count]}
		echo `pwd` | read prjpath
		cd ../../ccid/$userid
		#ln -s $prjpath ${projname[$count]}
                ln -s ../../$dircount/${projname[$count]} ${projname[$count]}
if [ "$?" -ne "0" ]
then
        echo Error in creating link for $prjpath
        exit 107
fi
		chgrp -h ctxanon ${projname[$count]}
let lcount=10
grep ${projname[$count]} $groupmapfile | wc -l | read lcount
       if [ $lcount = 0 ]
        then
                echo ${projname[$count]}:${projname[$count]} >> $groupmapfile
        fi

		linkcreated=1
                echo User $userid is successfully tested for project ${projname[$count]}
          $installDir/CreateGroup - ${projname[$count]}

if [ "$?" -ne "0" ]
then
        echo Error in new group creation ${projname[$count]}
        exit 107
fi
	fi
        
        fi
let count=count+1
done
fi
