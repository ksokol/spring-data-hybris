# Spring Data Hybris #

The primary goal of the [Spring Data](http://projects.spring.io/spring-data) project is to make it easier to build Spring-powered applications that use data access technologies. This module deals with enhanced support for Hybris Platform based data access layers.

## Features ##

* Implementation of CRUD methods for Hybris Servicelayer Models
<del>* Dynamic query generation from query method names</del>
* Implementation servicelayer model classes providing basic properties
<del>* Possibility to integrate custom repository code</del>
* Easy Spring integration with custom namespace

## Getting Help ##

This README as well as the [reference documentation](http://docs.spring.io/spring-data/data-jpa/docs/current/reference/html) are the best places to start learning about Spring Data JPA.  There are also [two sample applications](https://github.com/spring-projects/spring-data-examples) available to look at.

The main project [website](http://projects.spring.io/spring-data) contains links to basic project information such as source code, JavaDocs, Issue tracking, etc.

For more detailed questions, use [stackoverflow](http://stackoverflow.com/questions/tagged/spring-data-jpa) or the [forum](forum.spring.io/forum/jpa-orm). If you are new to Spring as well as to Spring Data, look for information about [Spring projects](http://projects.spring.io). You should also have a look at our new Spring Guide
[Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/) that leverages the simplified configuration provided by [Spring Boot](http://projects.spring.io/spring-boot/).


## Quick Start ##

Setup basic Hybris Platform as well as Spring Data Hybris repository support.

The simple Spring Data Hybris configuration with Java-Config looks like this:
```java
@Configuration
@EnableHybrisRepositories("com.acme.repositories")
class AppConfig {

}
```

Create an item model:

```xml
<itemtype code="MyItem" jaloclass="com.acme.jalo.MyItem">
	<attributes>
		<attribute qualifier="name" type="java.lang.String">
			<persistence type="property" />
		</attribute>
	</attributes>
</itemtype>
```

Create a repository interface in `com.acme.repositories`:

```java
public interface MyItemRepository extends CrudRepository<MyItemModel, PK> {

  @Query("select {PK},{name} from {MyItem} where {name} = ?....")
  List<MyItemModel> findByName(String name);
}
```