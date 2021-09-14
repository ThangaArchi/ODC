
if [[ -z $1 ]] 
then
echo Error : Need an Argument 
echo Please specify \'I\' for Integration or \'P\' for Production 
echo usage: " PubAppPostInstall I "
exit
fi

if [ $1 != 'I' ] && [ $1 != 'P' ] 
then
echo Error : Invalid Argument 
echo Please specify 'I' for Integration or 'P' for Production
echo usage: " PubAppPostInstall I "
exit
fi


echo Checking versions of property files ...
`echo ls` | grep -x PubApp.properties | read curfile

if [[ -z $curfile ]] 
then
	echo Current Property File not found ...
	exec 3>> PubApp.properties.sample
	print -u3 ISPROD=$1
	exec 3<&-	
	cp PubApp.properties.sample PubApp.properties
	echo Updated New Property File ...
	echo PubApp Has been successfully installed
	rm PubApp.properties.sample

else
	newfile="PubApp.properties.sample"
	echo `grep VERSION $newfile `| cut -f2 -d = | read newVersion
	echo `grep VERSION $curfile `| cut -f2 -d = | read curVersion 

	if [ $newVersion = $curVersion ]
	then
		echo Installation upto date
	else
		echo New install has been found
        	exec 3>> PubApp.properties.sample
        	print -u3 ISPROD=$1
        	exec 3<&-
        	cp PubApp.properties.sample PubApp.properties
        	echo Updated New Property File ...
        	echo PubApp Has been successfully installed

	fi
	rm PubApp.properties.sample
 
fi
chgrp ctxanon *
chmod 4750 LaunchPubApp
chmod go+x doPubApp.sh setprofile.sh _set_acl
chmod 4750 idManager
echo Please make sure that you have an updated version of the file PubAppTest_Decode.key
