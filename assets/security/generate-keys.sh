#!/bin/bash

if [ -z "$1" ]; then
    echo "Usage: $0 prefix"
    exit 1
fi

prefix=$1

echo -e "====== PLEASE make sure that you enter the same password every time it is asked here. ====="

echo -e "Generating the keyfile. You will need to enter a password here."
openssl genrsa -des3 -out ${prefix}.server.key 1024

echo -e "\nGenerating a certificate request file. You'll be asked to enter the same password."
openssl req -new -key ${prefix}.server.key -out ${prefix}.server.csr

echo -e "\nRemoving the password from the keyfile. Enter the password ... again."
cp ${prefix}.server.key ${prefix}.server.key.original
openssl rsa -in ${prefix}.server.key.original -out ${prefix}.server.key

echo -e "\nCreating a certificate out of the key and the certificate request."
openssl x509 -req -days 1095 -in ${prefix}.server.csr -signkey ${prefix}.server.key -out ${prefix}.server.crt

echo -e "\nCombining the key and certificate and creating a keystore, so that Jetty can use it."
openssl pkcs12 -inkey ${prefix}.server.key -in ${prefix}.server.crt -export -out ${prefix}.pkcs12
keytool -importkeystore -srckeystore ${prefix}.pkcs12 -srcstoretype PKCS12 -destkeystore ${prefix}.keystore

cat <<EOF

Take a look at: http://www.akadia.com/services/ssh_test_certificate.html if you need any more info.

If you have come till here, these are the files you'll probably care about:
Keystore for Jetty : ${prefix}.keystore
SSL Private Key    : ${prefix}.server.key
SSL Certificate    : ${prefix}.server.crt
EOF
