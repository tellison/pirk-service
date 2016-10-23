cat $1 | curl -X PUT --data-binary @- -H "Content-Type: application/json" $2

