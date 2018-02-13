#!/bin/sh
if [ -z "$1" ]
  then
    echo "No couchDB database name supplied"
    exit 1

else

FILES=*.json
for f in $FILES
  do
    echo "Processing $f file on DB $1"
  
     curl -H 'Content-Type: application/json' -vX POST http://127.0.0.1:5984/$1 -d @$f

  done

fi
