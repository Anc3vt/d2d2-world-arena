#!/bin/bash

./clean.sh

while getopts "adh" opt
do
   	case $opt in
        w) BUILD_WEB_TARGET=$OPTARG;;
	    d) DEFAULT_GAME_SERVER=$OPTARG;;
        h) printf "Example:\n./deploy-client.sh -w 'd2d2.world:/var/www/d2d2.world/builds' -d 'd2d2.world:3333'\n"
            exit;;
    	*) ;;
	esac
done

if [[ -z $BUILD_WEB_TARGET ]]; then
    BUILD_WEB_TARGET='ancevt@d2d2.world:/var/www/d2d2.world/builds/'
fi

if [[ -z $DEFAULT_GAME_SERVER ]]; then
    DEFAULT_GAME_SERVER="d2d2.world:3333"
fi

echo Start client deploying...
echo Build web target: $BUILD_WEB_TARGET
echo Client default game server is $DEFAULT_GAME_SERVER

sleep 3

mvn clean install -Ddefault-game-server=$DEFAULT_GAME_SERVER -Pclient -Pexe \
|| {
    printf "\nAborted"
    exit;
}

mkdir -p buildtmp/
cd buildtmp/
cp -v ../d2d2-world-arena-client/target/*dep* .
cp -v ../d2d2-world-arena-client/target/*exe .

rename 's/-jar-with-dependencies//' *.jar

scp * "$BUILD_WEB_TARGET"

echo Complete

