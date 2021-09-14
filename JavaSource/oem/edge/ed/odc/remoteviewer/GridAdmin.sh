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
# 124 - Invalid arguments
# 125 - Project could not be added to user 


set -x
#echo `pwd` | read InstalledDir
dirname $0 | read InstalledDir
echo $InstalledDir
let argcnt=0
let argpos=0
for arg in $*; do
#echo argument is $arg
case $arg in
     -cmd) 
          let argpos=1 ;;
     -path) 
          let argpos=2 ;;
     -user) 
          let argpos=3 ;;
          # echo arg is $argpos ;;
     -proj) 
          let argpos=4 ;;
          #echo arg is $argpos ;;
         *)
          if test $argpos -eq 1; then
          command=$arg
          #echo command in $command 
          elif test $argpos -eq 2; then
          path=$arg
          #echo path is $path 
          elif test $argpos -eq 3; then
          username=$arg
          #echo username is $username 
          elif test $argpos -eq 4; then
          projname=$arg
          #echo projname is $projname 
          else
          echo $arg is Invalid argument. Exiting.
		echo USAGE : gridadmin -cmd addprojtouser -path gridpath -user username -proj projectname
		echo USAGE : gridadmin -cmd delprojtouser -path gridpath -user username -proj projectname
		echo USAGE : gridadmin -cmd createproj/delproj -path /home -proj proectname
		echo USAGE : gridadmin -cmd creategrid -path gridpath
		echo USAGE : gridadmin -cmd delgrid -path gridpath
		echo USAGE : gridadmin -cmd createuser -path gridpath -user username
		echo USAGE : gridadmin -cmd deluser -path gridpath -user username
		echo USAGE : gridadmin -cmd listusers -path gridpath 
		echo USAGE : gridadmin -cmd listprojforuser -path gridpath -user username
		echo USAGE : gridadmin -cmd listproj -path gridpath
          exit 0
          fi
          let argpos=-1

esac
done

if test -z "$command"; then
echo -cmd argument is missing. Exiting...
echo USAGE : gridadmin -cmd addprojtouser -path gridpath -user username -proj projectname
echo USAGE : gridadmin -cmd delprojtouser -path gridpath -user username -proj projectname
echo USAGE : gridadmin -cmd createproj/delproj -path /home -proj proectname
echo USAGE : gridadmin -cmd creategrid -path gridpath
echo USAGE : gridadmin -cmd delgrid -path gridpath
echo USAGE : gridadmin -cmd createuser -path gridpath -user username
echo USAGE : gridadmin -cmd deluser -path gridpath -user username
echo USAGE : gridadmin -cmd listusers -path gridpath 
echo USAGE : gridadmin -cmd listprojforuser -path gridpath -user username
echo USAGE : gridadmin -cmd listproj -path gridpath
exit 120
fi


if test $command = "createproj" || test $command = "delproj"; then
if test $# -ne 6; then
echo Required arguments are missing. Exiting...
echo USAGE : gridadmin -cmd createproj/delproj -path /home -proj proectname
exit 120
fi
if test -z $path; then
echo -path argument is missing. Exiting...
echo USAGE : gridadmin -cmd createproj/delproj -path /home -proj proectname
exit 120
fi
if test ! -a $path && test -d $path; then
echo $path is invalid argument
exit 124
fi
elif test $command = "creategrid"; then
if test $# -ne 4; then
echo Required arguments are missing. Exiting...
echo USAGE : gridadmin -cmd creategrid -path gridpath
exit 120
fi
if [[ -z $path ||  -a $path ||  -d $path ]]
then
        echo Invalid griddir name $path or Grid Directory $path is already exist
        exit 124
fi
mkdir $path
chgrp "ctxanon" $path
cd $path
mkdir ccid
chgrp "ctxanon" ccid
chmod 750 ccid
mkdir common
chgrp "ctxanon" common
chmod 750 common
cd common
touch adminID
chgrp "ctxanon" adminID
touch groupmap
chgrp "ctxanon" groupmap
echo Grid dir $path is created successfully
exit 0
elif test $command = "delgrid"; then
if test $# -ne 4; then
echo Required arguments are missing. Exiting...
echo USAGE : gridadmin -cmd delgrid -path gridpath
exit 120
fi
if [[ -z $path || ! -a $path || ! -d $path ]]
then
        echo Invalid griddir name $path
        exit 124
fi
rm -r $path
echo Grid dir $path is deleted successfully
exit 0
elif test $command = "createuser"; then
if test $# -ne 6; then
echo Required arguments are missing. Exiting...
echo USAGE : gridadmin -cmd createuser -path gridpath -user username
exit 120
fi
if [[ -z $path || ! -a $path || ! -d $path ]]
then
        echo Invalid griddir name $path
        exit 124
fi
if [[ ! -a $path/ccid ]]
then
        mkdir $path/ccid
fi
if [[ ! -d $path/ccid ]]
then
 	rm -r $path/ccid
        mkdir $path/ccid
