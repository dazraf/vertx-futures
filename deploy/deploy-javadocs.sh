#!/usr/bin/env bash
SCRIPT_DIR=`dirname $0`
echo "script directory: $SCRIPT_DIR"
pushd "$SCRIPT_DIR/.."
mvn clean javadoc:javadoc
popd
pushd "$SCRIPT_DIR/../target/site/apidocs/"
git init
git remote add javadoc https://dazraf@github.com/dazraf/vertx-futures.git
git fetch --depth=1 javadoc gh-pages
git add --all
git commit -m "javadoc"
git merge --no-edit -s ours remotes/javadoc/gh-pages
git push javadoc master:gh-pages
rm -rf .git
popd

