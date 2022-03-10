#!/bin/bash
mvn install:install-file \
   -Dfile=d2d2-core/src/main/resources/Blooming.jar \
   -DgroupId=jar \
   -DartifactId=blooming-jar \
   -Dversion=1 \
   -Dpackaging=jar \
   -DgeneratePom=true

