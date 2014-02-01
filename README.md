jdsres
======

Java library to read results files from Dymola and OpenModelica.

To use from Maven, add the following to pom.xml:

```
    <repositories>
        <repository>
            <id>jdsres-mvn-repo</id>
            <url>https://raw.github.com/harmanpa/jdsres/mvn-repo/</url>
            <snapshots>
                <enabled>true</enabled>
                <updatePolicy>always</updatePolicy>
            </snapshots>
        </repository>
    </repositories>
```

and

```
    <dependency>
            <groupId>com.github.harmanpa</groupId>
            <artifactId>jdsres</artifactId>
            <version>1.0-SNAPSHOT</version>
    </dependency>
```
