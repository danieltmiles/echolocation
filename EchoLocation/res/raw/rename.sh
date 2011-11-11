for fl in `ls`; do
    oldName=$fl
    newName=`echo $oldName | sed 's/L/l/g'`
    if [ "x$newName" != "x$fl" ]; then
        echo "mv $fl $newName"
        mv $fl $newName
    fi
done
