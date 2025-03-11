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
