cat $1 | curl -X POST --data-binary @- -H "Content-Type: application/json" $2

