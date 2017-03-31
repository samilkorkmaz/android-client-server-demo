#Build and run server
clear all
mkdir dist
javac -classpath ./gson-2.8.0.jar src/server/*.java
cd src
jar cvfm ../dist/serverHeadless.jar ../manifest.mf server/*.class
#java -cp serverHeadless.jar:lib/* server.ServerTest
jar tf ../dist/serverHeadless.jar
rm ./server/*.class

java -jar ../dist/serverHeadless.jar
#Disable the above line and enable the following lines if you want the server to keep on running after you have closed your terminal.
#nohup java -jar ../dist/serverHeadless.jar &
#disown
