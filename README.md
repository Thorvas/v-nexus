
# V-Nexus (in development)

Spring Boot project written in Java (with usage of Spring Security, Spring Data JPA, Lombok, and Spring HATEOAS). It allows users represented as Volunteers to create their own volunteer projects and find other volunteers to participate. V-Nexus is secured by JWT Security method to prevent unauthorized access to resources.




## Installation

Since project is still in development, you can download files directly by cloning the repository via HTTPS.
```
https://github.com/Thorvas/v-nexus.git
```
and then run project through Maven by command:
```
mvn spring-boot:run
```
## API Instruction

Project consists of many classes returned with HATEOAS links in JSON format. To access API resources, we need to include JWT token in each request. JWT tokens are generated during login or registration process.

#### Register and Login into system
An endpoint that allows us to register and login within V-Nexus system. It accepts JSON object with two properties - ```username``` and ```password```. Returned value is JWT token which allows user to authenticate himself within system. Token expiration time is currently set to be 1 day.

Path to register or login user within system:

```
  POST /api/v1/auth/register
  POST /api/v1/auth/login
```
An example of passed register/login data:
```json
{
    "username": "user",
    "password": "password"
}
```

An example of returned Token:
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiaWF0IjoxNjkwNTYxMzEzLCJleHAiOjE2OTA2NDc3MTN9.MJgmwd44kUVSuiysx0wDXJAWbDXHwaDihSBpC72v3OY"
}
```
Such token should be later included in ```Authorization``` header in format ```Bearer (JWT token value)``` to gain access to protected resources.

#### Retrieve all volunteers
An endpoint which allows us to retrieve list of all volunteers that are stored in database.
```
  GET /api/v1/volunteers
```

Example response:

```json
{
    "_embedded": {
        "volunteers": [
            {
                "id": 1,
                "name": "John",
                "surname": "Doe",
                "dateOfBirth": "2012-12-25",
                "contact": "+48 788512482",
                "skills": [
                    "Photography"
                ],
                "reputation": 10,
                "interests": [
                    "Reading books"
                ],
                "_links": {
                    "participated-projects": {
                        "href": "http://localhost:8080/api/v1/volunteers/1/projects"
                    },
                    "owned-projects": {
                        "href": "http://localhost:8080/api/v1/volunteers/1/projects/owned"
                    },
                    "self": {
                        "href": "http://localhost:8080/api/v1/volunteers/1"
                    },
                    "root": {
                        "href": "http://localhost:8080/api/v1/volunteers"
                    }
                }
            }
        ]
    },
    "_links": {
        "self": {
            "href": "http://localhost:8080/api/v1/volunteers"
        }
    }
}
```

This example method allows us to retrieve list of volunteers. HATEOAS links guide us through API endpoints, making navigation through resources more intuitive.

More interesting endpoints:

```
GET /api/v1/projects
```

Retrieves list of all projects stored in database.

Example response:
```json
{
    "_embedded": {
        "projects": [
            {
                "id": 1,
                "projectName": "Allow forests to breathe again",
                "projectDescription": "We are searching for volunteers to clean forests in our town. Join us now!",
                "projectDate": "2023-12-25",
                "projectLocation": "Wroclaw",
                "projectStatus": false,
                "tasks": [
                    "Cleaning forests"
                ],
                "_links": {
                    "participating-volunteers": {
                        "href": "http://localhost:8080/api/v1/projects/1/volunteers"
                    },
                    "categories": {
                        "href": "http://localhost:8080/api/v1/projects/1/categories"
                    },
                    "opinions": {
                        "href": "http://localhost:8080/api/v1/projects/1/opinions"
                    },
                    "project-owner": {
                        "href": "http://localhost:8080/api/v1/volunteers/1"
                    },
                    "self": {
                        "href": "http://localhost:8080/api/v1/projects/1/volunteers"
                    },
                    "root": {
                        "href": "http://localhost:8080/api/v1/projects"
                    }
                }
            },
            {
                "id": 2,
                "projectName": "Shelter our little friends",
                "projectDescription": "We organize places for abandoned dogs and cats so they could rest.",
                "projectDate": "2023-12-25",
                "projectLocation": "Wroclaw",
                "projectStatus": true,
                "tasks": [
                    "Looking for safe shelter for homeless animals",
                    "Gathering donations for food"
                ],
                "_links": {
                    "participating-volunteers": {
                        "href": "http://localhost:8080/api/v1/projects/2/volunteers"
                    },
                    "categories": {
                        "href": "http://localhost:8080/api/v1/projects/2/categories"
                    },
                    "opinions": {
                        "href": "http://localhost:8080/api/v1/projects/2/opinions"
                    },
                    "project-owner": {
                        "href": "http://localhost:8080/api/v1/volunteers/1"
                    },
                    "self": {
                        "href": "http://localhost:8080/api/v1/projects/2/volunteers"
                    },
                    "root": {
                        "href": "http://localhost:8080/api/v1/projects"
                    }
                }
            }
        ]
    },
    "_links": {
        "self": {
            "href": "http://localhost:8080/api/v1/projects"
        }
    }
}
```

Link for full documentation will be available soon.



## Authors

- [@Thorvas](https://www.github.com/Thorvas)

