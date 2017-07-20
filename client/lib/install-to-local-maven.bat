@echo off

call mvn install:install-file -Dfile=TWL.jar -DgroupId=de.matthiasmann.twl -DartifactId=twl -Dversion=1.0.0 -Dpackaging=jar
