rm cat tempfile 1>/dev/null 2>/dev/null

echo $2$3 | read newline

cat $1 |
while read line
do
        echo $line | grep $2 1>/dev/null 2>/dev/null
        if [ $? -eq 1 ]; then
                echo $line >>tempfile   #line not interesting. just write to a temp file
        else                            #useful .... need to change
                echo $newline >>tempfile
        fi
done
mv tempfile $1
