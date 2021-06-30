# parent-pom project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
mvn quarkus:dev
```

## Packaging and running the application

The application can be packaged using `mvn package`.
It produces the `*-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: `mvn package -Pnative`.

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your native executable with: `./target/*-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image.

## Creating the Certificates
see: https://blog.devolutions.net/2020/07/tutorial-how-to-generate-secure-self-signed-server-and-client-certificates-with-openssl
* Create CA Cert
* Create Cleint Cert
* Create Server Cert
* Create PK12 Certs from Server and Client
```
openssl pkcs12 -export -name servercert -in server.crt -inkey server.key -out myp12keystore.p12
openssl pkcs12 -export -name clientcert -in client1.crt -inkey client1.key -out client1.p12
```
* Create Keystore and Trustsore
```
keytool -importkeystore -destkeystore mykeystore.jks -srckeystore myp12keystore.p12 -srcstoretype pkcs12 -alias servercert -storepasswd changeit
keytool -import -file ca.crt -alias ca -keystore mytruststore.jks -storepass test1234
```

* Configure application.properties
* Start the App
* Test with Curl
```
curl -v -k --cert-type P12 --cert client1.p12:test1234 https://localhost:8443/hello
```