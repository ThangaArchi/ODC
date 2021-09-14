rm cat tempfile 1>/dev/null 2>/dev/null

echo $2$3 | read newline
let found=0

cat $1 |
while read line
do
        echo $line | grep $2 1>/dev/null 2>/dev/null
        if [ $? -eq 1 ]; then
               echo $line >>tempfile   #line not interesting. just write to a temp file
        else                            #useful .... need to change
               let found=found+1
               echo $line >>tempfile
        fi
done
if [ "$found" -ne "1" ]
then
        echo $newline >>tempfile
fi

mv tempfile $1
