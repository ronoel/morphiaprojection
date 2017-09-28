# Morphia Projection
Annotation to project mongo entity

- Apache License, Version 2.0
- Dependency: Morphia 1.3.2

Entity file:
```java
public class Foo {

    @ProjectView(name = {"View1","View2"})
    @Id
    private Long id;

    @ProjectView(name = "View1")
    private String name;
    
    private int age;
    ...
}
```

Use the query to project the data to recovery from MongoDB
```java
    // create a query
    ...
    // projectQuery is a static method, the first parameter receive a view defined on the entity
    // the second parameter is the morphia query "org.mongodb.morphia.query.Query"
    ProjectViewExecutor.projectQuery("View1", query); 
    // this query will project only the fields with @ProjectView and name that contains "View1"
    ...
    // execute the query
```
