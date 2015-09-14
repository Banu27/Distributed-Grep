This project implements a distributed grep for parallel string pattern search 
over text files present in multiple networked nodes. This document explains the steps for building and running the project 

Dependencies: 
The project is setup using maven, therefore it is necessary to have some recent version of maven installed in your system for building the code. You also need a JDK for compiling and running the application. This project is tested on "1.7.0 85". 

How to Compile:
To build the code execute the below command
$ cd distGrep
$ mvn package -DskipTests=true
This will create a jar file which can be deployed and run.

How to Run:
To run the jar, copy it to the home directory in all the nodes. In our experimental setup we did
$ for NUM in `seq 1 1 7`; do scp distGrepFinal.jar fa15-cs425-g01-0$NUM:~; done

To run the executable run the below commnad
$ java -cp ~/distGrepFinal.jar edu.uiuc.cs425.App 0 $HOME/test
Arg 1: master node ID. Should always be 0
Arg 2: directory to search in

Run tests:
To run the junit test cases, copy the jars as specified in the previous section and run the below command.

$ mvn test

======================================================================================


