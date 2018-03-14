#!/bin/bash
curl -L  "https://github.com/mybatis/migrations/releases/download/mybatis-migrations-3.3.1/mybatis-migrations-3.3.1-bundle.zip" > /tmp/mybatis-migrations-3.3.1-bundle.zip
mkdir -p /u01/postgres/data
chown -R postgres /u01/postgres
unzip /tmp/mybatis-migrations-3.3.1-bundle.zip -d /opt/mybatis
export MIGRATIONS_HOME=/opt/mybatis/mybatis-migrations-3.3.1
MIGRATIONS=$MIGRATIONS_HOME/bin
export PATH=$MIGRATIONS:$PATH
migrate  up --path=assets/migrations  --env=test --force 