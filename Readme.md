# Cahier des Charges Détaillé : Système de Gestion de Bibliothèque 
## Architecture Microservices avec API Gateway

## 1. Objectif du Projet

Développer une application de gestion de bibliothèque basée sur une architecture microservices comprenant :
- Deux services métier indépendants
- Une API Gateway centralisée
- Deux bases de données distinctes
- Un service de découverte

Le but est d'apprendre et de mettre en pratique les principes fondamentaux d'une architecture microservices.

## 2. Architecture Globale

L'architecture sera composée des éléments suivants :

1. **API Gateway** : Point d'entrée unique pour toutes les requêtes
2. **Service Registry** : Service de découverte pour l'enregistrement des microservices
3. **Service de Gestion des Livres** : Gestion du catalogue de livres
4. **Service de Gestion des Utilisateurs et Emprunts** : Gestion des utilisateurs et des emprunts
5. **Base de données PostgreSQL** : Stockage des données de livres
6. **Base de données MongoDB** : Stockage des données utilisateurs et emprunts

## 3. Détail des Composants

### 3.1 API Gateway (Spring Cloud Gateway)

**Rôle** :
- Point d'entrée unique pour les clients
- Routage des requêtes vers les microservices appropriés
- Équilibrage de charge
- Filtrage des requêtes

**Technologies** :
- Spring Boot 3.x
- Spring Cloud Gateway
- Spring Cloud LoadBalancer