fi
if [[ -z $username ||  -a $path/ccid/$username || -d $path/ccid/$username ]]
then
        echo Invalid user name $username or $username is already exist.
        exit 124
fi
mkdir $path/ccid/$username
chgrp "ctxanon" $path/ccid/$username
chmod 750 $path/ccid/$username
echo User $username is created successfully
exit 0
elif test $command = "deluser"; then
if test $# -ne 6; then
echo Required arguments are missing. Exiting...
echo USAGE : gridadmin -cmd deluser -path gridpath -user username
exit 120
fi
if [[ -z $path || ! -a $path || ! -d $path || ! -a $path/ccid || ! -d $path/ccid ]]
then
        echo Invalid griddir name $path
        exit 124
fi
if [[ -z $username || ! -a $path/ccid/$username || ! -d $path/ccid/$username ]]
then
        echo Invalid user name $username or $username is not exist.
        exit 124
fi
rm -r $path/ccid/$username
echo User $username is deleted successfully
exit 0


elif test $command = "addprojtouser"; then
if test $# -ne 8; then
echo Required arguments are missing. Exiting...
echo USAGE : gridadmin -cmd addprojtouser -path gridpath -user username -proj projectname
exit 120
fi
if [[ -z $path || ! -a $path || ! -d $path || ! -a $path/ccid || ! -d $path/ccid ]]
then
        echo Invalid griddir name $path
        exit 124
fi
#if [[ -z $username || ! -a $path/ccid/$username || ! -d $path/ccid/$username ]]
#then
#        echo Invalid user name $username or $username is already exist.
#        exit 124
#fi
if [[ -z $projname || -a $path/ccid/$username/$projname || -d $path/ccid/$username/$projname ]]
then
        echo Invalid project name $projname or $projname is already added to user $username.
        exit 124
fi


if [[ ! -a $path/ccid ]]
then
        mkdir $path/ccid
fi
if [[ ! -d $path/ccid ]]
then
 	rm -r $path/ccid
        mkdir $path/ccid
fi
if [[ ! -a $path/ccid/$username ]]
then
        mkdir $path/ccid/$username
chgrp "ctxanon" $path/ccid/$username
chmod 750 $path/ccid/$username
fi
if [[ ! -d $path/ccid/$username ]]
then
 	rm -r $path/ccid/$username
        mkdir $path/ccid/$username
chgrp "ctxanon" $path/ccid/$username
chmod 750 $path/ccid/$username
fi

echo `ls $path | tr -s "\t" " " | cut -f11 -d" "` | read prjprntdirs
let dircount=1
let projcreated=0
for dname in $prjprntdirs
do
        if [ $dircount = $dname ]
        then
                let dircount=dircount+1
                echo `ls $path/$dname  | tr -s "\t" " " | cut -f11 -d" "` | read projectdirs
                for projectname in $projectdirs
                do

                # echo  projname is $projname and local proj is $projectname
                if [ $projname = $projectname ]
                then
			cd $path/ccid/$username
			ln -s ../../$dname/$projname $projname 
			grep $projname $path/common/groupmap | cut -f1 -d : | read grpname
			echo GROUPNAME $grpname	
			$InstalledDir/changegroup  $grpname $projname
			let projcreated=1
			cd $path
                        break
                fi
                done
        fi
        if [ $projcreated = 1 ]
        then
                break
        fi

done

        if [ $projcreated = 1 ]
        then
		echo Project $projname is added to user $username successfully
		exit 0
        else
		echo Project $projname could not be added to user $username
                exit 125
        fi

elif test $command = "delprojtouser"; then
if test $# -ne 8; then
echo Required arguments are missing. Exiting...
echo USAGE : gridadmin -cmd delprojtouser -path gridpath -user username -proj projectname
exit 120
fi
if [[ -z $path || ! -a $path || ! -d $path || ! -a $path/ccid || ! -d $path/ccid ]]
then
        echo Invalid griddir name $path
        exit 124
fi
if [[ -z $username || ! -a $path/ccid/$username || ! -d $path/ccid/$username ]]
then
        echo Invalid user name $username or $username is not exist.
        exit 124
fi
if [[ -z $projname || ! -a $path/ccid/$username/$projname || ! -d $path/ccid/$username/$projname ]]
then
        echo Invalid project name $projname or $projname is not added to user $username.
        exit 124
fi
rm -r  $path/ccid/$username/$projname
echo Project $projname is deleted from User $username successfully
exit 0

elif test $command = "listusers"; then
if test $# -ne 4; then
echo Required arguments are missing. Exiting...
echo USAGE : gridadmin -cmd listusers -path gridpath 
exit 120
fi
if [[ -z $path || ! -a $path || ! -d $path || ! -a $path/ccid || ! -d $path/ccid ]]
then
        echo Invalid griddir name $path
        exit 124
fi
echo Listed Users are :
ls -1 $path/ccid
exit 0

