#!/usr/bin/env bash

cd `dirname $0`
for i in `ls -d conf_*`
do
        echo -n $i
        rm -f conf
        ln -s $i conf
        sh ./test_load_config_files.sh > tmp
        diff -u expected/test_load_config_files/$i tmp || (echo ' [NG]' ; exit 1)
        rm tmp
        rm conf
        echo ' [OK]'
done
