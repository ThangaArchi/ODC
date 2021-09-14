#echo `grep $userid $mappingfile `| cut -f2 -d : | read login
# KLOGing with the userid and the decoded password from the mapping file
#grep "^$userid:" $mappingfile  | cut -f3 -d :  | java oem.edge.ed.odc.remoteviewer.Base64DecoderForPubApp | /usr/afsws/bin/klog -setpag $login -cell eda -pipe
echo `pwd` | read installDir
echo installdir is $installDir
if test $# -ne 3;  then
echo Insufficient arguments. USAGE ChangePasswd ApplicationName NewPasswd
exit 0
fi

pubApp=$1
newpasswd=$2
error="Error"
echo pubApp is $pubApp
mappingfile=$installDir/../$pubApp/mappingfile
echo mappingfile is $mappingfile
cat $mappingfile | wc -l | read count
#$installDir/idManager obtainid $pubApp |  read mappedID
cat $mappingfile | wc -l | read count
echo count is $count
for i in 1 $count; do
awk NR==$i $mappingfile | read mappedID
echo mapped id is $mappedID
echo $mappedID | cut -f2 -d : | read login
echo $mappedID | cut -f3 -d : | read password

echo login is $login and passwd is $password

if [ $login = $error    ]
then
echo $userid does not have the required permissions
exit 102
fi
# KLOGing with the adminID and the decoded password from the adminID file
#grep "^$adminID:" $adminIDfile  | cut -f3 -d :  | java oem.edge.ed.odc.remoteviewer.Base64DecoderForPubApp | /usr/afsws/bin/klog $adminlogin -cell eda -pipe
#grep "^$userid:" $mappingfile  | cut -f3 -d :  | java oem.edge.ed.odc.remoteviewer.Base64DecoderForPubApp | /usr/afsws/bin/klog $login -cell eda -pipe
#java oem.edge.ed.odc.remoteviewer.Base64DecoderForPubApp $password | /usr/afsws/bin/klog $login -cell eda -pipe
#/usr/afsws/bin/klog $login -cell eda -password passw0rd

if [ "$?" -ne "0" ]
then
        echo Could Not login with the admin id
        exit 106
fi

echo `pwd`| read pwd
echo this is the present working order $pwd
#kpasswd -pr $login -pa oldpasswd -n newpasswd
rm $installDir/mapfile
echo "Y:$login:$2" >> $installDir/mapfile;
done

#echo Tokens after Klogging as admin `/usr/afsws/bin/tokens`


