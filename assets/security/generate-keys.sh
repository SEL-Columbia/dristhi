#!/bin/bash

if [ -z "$1" ]; then
    echo "Usage: $0 prefix"
    exit 1
fi

prefix=$1

/bin/rm -f ${prefix}* certificates/${prefix}*

echo -e "Generating the keyfile. You will need to enter a password here."
openssl genrsa -des3 -out ${prefix}.server.key 1024

echo -e "\nGenerating a certificate request file. You'll be asked to enter the same password."
openssl req -new -key ${prefix}.server.key -out ${prefix}.server.csr -config openssl.conf

echo -e "\nRemoving the password from the keyfile. Enter the password ... again."
cp ${prefix}.server.key ${prefix}.server.key.original
openssl rsa -in ${prefix}.server.key.original -out ${prefix}.server.key

echo -e "\nCreating a certificate out of the key and the certificate request."
openssl x509 -req -days 1095 -in ${prefix}.server.csr -signkey ${prefix}.server.key -out certificates/${prefix}.server.crt

echo -e "\nCombining the key and certificate and creating a keystore, so that Jetty can use it."
openssl pkcs12 -inkey ${prefix}.server.key -in certificates/${prefix}.server.crt -export -out ${prefix}.pkcs12
keytool -importkeystore -srckeystore ${prefix}.pkcs12 -srcstoretype PKCS12 -destkeystore ${prefix}_server.keystore

echo -e "\n === === === Almost done. Please enter a DIFFERENT password from the one you entered earlier. This is for clients. === === ==="
read -p "Password for client keystore: " client_password

export CLASSPATH=bcprov-jdk15on-146.jar
keytool -import -v -trustcacerts -alias ${prefix} -file <(openssl x509 -in certificates/${prefix}.server.crt) -keystore ${prefix}_client.keystore -storetype BKS -provider org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath ./bcprov-jdk15on-146.jar -storepass "${client_password}"
keytool -import -v -trustcacerts -alias commcarehq -file <(openssl x509 -in certificates/commcarehq.crt) -keystore ${prefix}_client.keystore -storetype BKS -provider org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath ./bcprov-jdk15on-146.jar -storepass "${client_password}"
keytool -import -v -trustcacerts -alias ThawtePremiumServerCA -file <(openssl x509 -in certificates/ThawtePremiumServerCA) -keystore ${prefix}_client.keystore -storetype BKS -provider org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath ./bcprov-jdk15on-146.jar -storepass "${client_password}"
keytool -import -v -trustcacerts -alias ThawteSSLCA -file <(openssl x509 -in certificates/ThawteSSLCA) -keystore ${prefix}_client.keystore -storetype BKS -provider org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath ./bcprov-jdk15on-146.jar -storepass "${client_password}"
keytool -import -v -trustcacerts -alias ThawteServerCA -file <(openssl x509 -in certificates/ThawteServerCA) -keystore ${prefix}_client.keystore -storetype BKS -provider org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath ./bcprov-jdk15on-146.jar -storepass "${client_password}"
keytool -import -v -trustcacerts -alias thawtePrimaryRootCA -file <(openssl x509 -in certificates/thawtePrimaryRootCA) -keystore ${prefix}_client.keystore -storetype BKS -provider org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath ./bcprov-jdk15on-146.jar -storepass "${client_password}"

/bin/cp -f ${prefix}_client.keystore ../../drishti-common/src/main/resources/

cat <<EOF

Take a look at: http://www.akadia.com/services/ssh_test_certificate.html if you need any more info.

If you have come till here, these are the files you'll probably care about:
Keystore for Jetty/Tomcat : ${prefix}_server.keystore
Keystore for clients      : ${prefix}_client.keystore
SSL Private Key           : ${prefix}.server.key
SSL Certificate           : certificates/${prefix}.server.crt

Client key has been copied to: ../../drishti-common/src/main/resources/.
EOF