**Configuration** :
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: book-service
          uri: lb://BOOK-SERVICE
          predicates:
            - Path=/api/books/**
        - id: user-service
          uri: lb://USER-SERVICE
          predicates:
            - Path=/api/users/**, /api/loans/**
```

### 3.2 Service Registry (Eureka)

**Rôle** :
- Enregistrement et découverte des microservices
- Surveillance de l'état des services

**Technologies** :
- Spring Boot 3.x
- Spring Cloud Netflix Eureka Server

**Configuration** :
```yaml
server:
  port: 8761

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

### 3.3 Service de Gestion des Livres

**Fonctionnalités** :
- CRUD des livres
- Recherche de livres par différents critères
- Gestion de la disponibilité des livres

**Technologies** :
- Spring Boot 3.x
- Spring Data JPA
- PostgreSQL
- Spring Cloud Netflix Eureka Client

**Modèle de données** :
- Livre (id, titre, auteur, genre, isbn, disponibilité, dateAjout)

**API REST** :
- `GET /api/books` - Liste tous les livres
- `GET /api/books/{id}` - Détails d'un livre
- `POST /api/books` - Ajouter un nouveau livre
- `PUT /api/books/{id}` - Mettre à jour un livre
- `DELETE /api/books/{id}` - Supprimer un livre
- `GET /api/books/search?title=X&author=Y&genre=Z` - Rechercher des livres
- `PUT /api/books/{id}/availability` - Mettre à jour la disponibilité d'un livre

### 3.4 Service de Gestion des Utilisateurs et Emprunts

**Fonctionnalités** :
- CRUD des utilisateurs
- Gestion des emprunts et retours
- Historique des emprunts

**Technologies** :
- Spring Boot 3.x
- Spring Data MongoDB
- MongoDB
- Spring Cloud Netflix Eureka Client
- Spring Cloud OpenFeign (pour la communication avec le service des livres)

**Modèle de données** :
- Utilisateur (id, nom, prénom, email, dateInscription)
- Emprunt (id, userId, bookId, dateEmprunt, dateRetourPrévue, dateRetourEffective, statut)

**API REST** :
- `GET /api/users` - Liste tous les utilisateurs
- `GET /api/users/{id}` - Détails d'un utilisateur
- `POST /api/users` - Créer un utilisateur
- `PUT /api/users/{id}` - Mettre à jour un utilisateur
- `DELETE /api/users/{id}` - Supprimer un utilisateur
- `POST /api/loans` - Créer un emprunt
- `PUT /api/loans/{id}/return` - Retourner un livre
- `GET /api/users/{id}/loans` - Historique des emprunts d'un utilisateur

## 4. Communication Inter-services

### 4.1 Synchrone (REST avec OpenFeign)

Le service d'emprunts communiquera avec le service de livres pour :
- Vérifier la disponibilité d'un livre avant emprunt
- Mettre à jour la disponibilité après emprunt/retour

**Configuration OpenFeign** :
```java
@FeignClient(name = "BOOK-SERVICE")
public interface BookServiceClient {
    @PutMapping("/api/books/{id}/availability")
    void updateBookAvailability(@PathVariable Long id, @RequestBody AvailabilityRequest request);
    
    @GetMapping("/api/books/{id}")
    BookDto getBookById(@PathVariable Long id);
}
```

## 5. Infrastructure et Déploiement

### 5.1 Docker

Chaque composant sera conteneurisé avec Docker :

**Dockerfile exemple (pour un service)** :
```dockerfile
FROM openjdk:17-slim
WORKDIR /app
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 5.2 Docker Compose

**docker-compose.yml** pour orchestrer tous les services :
```yaml
version: '3.8'
services:
  eureka-server:
    build: ./eureka-server
    ports:
      - "8761:8761"
    networks:
      - library-network

  api-gateway:
    build: ./api-gateway
    ports:
      - "8080:8080"
    depends_on:
      - eureka-server
    environment:
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/
    networks:
      - library-network

  book-service:
    build: ./book-service
    depends_on:
      - eureka-server
      - postgres
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/bookdb
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=password
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/
    networks:
      - library-network

  user-service:
    build: ./user-service
    depends_on:
      - eureka-server
      - mongodb
    environment:
      - SPRING_DATA_MONGODB_URI=mongodb://mongodb:27017/userdb
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/
    networks:
      - library-network

  postgres:
    image: postgres:14
    environment:
      - POSTGRES_DB=bookdb
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=password
    volumes:
      - postgres-data:/var/lib/postgresql/data
    networks:
      - library-network

  mongodb:
    image: mongo:5
    volumes:
      - mongo-data:/data/db
    networks:
      - library-network

networks:
  library-network:

volumes:
  postgres-data:
  mongo-data:
```

## 6. Plan d'Implémentation

### 6.1 Étapes de Développement

1. **Mise en place de l'infrastructure**
   - Création du projet Spring Boot pour le Service Registry (Eureka)
   - Création du projet pour l'API Gateway
   - Configuration des bases de données (PostgreSQL et MongoDB)

2. **Développement du service de gestion des livres**
   - Création des entités et repositories
   - Développement des services métier
   - Implémentation des API REST
   - Tests unitaires et d'intégration

3. **Développement du service de gestion des utilisateurs et emprunts**
   - Création des entités et repositories
   - Développement des services métier
   - Implémentation des API REST
   - Configuration de Feign pour la communication avec le service de livres
   - Tests unitaires et d'intégration

4. **Intégration et tests**
   - Configuration de Docker et Docker Compose
   - Tests de bout en bout
   - Tests de charge

### 6.2 Structure des Projets

**Structure type pour chaque microservice** :
```
service-name/
├── src/
│   ├── main/
│   │   ├── java/com/library/servicename/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── model/
│   │   │   ├── dto/
│   │   │   ├── config/
│   │   │   └── ServiceNameApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── bootstrap.yml (si nécessaire)
│   └── test/
│       └── java/com/library/servicename/
│           ├── controller/
│           ├── service/
│           └── repository/
├── Dockerfile
└── pom.xml
```

## 7. Documentation et Livrables

### 7.1 Livrables Attendus

1. Code source complet des microservices
2. Fichiers Docker et Docker Compose
3. Documentation des API (Swagger/OpenAPI)
4. Guide d'installation et d'utilisation
5. Documentation technique décrivant l'architecture

### 7.2 Documentation des API

Chaque service sera documenté avec Swagger/OpenAPI :

**Configuration exemple** :
```java
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Library Service API")
                        .version("1.0")
                        .description("API Documentation for Library Microservice"));
    }
}
```

## 8. Validation et Tests

### 8.1 Scénarios de Test

1. **Enregistrement d'un nouveau livre**
   - Créer un livre via l'API Gateway
   - Vérifier sa présence dans la base PostgreSQL

2. **Cycle d'emprunt complet**
   - Créer un utilisateur
   - Emprunter un livre (vérifier la mise à jour de disponibilité)
   - Retourner le livre (vérifier la mise à jour de disponibilité)
   - Consulter l'historique des emprunts

3. **Tests de résilience**
   - Arrêter un service et vérifier le comportement du système
   - Redémarrer le service et vérifier la reprise
