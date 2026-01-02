# FROM openjdk:11
FROM eclipse-temurin:11
# initialize dependencies version tag & evironment path
ENV MAVEN_HOME=/usr/local/maven/apache-maven-3.9.12
# ENV JAVA_HOME=/usr/local/openjdk-11
ENV PATH=$MAVEN_HOME/bin:$JAVA_HOME/bin:$PATH
# for Chinese mainland user:
# RUN sed -i 's/cn.archive.ubuntu.com/mirrors.ustc.edu.cn/g' /etc/apt/sources.list
# build dependencies
RUN apt-get update \
    && apt-get install wget g++-10 gcc-10 make libboost-all-dev -y \
    && wget https://dlcdn.apache.org/maven/maven-3/3.9.12/binaries/apache-maven-3.9.12-bin.tar.gz \
    && mkdir /usr/local/maven/ \
    && mkdir /root/.m2/ \
    && mkdir /root/.m2/repository/ \
    && tar -xvzf apache-maven-3.9.12-bin.tar.gz -C /usr/local/maven \
    && ln -s /usr/bin/g++-10 /usr/bin/g++ 
# copy Aster source code to container
COPY . /root/AsterDB/

# for Chinese mainland user:
# RUN echo "<settings> \
#         <mirrors> \
#             <mirror> \
#                 <id>aliyun</id> \
#                 <mirrorOf>central</mirrorOf> \
#                 <name>Aliyun Maven</name> \
#                 <url>https://maven.aliyun.com/repository/public</url> \
#             </mirror> \
#         </mirrors> \
# </settings>" >> /root/.m2/settings.xml

# build GraphKV
WORKDIR /root/AsterDB/GraphKV/
RUN make -j8 install-static DEBUG_LEVEL=0 DISABLE_WARNING_AS_ERROR=1 EXTRA_CXXFLAGS=-fPIC
RUN make -j8 install-shared DEBUG_LEVEL=0 DISABLE_WARNING_AS_ERROR=1 EXTRA_CXXFLAGS=-fPIC
RUN make -j8 rocksdbjava DEBUG_LEVEL=0 DISABLE_WARNING_AS_ERROR=1 EXTRA_CXXFLAGS=-fPIC

# build AsterDB
WORKDIR /root/AsterDB/
RUN apt-get update && \
    apt-get install -y git && \
    rm -rf /var/lib/apt/lists/*
RUN mvn install:install-file \
    -Dfile=GraphKV/java/target/rocksdbjni-8.9.0-linux64.jar \
    -DgroupId=org.rocksdb \
    -DartifactId=rocksdbjni \
    -Dversion=8.9.0 \
    -Dpackaging=jar \
    -DlocalRepositoryPath=/root/.m2/repository
RUN mvn clean install -pl tinkergraph-gremlin,gremlin-console -Dmaven.test.skip=true

# Add entrypoint wrapper
RUN printf '%s\n' '#!/usr/bin/env sh' \
  'set -e' \
  'if [ $# -gt 0 ]; then' \
  '  # user passed arguments, assume first one is a groovy script' \
  '  exec ./bin/gremlin.sh -e "$@"' \
  'else' \
  '  exec ./bin/gremlin.sh' \
  'fi' > /root/AsterDB/bin/gremlin-entrypoint.sh && \
  chmod +x /root/AsterDB/bin/gremlin-entrypoint.sh

ENTRYPOINT ["bin/gremlin-entrypoint.sh"]
