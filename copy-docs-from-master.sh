#!/bin/sh

git show master:drishti-web/doc/schedules/ | grep '\.html$' | while read file do
do
  git show master:drishti-web/doc/schedules/$file >schedules/$file
done

