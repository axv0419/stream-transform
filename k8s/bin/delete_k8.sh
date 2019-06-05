#!/bin/bash

export BASE_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )/../.." >/dev/null 2>&1 && pwd )"

#project_config_file="project_config.yaml"
#
#if [ ! -f ${BASE_DIR}/${project_config_file} ]; then
#    echo "Project config file ${project_config_file} missing in current directory"
#    exit 0
#fi
#
#echo .
#echo "Bootstraping kubernetes project artifacts"
#echo .

. ${BASE_DIR}/k8s/bin/setenv.sh

pushd ${BASE_DIR}

echo .
echo Deleting kubernetes objects
echo .


kubectl delete -f ./k8s/deploy/svc.yaml
kubectl delete -f ./k8s/deploy/deployment.yaml
kubectl delete -f ./k8s/deploy/role.yaml
kubectl delete -f ./k8s/deploy/namespace.yaml

popd


