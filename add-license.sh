#!/bin/bash


modules=(
    $(find $(pwd) -type d -name "d2d2-*")
)

for module in ${modules[@]}; do
    
    cd $module

    filelist=(
        $(find src -name "*.java") 
    )

    for path in ${filelist[@]}; do
        cat $path | grep Copyright

        [[ $? == 1 ]] && {
            cat license-header.txt > tmp.tmp
            cat $path >> tmp.tmp
            cat tmp.tmp > $path
        }
    done
done

