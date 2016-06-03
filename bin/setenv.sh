#!/bin/sh

export JAVA_HOME=/usr/java/jdk1.7.0_67-cloudera
export CLASSPATH=$CLASSPATH:`hadoop classpath`
#export 
alias hj="${JAVA_HOME}/bin/java -cp $CLASSPATH "
