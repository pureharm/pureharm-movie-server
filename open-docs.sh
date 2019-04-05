#!/usr/bin/env bash

#   !! IMPORTANT !!
# You need to run > sbt mkSite at least once for this to even work
# ---
# handy script to get the micro-site served without having to remember where it is stored
#

cd docs/target/site/
jekyll serve & open http://localhost:4000/pure-movie-server/