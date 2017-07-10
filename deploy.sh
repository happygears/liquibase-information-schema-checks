#!/usr/bin/env bash

mvn  -DrepositoryId=happygears-repository \
     -DuniqueVersion=false \
     -DaltDeploymentRepository=snapshot-repo::default::file:../mvn-repo/ \
     clean deploy
