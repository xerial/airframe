#!/bin/bash
set -e

if [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "master" -o "$TRAVIS_BRANCH" == "$TRAVIS_TAG" ]; then
  PUBLIC_IP=$(curl https://api.ipify.org)
  echo "Public IP Address of this machine: ${PUBLIC_IP}"
  openssl aes-256-cbc -K $encrypted_fa45534951b5_key -iv $encrypted_fa45534951b5_iv -in travis/secrets.tar.enc -out travis/secrets.tar -d
  tar xvf travis/secrets.tar
  if [ -z "$TRAVIS_TAG" ]; then
     # Publish a snapshot
     ./sbt "publishSnapshots"
  else
    case "$SCALA" in
       2.12)
         RELEASE=true SESSION=$SCALA ./sbt ++2.12.8 "; projectJVM2_13/publishSigned; projectJVM2_12/publishSigned; sonatypeBundleRelease"
         ;;
       2.13)
         RELEASE=true SESSION=$SCALA ./sbt ++2.13.0 "; projectJVM2_13/publishSigned; sonatypeBundleRelease"
         ;;
       2.11)
         RELEASE=true SESSION=$SCALA ./sbt ++2.11.12 "; projectJVM2_13/publishSigned; projectJVM2_12/publishSigned; sonatypeBundleRelease"
         ;;
       js)
         RELEASE=true SESSION=$SCALA ./sbt "; projectJS/publishSigned; sonatypeBundleRelease"
         ;;     # Publish a release version
    esac
  fi
else
  echo "This not a master branch commit. Skipping the release step"
fi
