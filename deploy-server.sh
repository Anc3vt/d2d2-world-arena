#!/bin/bash

./clean.sh

while getopts "awh" opt
do
    case $opt in
        a) HOST=$OPTARG;;
        w) BUILD_WEB_TARGET=$OPTARG;;
        h) printf "Example:\n./deploy-server.sh -a 'd2d2.world' -w 'd2d2.world:/var/www/d2d2.world/builds'\n"
            exit;;
        *) ;;
    esac
done

if [[ -z $HOST ]]; then
    HOST='ancevt@d2d2.world'
fi

if [[ -z $BUILD_WEB_TARGET ]]; then
    BUILD_WEB_TARGET="d2d2.world:/var/www/d2d2.world/builds/"
fi

echo Start server deploying...
echo Host: $HOST
echo Build web target: $BUILD_WEB_TARGET
sleep 3

mvn clean install -Pserver \
|| {
    printf "\nAborted"
    exit;
}

mkdir -p build/
mkdir -p buildtmp/
cd buildtmp/
cp -v ../d2d2-world-arena-server/target/*dep* .

rename 's/-jar-with-dependencies//' *.jar

cp * ../build/
scp * "$BUILD_WEB_TARGET"
scp * "$HOST:app"

cd ..
ssh $HOST "rm -rf app/data/"
scp -r "d2d2-world-arena-server/data" "$HOST:app/"
ssh $HOST "ps x | grep java | grep 'd2d2-world-arena'| grep 3333 | sed 's/^ *//i' | cut -d ' ' -f 1 | xargs kill -9"

echo Server deployment complete

