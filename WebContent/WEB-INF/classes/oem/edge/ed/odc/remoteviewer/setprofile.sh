echo before exporting home
echo HERE I AM 
pwd
/usr/afsws/bin/tokens
export HOME=$1
/usr/afsws/bin/tokens
cd $1
pwd
echo pubapp name is $2
#/afs/eda/@sys/prod/bin/netscape
pubAppCommandfile=$INSTALLDIR/pubAppCommandMap
grep "^$2:" $pubAppCommandfile  | cut -f2 -d :  | read pubAppCommand 
echo `$pubAppCommand`
