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

export DOLLAR='$'
pushd ${BASE_DIR}

echo .
echo running from $(pwd)
echo .

mkdir -p ${BASE_DIR}/k8s/deploy

for f in ${BASE_DIR}/k8s/templates/*.yaml ;
    do
    envsubst < $f > ${BASE_DIR}/k8s/deploy/${f##*/} ;
    echo "Processing $f -> ${BASE_DIR}/k8s/deploy/${f##*/} ";
done


echo
echo "### Creating spring boot yaml file.. Customize it later as needed ###"
echo

cat << EOF > ${BASE_DIR}/src/main/resources/application.yaml
#Info endpoints
info:
  company: Dexcom
  project: integrations

# Management endpoints
management:
  endpoint:
    restart:
      enabled:  true

spring:
  application:
    name: ${KUBE_APP_NAME}
  cloud:
    kubernetes:
      config:
        name: ${KUBE_APP_NAME}
        namespace: ${KUBE_NAMESPACE}
        sources:
          - name: ${KUBE_APP_NAME}
      reload:
        enabled: true
        mode: polling
        period: 2000
# Actuator endpoints
endpoints:
  cors:
    allowed-origins: "*"
  metrics:
    enabled: true
  configprops:
    enabled: true
  env:
    enabled: true
  beans:
    enabled: true

EOF

echo
echo "### Creating Dockerfile ###"
echo

cat << EOF > ${BASE_DIR}/Dockerfile
FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD build/libs/*.jar app.jar
CMD ["java","-jar","/app.jar"]
EOF


echo
echo "### Creating Google Cloud Build config ###"
echo

cat << EOF > ${BASE_DIR}/cloudbuild.yaml
substitutions:
  _DEXCOM_PROJECT_ID: ${KUBE_APP_NAME}
  _DEXCOM_NAMESPACE: ${KUBE_NAMESPACE}
  _DEXCOM_RELEASE_TAG: "latest"
images:
  - "gcr.io/${DOLLAR}PROJECT_ID/${DOLLAR}{_DEXCOM_PROJECT_ID}:${DOLLAR}{_DEXCOM_RELEASE_TAG}"
steps:
  - name: 'gcr.io/cloud-builders/gradle'
    args: ['build']
  - name: 'gcr.io/cloud-builders/docker'
    args: ['build', '--tag=gcr.io/${DOLLAR}PROJECT_ID/${DOLLAR}{_DEXCOM_PROJECT_ID}:${DOLLAR}{_DEXCOM_RELEASE_TAG}', '.']
  - name: 'gcr.io/cloud-builders/docker'
    args: ["push", "gcr.io/${DOLLAR}PROJECT_ID/${DOLLAR}{_DEXCOM_PROJECT_ID}:${DOLLAR}{_DEXCOM_RELEASE_TAG}"]
  - name: 'gcr.io/cloud-builders/kubectl'
    args:
      - 'set'
      - 'image'
      - 'deployment/${DOLLAR}{_DEXCOM_PROJECT_ID}-dep'
      - 'spring-boot-container=gcr.io/${DOLLAR}PROJECT_ID/${DOLLAR}{_DEXCOM_PROJECT_ID}:${DOLLAR}{_DEXCOM_RELEASE_TAG}'
      - '--namespace=${DOLLAR}{_DEXCOM_NAMESPACE}'
    env:
      - 'CLOUDSDK_COMPUTE_ZONE=${KUBE_REGION}'
      - 'CLOUDSDK_CONTAINER_CLUSTER=${KUBE_CLUSTER}'
EOF

echo
echo "###  NOTICE ###"
echo
echo "Please add required configurations to ** ${BASE_DIR}/src/main/resources/application.yaml ** for develop/test your application locally."
echo "The subsequent setup steps [TBD] will populate the Kubernetes namespace with required configurations."
echo

popd

