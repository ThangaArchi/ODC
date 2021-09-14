set -x
dirname $0 | read installDir
echo installdir is $installDir
export INSTALLDIR=$installDir
dirname $installDir | read installHome
#echo installHome is $installHome
#echo " "
#echo Please enter the application name
if test $# -ne 5;  then
echo Insufficient arguments. USAGE AppSetup.sh ApplicationName griddir IDPrefix NumberOfIDS ApplicationPath
exit 0
fi

appname=$1
#HARDCODED FOR JFS ONLY
AFS="JFS"

cd $installHome
mkdir $appname
chgrp ctxanon $appname
chgrp ctxanon $appname/.
if [ "$?" -ne "0" ]
then
      echo Entered application folder is already exist in $installHome. Exiting.
      exit 0
fi
cp $installHome/APPJFSTEMPLATE/* $appname
chgrp ctxanon $appname/*

echo " "
apppath=$5
echo $apppath >> $appname/setprofile.sh
$installDir/FileContentModify.sh $installHome/$appname/PubApp.properties ApplicationPath= $apppath
if [ "$?" -ne "0" ]
then
        echo Could Not modify the PubApp.properties file
        rm -r $installHome/$appname
        exit 0
fi

cd $installDir

echo " "
griddir=$2


#echo `grep "$griddir" $installDir/GridListFile `| cut -f2 -d : | read gridinfo
echo `grep "$griddir" $installDir/GridListFile ` | read gridinfo

#if [ "$grdfound" -eq "0" ]
if [[ -z $gridinfo ]]
then
        echo Invalid grid home $griddir
        rm -r $installHome/$appname
        exit 1
fi
adminIDfile="$griddir/common/adminID"
adminID="adminID"
echo `grep $adminID $adminIDfile `| cut -f2 -d : | read adminlogin

if [[ -z $adminlogin ]]
then
        echo Could Not read admin id
        exit 0
fi
#chown $adminlogin $installHome/$appname/*

$installDir/FileContentModify.sh $installHome/$appname/PubApp.properties IgridDir= $griddir

if [ "$?" -ne "0" ]
then
        echo Could Not modify the PubApp.properties file
        rm -r $installHome/$appname
        exit 1
fi

#isAFS=w
#while (( "$isAFS" != "Y" && "$isAFS" != "N" )); do
#echo " "
#echo Please enter if it is AFS or JFS?(Y/N)
#read isAFS
echo $gridinfo | cut -f1 -d : | read isAFS
if [ $isAFS = $AFS    ]
then
isAFS=Y
#echo isAFS contains AFS : $isAFS
else 
isAFS=N
#echo isAFS contains JFS : $isAFS
fi

#done
$installDir/FileContentModify.sh $installHome/$appname/PubApp.properties ISAFS= $isAFS
if [ "$?" -ne "0" ]
then
        echo Could Not modify the PubApp.properties file
        rm -r $installHome/$appname
        exit 0
fi



#echo How many user ids would you like to add?
#read idcnt
idprefix=$3
$installDir/FileContentModify.sh $installHome/$appname/PubApp.properties IDPrefix= $idprefix
if [ "$?" -ne "0" ]
then
        echo Could Not modify the PubApp.properties file
        rm -r $installHome/$appname
        exit 0
fi
idcnt=$4
$installDir/FileContentModify.sh $installHome/$appname/PubApp.properties NumberofUsers= $idcnt
if [ "$?" -ne "0" ]
then
        echo Could Not modify the PubApp.properties file
        rm -r $installHome/$appname
        exit 0
fi
let count=0
let icount=0
#if [[ ! -a /$griddir/ccid ]]; then 
#mkdir $griddir/ccid
#fi
#if [[ ! -d /$griddir/ccid ]]; then 
#rm -r $griddir/ccid
#mkdir $griddir/ccid
#fi
echo " "
#while (( $count < $idcnt)); do
#let icount=icount+1
#let count=count+1
#echo Y:remview$count:cGFzc3cwcmQ= >> $installHome/$appname/mappingfile
#done
$installDir/updatemapfile obtainid $appname $idcnt

#let projcnt=0
#echo " "
#echo How many projects would you like to add?
#read projcnt
#let count=0
#let idcount=0
#let icount=0
#echo " "
#while (( $count < $projcnt)); do
#let icount=icount+1
#echo what is the name of project $icount?
#read projname[$count]
#let idcount=0
#while (( $idcount < $idcnt)); do
#echo projname is ${projname[$count]} and projcount is $idcount idname is ${idname[$idcount]} count is $count
#if [[ ! -a $griddir/ccid/${idname[$idcount]}/${projname[$count]} ]]; then 
#mkdir $griddir/ccid/${idname[$idcount]}/${projname[$count]}
#fi
#if [[ ! -d $griddir/ccid/${idname[$idcount]}/${projname[$count]} ]]; then 
#rm -r $griddir/ccid/${idname[$idcount]}/${projname[$count]}
#mkdir $griddir/ccid/${idname[$idcount]}/${projname[$count]}
#fi
#let idcount=idcount+1
#done

#$installDir/groupmapmodify.sh $installHome/$appname/groupmap ${projname[$count]}: ${projname[$count]}
#if [ "$?" -ne "0" ]
#then
#        echo Could Not modify the groupmap file $installHome/$appname/groupmap
#        rm -r $installHome/$appname
#        exit 0
#fi
#let count=count+1
#done
echo Setup of $appname application is completed successfully

