#!/bin/bash

# Move to script's directory
cd `dirname $0`

# Get the kernel information
UNAME=`uname`

# Set the proper libpath
if [ "$UNAME" == "Darwin" ]; then
  export DYLD_LIBRARY_PATH=$DYLD_LIBRARY_PATH:./
elif [ "$UNAME" == "Linux" ]; then
  export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:./
fi

# Run the server!
java -jar lwjake2.jar +set dedicated 1 $@