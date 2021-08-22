# TLS Client-Server Example in Java

This is an example on how to build a client AND a server using pure java. The server can be started in plain or TLS mode, the client is also included. The showcase was used to test how to connect via TLS using self signed certificates in Java.

The self signed certificates, secure stores and trust stores are included in this example.

You can generate them as follows:


#generate private key without password, using RSA and 2024 bits
openssl genpkey -out privatekey.pem -algorithm RSA -pkeyopt rsa_keygen_bits:2048

#generate private key WITH password, using RSA and 2024 bits (not used lately)
openssl genpkey -out privatekey.pem -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -aes-128-cbc

#generate CSR (not used lately)
openssl req -new -key privatekey.pem -out csr.pem

#generate CSR with config file
openssl req -new -config openssl.cfg -key privatekey.pem -out csr.pem

#generate self signed certificate
openssl x509 -req -days 365 -in csr.pem -signkey privatekey.pem -out certificate.pem

#show generated certificate
openssl x509 -text -in certificate.pem -noout

#convert to pfx / pkcs#12 format
openssl pkcs12 -export -name "localhost" -out certificate.p12 -inkey privatekey.pem -in certificate.pem -certfile ca.pem
