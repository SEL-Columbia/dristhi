#!/usr/bin/env bash

DATABASE=0
PORT=5984
HOST=http://127.0.0.1
USERNAME=0
PASSWORD=0
CONFIG_FOLDER=.
count="$#"
PROTO=0


if [ "$1" == "--help" ]
  then
     echo -e "Specify (Optional) a database name like \033[33m./setup_view_configs.sh -d <database name> \033[0m"
     echo -e "Specify (Optional) a host like \033[33m ./setup_view_configs.sh -h <hostname>\033[0m"
     echo -e "Specify (Optional) a port name like \033[33m ./setup_view_configs.sh -p <port>\033[0m"
     echo -e "Specify (Optional) a source folder like \033[33m ./setup_view_configs.sh -f <folder path>\033[0m"
     echo -e "Specify (Optional) a username like \033[33m ./setup_view_configs.sh -u <username>\033[0m"
     echo -e "Specify (Optional) a password like \033[33m ./setup_view_configs.sh -pwd <password>\033[0m"
     echo -e "Specify (Optional) a protocol e.g. http or https like \033[33m ./setup_view_configs.sh -proto <protocol>\033[0m"
     echo -e "Specify (Optional) any multiple arguments like \033[33m ./setup_view_configs.sh -u <username> -pwd <password> -d <database name>\033[0m"
     echo -e "For Authentication you can choose to pass the username and password as part of the host e.g. \033[33m ./setup_view_configs.sh -h http://<username>:<password>@<hostname>\033[0m"
     echo -e "Running \033[33m./setup_view_configs.sh -help \033[0m brings this help menu"

elif [ "$((count % 2))" != 0 ]
  then
     echo -e "\033[1;31m None or Incorrect Parameters supplied. Run ./setup_view_configs.sh --help to view a list of valids options \033[0m"
    exit 1

else

    varPrev=0
    #set the params
    for var in "$@"
    do
       if [ $varPrev == "-d" ];then
            DATABASE="$var"
       fi

       if [ $varPrev == "-p" ];then
            PORT="$var"
       fi

       if [ $varPrev == "-h" ];then
            HOST="$var"
       fi

       if [ $varPrev == "-f" ];then
            CONFIG_FOLDER="$var"
       fi

       if [ $varPrev == "-u" ];then
            USERNAME="$var"
       fi

       if [ $varPrev == "-pwd" ];then
            PASSWORD="$var"
       fi

       if [ $varPrev == "-proto" ];then
            PROTO="$var"
       fi

       varPrev="$var"
    done

    cd $CONFIG_FOLDER

    echo -e "\033[;32m Connecting to host \033[1;37m$HOST ...\033[0m"
    echo -e "\033[;32m Connecting on port \033[0m$PORT"
    echo -e "\033[;32m Couch Database specified \033[0m$DATABASE"

    if [ $CONFIG_FOLDER == "." ]; then
         echo -e "\033[;32m View Configuration folder supplied \033[0m $(pwd)"
    else

         echo -e "\033[;32m View Configuration folder supplied \033[0m$CONFIG_FOLDER"
    fi

    if [ $DATABASE != 0 ]; then

        FILES=*.json
        for f in $FILES
          do
            echo -e "\033[;33m Processing File\033[0m $f file "

                    if [[ $HOST == https* ]]; then

                      if [[ $PROTO == 0 ]]; then
                         PROTO=${HOST:0:5}
                      fi

                      HOST=${HOST:8}
                    fi

                    if [[ $HOST == http* ]]; then

                      if [[ $PROTO == 0 ]]; then
                         PROTO=${HOST:0:4}
                      fi

                      HOST=${HOST:7}
                    fi

                    if [ $USERNAME != 0 ]; then


                        if [ $PASSWORD != 0 ]; then

                             curl -H 'Content-Type: application/json' -vX POST $PROTO://$USERNAME:$PASSWORD@$HOST:$PORT/$DATABASE -d @$f
                        else

                             curl -H 'Content-Type: application/json' -vX POST $PROTO://$USERNAME@$HOST:$PORT/$DATABASE -d @$f
                        fi

                    else

                      if [[ $PROTO == 0 ]]; then
                             PROTO=http
                      fi
                             curl -H 'Content-Type: application/json' -vX POST $PROTO://$HOST:$PORT/$DATABASE -d @$f


                    fi

              resultCode=$?;
              if [[ $resultCode != 0 ]];then
                 echo -e "\033[1;31m Failed \033[0m"
              fi


          done

            echo -e "\033[1;32m Script run complete...\033[0m"
    else

        echo -e "\033[1;31m You need to supply a database name. For help Run  ./setup_view_configs.sh --help \033[0m"
        exit 1

    fi

fi
