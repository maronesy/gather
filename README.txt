GATHER

INTRODUCTION
---------------
Gather is a Java based web application that allows both individual hobbyists, hobby groups, or general interest groups to display and filter their specific interests in the application based on location and time. In this way, people can easily meet new friends with similar interests, especially when they are new to an area. Established hobby and interest groups can also attract new friends to join their group.

CONTRIBUTORS
---------------
Hiren Bhavsar - hbhavsa2
Max Gabreski - gabresk2
Benson Ma - bjma2
Souhayl Maronesy - marones2
Heeho Park - hdpark2
Kai Song - kaisong2

INSTALLATION
---------------
Our application build process relies on Apache Maven. Please download and install Maven before attempting to build and deploy our app.
Our application is a web app developed on the Spring Boot platform, so it includes a Tomcat server and can build and deploy itself through Maven. Simply use 'mvn spring-boot:run' from the root of our app to launch our app in this mode.
Additionally, if you wish to compile a WAR package that can then be deployed to any other Tomcat instance, simply run 'mvn package' from the root of our app.

DOCUMENTATION
---------------
To easily build our Javadoc documentation, please ensure you have Apache Ant installed.
Please run 'ant -buildfile javadoc.xml' from the root of our app.

For additional documentation, please see our report (project_documentation.pdf) located in the root of our app.