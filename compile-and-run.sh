#!/bin/bash
mvn clean install -Pdesktop
java -jar d2d2-world-arena-desktop/target/*dep*jar -Pserver=d2d2.world

