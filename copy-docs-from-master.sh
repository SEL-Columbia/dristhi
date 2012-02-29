#!/bin/sh

for file in schedules/*
do
  git show master:drishti-web/doc/$file >$file
done

