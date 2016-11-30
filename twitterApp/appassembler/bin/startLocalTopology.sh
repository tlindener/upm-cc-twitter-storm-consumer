#!/bin/sh
# ----------------------------------------------------------------------------
#  Copyright 2001-2006 The Apache Software Foundation.
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
# ----------------------------------------------------------------------------
#
#   Copyright (c) 2001-2006 The Apache Software Foundation.  All rights
#   reserved.


# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

PRGDIR=`dirname "$PRG"`
BASEDIR=`cd "$PRGDIR/.." >/dev/null; pwd`

# Reset the REPO variable. If you need to influence this use the environment setup file.
REPO=


# OS specific support.  $var _must_ be set to either true or false.
cygwin=false;
darwin=false;
case "`uname`" in
  CYGWIN*) cygwin=true ;;
  Darwin*) darwin=true
           if [ -z "$JAVA_VERSION" ] ; then
             JAVA_VERSION="CurrentJDK"
           else
             echo "Using Java version: $JAVA_VERSION"
           fi
		   if [ -z "$JAVA_HOME" ]; then
		      if [ -x "/usr/libexec/java_home" ]; then
			      JAVA_HOME=`/usr/libexec/java_home`
			  else
			      JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Versions/${JAVA_VERSION}/Home
			  fi
           fi       
           ;;
esac

if [ -z "$JAVA_HOME" ] ; then
  if [ -r /etc/gentoo-release ] ; then
    JAVA_HOME=`java-config --jre-home`
  fi
fi

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin ; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
  [ -n "$CLASSPATH" ] && CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
fi

# If a specific java binary isn't specified search for the standard 'java' binary
if [ -z "$JAVACMD" ] ; then
  if [ -n "$JAVA_HOME"  ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
      # IBM's JDK on AIX uses strange locations for the executables
      JAVACMD="$JAVA_HOME/jre/sh/java"
    else
      JAVACMD="$JAVA_HOME/bin/java"
    fi
  else
    JAVACMD=`which java`
  fi
fi

if [ ! -x "$JAVACMD" ] ; then
  echo "Error: JAVA_HOME is not defined correctly." 1>&2
  echo "  We cannot execute $JAVACMD" 1>&2
  exit 1
fi

if [ -z "$REPO" ]
then
  REPO="$BASEDIR"/repo
fi

CLASSPATH="$BASEDIR"/conf:"$REPO"/org/apache/kafka/kafka-clients/0.10.1.0/kafka-clients-0.10.1.0.jar:"$REPO"/net/jpountz/lz4/lz4/1.3.0/lz4-1.3.0.jar:"$REPO"/org/xerial/snappy/snappy-java/1.1.2.6/snappy-java-1.1.2.6.jar:"$REPO"/org/slf4j/slf4j-api/1.7.21/slf4j-api-1.7.21.jar:"$REPO"/it/unimi/dsi/fastutil/7.0.13/fastutil-7.0.13.jar:"$REPO"/org/apache/kafka/kafka_2.11/0.10.1.0/kafka_2.11-0.10.1.0.jar:"$REPO"/net/sf/jopt-simple/jopt-simple/4.9/jopt-simple-4.9.jar:"$REPO"/com/yammer/metrics/metrics-core/2.2.0/metrics-core-2.2.0.jar:"$REPO"/org/scala-lang/scala-library/2.11.8/scala-library-2.11.8.jar:"$REPO"/com/101tec/zkclient/0.9/zkclient-0.9.jar:"$REPO"/org/apache/zookeeper/zookeeper/3.4.8/zookeeper-3.4.8.jar:"$REPO"/jline/jline/0.9.94/jline-0.9.94.jar:"$REPO"/io/netty/netty/3.7.0.Final/netty-3.7.0.Final.jar:"$REPO"/org/scala-lang/modules/scala-parser-combinators_2.11/1.0.4/scala-parser-combinators_2.11-1.0.4.jar:"$REPO"/org/apache/storm/storm-core/1.0.2/storm-core-1.0.2.jar:"$REPO"/com/esotericsoftware/kryo/3.0.3/kryo-3.0.3.jar:"$REPO"/com/esotericsoftware/reflectasm/1.10.1/reflectasm-1.10.1.jar:"$REPO"/org/ow2/asm/asm/5.0.3/asm-5.0.3.jar:"$REPO"/com/esotericsoftware/minlog/1.3.0/minlog-1.3.0.jar:"$REPO"/org/objenesis/objenesis/2.1/objenesis-2.1.jar:"$REPO"/org/clojure/clojure/1.7.0/clojure-1.7.0.jar:"$REPO"/com/lmax/disruptor/3.3.2/disruptor-3.3.2.jar:"$REPO"/org/apache/logging/log4j/log4j-api/2.1/log4j-api-2.1.jar:"$REPO"/org/apache/logging/log4j/log4j-core/2.1/log4j-core-2.1.jar:"$REPO"/org/apache/logging/log4j/log4j-slf4j-impl/2.1/log4j-slf4j-impl-2.1.jar:"$REPO"/org/slf4j/log4j-over-slf4j/1.6.6/log4j-over-slf4j-1.6.6.jar:"$REPO"/javax/servlet/servlet-api/2.5/servlet-api-2.5.jar:"$REPO"/org/apache/storm/storm-kafka/1.0.2/storm-kafka-1.0.2.jar:"$REPO"/commons-io/commons-io/2.5/commons-io-2.5.jar:"$REPO"/org/apache/curator/curator-framework/2.10.0/curator-framework-2.10.0.jar:"$REPO"/org/apache/curator/curator-client/2.10.0/curator-client-2.10.0.jar:"$REPO"/com/google/guava/guava/16.0.1/guava-16.0.1.jar:"$REPO"/commons-lang/commons-lang/2.5/commons-lang-2.5.jar:"$REPO"/com/googlecode/json-simple/json-simple/1.1/json-simple-1.1.jar:"$REPO"/master2016/trandingTopology/0.0.1-SNAPSHOT/trandingTopology-0.0.1-SNAPSHOT.jar

ENDORSED_DIR=
if [ -n "$ENDORSED_DIR" ] ; then
  CLASSPATH=$BASEDIR/$ENDORSED_DIR/*:$CLASSPATH
fi

if [ -n "$CLASSPATH_PREFIX" ] ; then
  CLASSPATH=$CLASSPATH_PREFIX:$CLASSPATH
fi

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  [ -n "$CLASSPATH" ] && CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --path --windows "$JAVA_HOME"`
  [ -n "$HOME" ] && HOME=`cygpath --path --windows "$HOME"`
  [ -n "$BASEDIR" ] && BASEDIR=`cygpath --path --windows "$BASEDIR"`
  [ -n "$REPO" ] && REPO=`cygpath --path --windows "$REPO"`
fi

exec "$JAVACMD" $JAVA_OPTS  \
  -classpath "$CLASSPATH" \
  -Dapp.name="startLocalTopology.sh" \
  -Dapp.pid="$$" \
  -Dapp.repo="$REPO" \
  -Dapp.home="$BASEDIR" \
  -Dbasedir="$BASEDIR" \
  master2016.Top3App \
  en:MTVStars 172.17.0.1:2181 master2016-group10 /home/tobias/workspace/twitter-hashtag-topology "$@"
