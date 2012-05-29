#!/bin/bash

if [ -z "$1" ]; then
    echo "Usage: $0 prefix"
    exit 1
fi

prefix=$1

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
keytool -importkeystore -srckeystore ${prefix}.pkcs12 -srcstoretype PKCS12 -destkeystore ${prefix}_server.keystore

echo -e "\n === === === Almost done. Please enter a DIFFERENT password from the one you entered earlier. This is for clients. === === ==="
export CLASSPATH=bcprov-jdk15on-146.jar
keytool -import -v -trustcacerts -alias ${prefix} -file <(openssl x509 -in ${prefix}.server.crt) -keystore ${prefix}_client.keystore -storetype BKS -provider org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath ./bcprov-jdk15on-146.jar

cat <<EOF

Take a look at: http://www.akadia.com/services/ssh_test_certificate.html if you need any more info.

If you have come till here, these are the files you'll probably care about:
Keystore for Jetty/Tomcat : ${prefix}_server.keystore
Keystore for clients      : ${prefix}_client.keystore
SSL Private Key           : ${prefix}.server.key
SSL Certificate           : ${prefix}.server.crt
EOF

