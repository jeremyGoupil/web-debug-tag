#README

Tag dumping attributes of session, requests and application into console like firebug.

## INSTALL

 
### MAVEN            

'''

- Add dependency in your pom.xml : 
   <dependency>
      <groupId>fr.figarocms</group>
      <artifactId>web-debug-tag</artifactId>
      <version>0.0.2</version>
   </dependency>

'''

### FREESTYLE OR OTHER :

'''

- you can download it here =>  https://oss.sonatype.org/ find web-debug-tag.

'''

### FILTER PARAM            

'''

- Add in your web.xml, min 1 filter.
   <context-param>
      <param-name>webdebug.excludes</param-name>
      <param-value>org.,com.</param-value>
   </context-param> 
  

''' 

### TOMCAT OR OTHER

'''  

- Add -Ddebug.jsp=true in "APPS-OPTS" of your setenv.sh.

'''

### IDE 

'''

- Add -Ddebug.jsp=true in VM parameters of your Runner.

'''

### USAGE
          
'''
- Add in jsp file you want to debug : 
      <%@ taglib prefix="debug" uri="https://github.com/figarocms/web-debug-tag"%>
      <debug:debugModel/>  

- Launch your web application and open firebug to see an Object with :
  - request
  - session
  - application

'''              
    

