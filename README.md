The OpenAMEE Platform
=====================

Running the platform locally
----------------------------

  1. Create a maven profile in your settings.xml
```xml
<!-- AMEE -->
<profile>
  <id>amee-local</id>
  <properties>
      <db.username>amee</db.username>
      <db.password>amee</db.password>
      <db.url>mysql://localhost:3306/amee?autoReconnect=true&amp;autoReconnectForPools=true&amp;useCompression=false</db.url>
      <rabbitmq.address>localhost</rabbitmq.address>
      <rabbitmq.port>5672</rabbitmq.port>
      <rabbitmq.vhost>/</rabbitmq.vhost>
      <rabbitmq.username>guest</rabbitmq.username>
      <rabbitmq.password>guest</rabbitmq.password>
  </properties>
</profile>
```
  2. Generate a secret key (amee.key) using ```com.amee.base.crypto.BaseCrypto#main``` and place it in ```amee-platform-core/src/main/config```.
  3. Create a salt file (amee.salt) with sixteeen characters in it, eg: "FOOBARBAZQUUX123" and place it in ```amee-platform-core/src/main/config```.
  4. Add a user to the database. Use ```com.amee.base.utils.PasswordEncoder``` to generate the password.
  5. Start the platform:
```sh
mvn exec:java -P amee-local
```

Creating the index
------------------

TODO

Packaging the platform
----------------------

From the amee-platform-core directory:
```sh
mvn clean package -P tar
```