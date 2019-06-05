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
echo Creating kubernetes objects
echo .


kubectl apply -f ./k8s/deploy/namespace.yaml
kubectl apply -f ./k8s/deploy/role.yaml
kubectl apply -f ./k8s/deploy/deployment.yaml
kubectl apply -f ./k8s/deploy/svc.yaml


echo .
echo Use the below command to create a configuraiton map to populate
echo the host kubernetes runtime with runtime config file.
echo .
echo "kubectl create configmap --namespace=${KUBE_NAMESPACE} ${KUBE_APP_NAME} --from-file={path to application.yaml} "
echo .

popd


