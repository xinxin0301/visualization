#!/bin/sh
#当前应用容器的名称
PRODUCT_NAME=visualization
PRODUCT_VERSION=V1.0.0
DEPLOY_TAR_NAME=$PRODUCT_NAME$PRODUCT_VERSION

# 公共服务依赖
BASIC_SERVICE_FULL_NAME=()
# 该目录为挂载目录的公共目录，不建议修改。
if [ "$DIR_VOLUME" = "" ]
then
	DIR_VOLUME=/opt/products/volume	 
fi