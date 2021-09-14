#!/bin/ksh
#
# STM code - Stop RealServer
#
ps $(cat $1/Logs/rmserver.pid) | grep rmserver >/dev/null

if [[ $? -eq 0 ]]
then
  kill $(cat $1/Logs/rmserver.pid)
fi

exit
