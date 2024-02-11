FROM public.ecr.aws/lambda/python:3.8
ARG HADOOP_VERSION=3.2.4
ARG AWS_SDK_VERSION=1.11.901
ARG PYSPARK_VERSION=3.3.0
RUN yum update -y && \
    yum -y install yum-plugin-versionlock && \
    yum -y versionlock add java-1.8.0-openjdk-1.8.0.362.b08-0.amzn2.0.1.x86_64 && \
    yum -y install java-1.8.0-openjdk wget curl
RUN pip install --upgrade pip && \
    pip install pyspark==$PYSPARK_VERSION
ENV JAVA_HOME="/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.362.b08-0.amzn2.0.1.x86_64/jre"
ENV PATH=${PATH}:${JAVA_HOME}/bin
ENV SPARK_HOME="/var/lang/lib/python3.8/site-packages/pyspark"
ENV PATH=$PATH:$SPARK_HOME/bin
ENV PYTHONPATH=$SPARK_HOME/python:$SPARK_HOME/python/lib/py4j-0.10.9-src.zip:$PYTHONPATH
ENV PATH=$SPARK_HOME/python:$PATH
RUN mkdir $SPARK_HOME/conf
