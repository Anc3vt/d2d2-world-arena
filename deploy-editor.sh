#!/bin/bash

./clean.sh

while getopts "ah" opt
do
   	case $opt in
        w) BUILD_WEB_TARGET=$OPTARG;;
        h) printf "Example:\n./deploy-editor.sh -w 'd2d2.world:/var/www/d2d2.world/builds'\n"
            exit;;
    	*) ;;
	esac
done

if [[ -z $BUILD_WEB_TARGET ]]; then
    BUILD_WEB_TARGET='ancevt@d2d2.world:/var/www/d2d2.world/builds/'
fi

echo Start editor deploying...
echo Build web target: $BUILD_WEB_TARGET

sleep 3

mvn clean install -Peditor \
|| {
    printf "\nAborted"
    exit;
}

mkdir -p build/
mkdir -p buildtmp/
cd buildtmp/
cp -v ../d2d2-world-editor/target/*dep* .

rename 's/-jar-with-dependencies//' *.jar

cp * ../build/
scp * "$BUILD_WEB_TARGET"

echo Editor deployment complete

