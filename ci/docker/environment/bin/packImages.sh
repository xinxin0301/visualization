#!/bin/sh
cd `dirname $0`
CURRENT_DIR=$PWD

DOCKER_IMAGE_DIR=$CURRENT_DIR/../docker-images

#拉取并保存镜像
docker pull product.repo/consul:0.9.4.1
docker save > $DOCKER_IMAGE_DIR/consul0.9.4.1.tar product.repo/consul:0.9.4.1

docker pull product.repo/jre:1.8.0_191.1
docker save > $DOCKER_IMAGE_DIR/jre1.8.0_191.1.tar product.repo/jre:1.8.0_191.1

docker pull product.repo/redis:4.0.2.1
docker save > $DOCKER_IMAGE_DIR/redis4.0.2.1.tar product.repo/redis:4.0.2.1

docker pull product.repo/mysql:8.0.18.2
docker save > $DOCKER_IMAGE_DIR/mysql8.0.18.2.tar product.repo/mysql:8.0.18.2


docker pull product.repo/jre-with-font:1.8.0_191.1
docker save > $DOCKER_IMAGE_DIR/jre-with-font1.8.0_191.1.tar product.repo/jre-with-font:1.8.0_191.1

docker pull product.repo/jre-with-font:1.8.0_191.2
docker save > $DOCKER_IMAGE_DIR/jre-with-font1.8.0_191.2.tar product.repo/jre-with-font:1.8.0_191.2
