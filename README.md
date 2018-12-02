# ares-service-mysql
Spring Data Rest with JPA connecting to MySQL

## ToDo
* encrypt/decrypt db credentials with Spring Config
* implement Vault

----

### schema.sql
```sql
DROP TABLE IF EXISTS HERO;	
	CREATE TABLE hero (
	id INTEGER NOT NULL AUTO_INCREMENT,
	first_name VARCHAR(32),
	last_name VARCHAR(32),
	code_name VARCHAR(32),
	email VARCHAR(32),
	team VARCHAR(32),
	PRIMARY KEY (id)
);
```

### data.sql
```sql
insert into hero (first_name, last_name, code_name, email, team) values 
('Tony', 'Stark', 'Ironman', 'ironman@avengers.com', 'Avengers')
('Steve', 'Rogers', 'Captain America', 'cap@avengers.com', 'Avengers');
```
> rename these files so they don't execute on startup or set the property
spring.datasource.initialization-mode=none

### bootstrap.yml
```yaml
server:
  port: 8081

spring:
  application:
    name: ares-service-mysql
  cloud:
    config:
      uri: http://localhost:8900
```

### ares-service-mysql.yml
```yaml
# create, update, create-drop, validate, none
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/ares_service_mysql
    username: newuser
    # password: newpass
    password: '{cipher}AQBMJGTaOOQ/lPcJeVX1HF74RlILbWENzKD/kMoIjhFT9pYz/EoAhw1Vv3uJ2Jgy61Xl9hEN4WTD5SXEfS1ExAzI+IvNB9x5PGCI0a0dO+xXs7Al8kreoiYOCZm/1wPrCYotY0z/Jlp8AIdlRcSX2+0hTP8s+EkqsQLQvrgN6Z62Rni+h1KE9oeE+K4qtjIafgsVylwqI09LOpOonpTcWk5T+WndnqeFjuZqbAAlQJ6nQNJJhXvT8C7Zv/bgF2fKBcSWOYbd5Ud1Y+Gp5dO9ZVJIa6n1qa+szbC1uRUdFBF3uXr43nZClAIAK/qEjxcV34wsppfj9e610KKJM5kNFFepkAfnysPg8dAIapZqQTzpvwQiqIZGTfRyS62wP+lq1hc='
    # initialization-mode: always
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL5Dialect
    # hibernate:
    #   ddl-auto: update
```

### Create the Entity object
```java
@Entity	
@Data
public class Hero {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
  private String firstName;
	private String lastName;
	private String codeName;
	private String email;
	private String team;
	
  public Hero() {}
	
  public Hero(String firstName, String lastName, String codeName, String email, String team) {
	  this.firstName = firstName;
	  this.lastName = lastName;
	  this.codeName = codeName;
	  this.email = email;
	  this.team = team;
	}
}
```

### Create a JPA Repository that extends CrudRepository class
```java
@RepositoryRestResource
public interface HeroRepository extends CrudRepository<Hero, Long> {

    List<Hero> findByFirstName(@Param("firstname") String firstName);
    List<Hero> findByLastName(@Param("lastname") String lastName);
    List<Hero> findByCodeName(@Param("codename") String codeName);
    List<Hero> findByTeam(@Param("team") String team);

}
```

### Connfigure the Controller methods to use the Repository for CRUD actions
```java
@RestController	
@Slf4j
@RequestMapping(path="/admin")
public class HeroController {
	@Autowired
	private HeroRepository heroRepository;

  @RequestMapping (value = "/add", method = RequestMethod.POST)
	public ResponseEntity<Hero> createHero (@RequestBody Hero hero) {
	  log.info("POST hero");
	  heroRepository.save(hero);
	  return new ResponseEntity<>(hero, HttpStatus.OK);
	}
  
	@RequestMapping (value = "/addList", method = RequestMethod.POST)
	public ResponseEntity<List<Hero>> createHeroes (@RequestBody List<Hero> heroes) {
	  log.info("POST list of heroes");
	  heroRepository.saveAll(heroes);
	  return new ResponseEntity<>(heroes, HttpStatus.OK);
	}
  
	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	public ResponseEntity<Hero> updateUser(@RequestBody Hero hero) {
	  log.info("PUT hero");
	  heroRepository.save(hero);
	  return new ResponseEntity<>(hero, HttpStatus.OK);
	}
  
	@RequestMapping (value = "/updateList", method = RequestMethod.PUT)
	public ResponseEntity<List<Hero>> updateHeroes (@RequestBody List<Hero> heroes) {
	  log.info("PUT list of heroes");
	  heroRepository.saveAll(heroes);
	  return new ResponseEntity<>(heroes, HttpStatus.OK);
	}
  
	@RequestMapping (value = "/delete/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Optional<Hero>> deleteHero (@PathVariable("id") Long id) {
	  log.info("DELETE hero");
	  Optional<Hero> deletedHero = heroRepository.findById(id);
	  heroRepository.deleteById(id);
	  return new ResponseEntity<>(deletedHero, HttpStatus.OK);
	}
  
	@RequestMapping (value = "/deleteAll", method = RequestMethod.DELETE)
	public ResponseEntity<Iterable<Hero>> deleteAll () {
	  log.info("DELETE hero");
	  Iterable<Hero> deletedHeroes = heroRepository.findAll();
	  heroRepository.deleteAll();
	  return new ResponseEntity<>(deletedHeroes, HttpStatus.OK);
	}
}
```

### Dockerfile
```dockerfile
FROM openjdk:8-jdk-alpine
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
EXPOSE 8900
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
```

### Jenkinsfile
```groovy
def mvnTool
def prjName = "ares-service-mysql"
def imageTag = "latest"

pipeline {
    agent { label 'maven' }
    options {
        buildDiscarder(logRotator(numToKeepStr: '2'))
        disableConcurrentBuilds()
    }
    stages {
        stage('Build && Test') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    script {
                        mvnTool = tool 'Maven'
                        sh "${mvnTool}/bin/mvn -B clean verify sonar:sonar -Prun-its,coverage"
                    }
                }
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    jacoco(execPattern: 'target/jacoco.exec')
                }
            }
        }
        stage('Quality Gate') {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        stage('Release && Publish Artifact') {

        }
        stage('Create Image') {
            steps {
                sh "docker build --build-arg JAR_FILE=target/${prjName}-${releaseVersion}.jar -t ${prjName}:${releaseVersion}"
            }
        }
        stage('Publish Image') {
            steps {
                withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'JENKINS_ID', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
                    sh """
                        docker login -u ${USERNAME} -p ${PASSWORD} dockerRepoUrl
                        docker push ...
                    """
                }
            }
        }
    }
}
```
