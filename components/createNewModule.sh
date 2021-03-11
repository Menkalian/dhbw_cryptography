#!/usr/bin/env bash
#
if [ -z ${1+x} ];
  then echo "Please enter the name of the component as the first parameter";
  else echo "Creating component '$1'"
  component=$1
  class="$(tr '[:lower:]' '[:upper:]' <<< ${component:0:1})${component:1}"
  mkdir $component
  cd $component || exit 1
  gradle init --type basic --dsl groovy --project-name $component
  gradle wrapper
  echo "plugins {
    id 'java-library'
}

group 'dhbw'
version null

repositories {
    mavenCentral()
}

task buildJar(type: Jar) {
    project.archivesBaseName = \"$1\"

    manifest {
        attributes[\"Main-Class\"] = \"$class\"
    }

    from { configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) } }
    with jar
}" > ./build.gradle
mkdir -p src/main/java
cd src/main/java || exit 1

echo "public interface I$class {
  //TODO: Fill the contents
}" > I${class}.java

echo "public class $class {
    // static instance
    private static $class instance = new $class();
    // port
    public Port port;

    private $class() {
        port = new Port();
    }

    // TODO

    public class Port implements I$class {
        // TODO
    }
}" > ${class}.java

cd ../../..
cd gradle/wrapper || exit 1
echo "distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-6.8.2-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
" > gradle-wrapper.properties

cd ../.. && ./gradlew buildJar
fi
