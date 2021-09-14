set -x
#echo `pwd` | read installDir
installDir=/home/pubApp/COMMON #150605
echo installdir is $installDir
export INSTALLDIR=$installDir
dirname $installDir | read installHome
#echo installHome is $installHome
#echo " "
#echo Please enter the application name
if test $# -ne 1;  then
echo Insufficient arguments. USAGE AppDel.sh ApplicationName
exit 0
fi


 if [[ -z $1 ]]
 then
          echo Could Not find the  file
          exit 0
 fi
 
  rm -r $installHome/$1