elif test $command = "listprojforuser"; then
if test $# -ne 6; then
echo Required arguments are missing. Exiting...
echo USAGE : gridadmin -cmd listprojforuser -path gridpath -user username
exit 120
fi
if [[ -z $path || ! -a $path || ! -d $path || ! -a $path/ccid || ! -d $path/ccid ]]
then
        echo Invalid griddir name $path
        exit 124
fi
if [[ -z $username || ! -a $path/ccid/$username || ! -d $path/ccid/$username ]]
then
        echo Invalid user name $username or $username is not exist.
        exit 124
fi
echo Listed Projects for $username user are :
ls -1 $path/ccid/$username
exit 0

elif test $command = "listproj"; then
if test $# -ne 4; then
echo Required arguments are missing. Exiting...
echo USAGE : gridadmin -cmd listproj -path gridpath
exit 120
fi
if [[ -z $path || ! -a $path || ! -d $path ]]
then
        echo Invalid griddir name $path
        exit 124
fi
cd $path
echo Listed Projects are :
echo `ls  | tr -s "\t" " " | cut -f11 -d" "` | read prjprntdirs
let dircount=1
let projcreated=0
for dname in $prjprntdirs
do
        if [ $dircount = $dname ]
        then
                let dircount=dircount+1
                ls -1 $dname
        fi
done
exit 0


else
echo -cmd argument $command is invalid argument
echo USAGE : gridadmin -cmd addprojtouser -path gridpath -user username -proj projectname
echo USAGE : gridadmin -cmd delprojtouser -path gridpath -user username -proj projectname
echo USAGE : gridadmin -cmd createproj/delproj -path /home -proj proectname
echo USAGE : gridadmin -cmd creategrid -path gridpath
echo USAGE : gridadmin -cmd delgrid -path gridpath
echo USAGE : gridadmin -cmd createuser -path gridpath -user username
echo USAGE : gridadmin -cmd deluser -path gridpath -user username
echo USAGE : gridadmin -cmd listusers -path gridpath 
echo USAGE : gridadmin -cmd listprojforuser -path gridpath -user username
echo USAGE : gridadmin -cmd listproj -path gridpath
exit 124
fi


if test $command = "createproj"; then
cd $path
echo `ls  | tr -s "\t" " " | cut -f11 -d" "` | read prjprntdirs
let dircount=1
let projcreated=0
for dname in $prjprntdirs
do
        if [ $dircount = $dname ]
        then
                let dircount=dircount+1
                cd $dname
                echo `ls  | tr -s "\t" " " | cut -f11 -d" "` | read projectdirs
                for projectname in $projectdirs
                do

                echo  projname is $projname and local proj is $projectname
                if [ $projname = $projectname ]
                then
                        echo $proj is exist in `pwd`
                        projcreated=1
			let lcount=10
			grep $projname ../../common/groupmap | wc -l | read lcount
		        if [ $lcount = 0 ]
			then
				echo $projname:$projname:$dircount >> ../../common/groupmap
			fi
                        break
                fi
                done
                cd ..
        fi
        if [ $projcreated = 1 ]
        then
                break
        fi

done
         if [ $projcreated != 1 ]
         then
             #$InstalledDir/CreateGroup + $projname #150605
	     groupname="rem"$dircount
	     $InstalledDir/CreateGroup + $groupname
             mkdir $dircount
             $InstalledDir/changegroup $groupname $dircount
             chmod 2770 $dircount	     
		cd $dircount
             mkdir $projname
             $InstalledDir/changegroup $groupname $projname
	     	
let lcount=10
grep $projname ../common/groupmap | wc -l | read lcount
       if [ $lcount = 0 ]
        then
                echo $groupname:$projname:$dircount >> ../common/groupmap
        fi
       fi
echo Project $projname is created successfully
exit 0
fi

if test $command = "delproj"; then
cd $path
echo `grep $projname common/groupmap `| cut -f3 -d : | read dircount
if [[  -z $dircount || ! -a $path/$dircount || ! -d $path/$dircount ]]
then
        echo Invalid project name $projname
        exit 124
fi
rm -r $dircount
$InstalledDir/CreateGroup - $projname

rm cat tempfile 1>/dev/null 2>/dev/null

cat common/groupmap |
while read line
do
        echo $line | grep $projname:$dircount 1>/dev/null 2>/dev/null
        if [ $? -eq 1 ]; then
                echo $line >>tempfile   
        fi
done
mv tempfile common/groupmap

echo Project $projname is deleted successfully

cd ccid
echo `ls  | tr -s "\t" " " | cut -f11 -d" "` | read  projectlinks1

echo $projectlinks1
temp=$projectlinks1
print ${#temp}
i=0
 while [ temp ]
 do
        path=${temp%% *}
	echo path is $path
 	cd $path
	rm $projname
	cd ..	
	temp=${temp#${path} }
        if [ $path = $temp ]
        then
                break
        exit
        fi
	i=i+1
done
echo i is $i






fi

