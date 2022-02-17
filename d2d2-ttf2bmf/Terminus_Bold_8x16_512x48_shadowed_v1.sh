#!/bin/bash
alias java='/home/ancevt/Software/Java/jdk/bin/java'

name="Terminus_Bold_8x16_spaced_shadowed_v1.bmf"
src="$HOME/.fonts/terminus-ttf-4.49.1/TerminusTTF-Bold-4.49.1.ttf"
png="src/main/resources/Terminus8x16_spaced_shadowed_v1.png"
target="$HOME/workspace/ancevt/d2d2/d2d2-world-desktop/src/main/resources/assets/bitmapfonts/$name"

rm -v $name

java -jar target/d2d2-ttf2bmf-*-jar-with-dependencies.jar \
  --font-size 14 \
  --bold \
  --atlas-size 872x80 \
  --input $src \
  --output $name \
  --gui \
  --meta-data-only \
  --d2d2-world-special \
  || exit

cat $png >> $name || exit

rm -v "$target"

cp -v $name "$target"
