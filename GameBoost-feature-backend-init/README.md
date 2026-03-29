# GameBoost Backend - Documentation des Tests Unitaires

Ce document résume l'état des tests unitaires du backend GameBoost, garantissant la qualité et la fiabilité du code.

## Résumé de la Couverture

Le backend bénéficie d'une excellente couverture de tests (supérieure à **90% globalement**). Tous les composants principaux sont testés et validés par l'arsenal de tests automatisés.

- **Nombre total de tests :** `108` tests unitaires.
- **Taux de réussite :** `100%` (0 erreur, 0 échec).

### Composants Couverts

**Couverture Complète (ou quasi-complète à 100%)**
- **Contrôleurs :** `AdminController`, `AuthController`, `SessionController` (Test des requêtes HTTP et de leurs réponses).
- **Services Principaux :** `AdminService`, `AuthService`.
- **Mécanismes de Sécurité :** `JwtAuthFilter`, `JwtUtils`, `UserDetailsServiceImpl` (Authentification et filtres par rôle).
- **Gestion Globale :** `GlobalExceptionHandler` (Test de la capture et des messages d'erreurs normés).

**Composants avec quelques instructions manquantes (Très bien couverts mais < 100%)**
- **`CoachService` / `SessionService` :** Très fortement couverts (85% à 90%), avec seulement quelques "branches" ou conditions extrêmes d'erreur spécifiques qui restent non testées.
- **`CoachController` / `MessageService` :** Quasi complètement couverts, il manque moins de 10 instructions spécifiques liées aux exceptions de traitement.

## Comment Lancer les Tests

Pour exécuter tous les tests unitaires et s'assurer que le backend est fonctionnel, utilisez **Maven** dans le dossier racine du projet.

```bash
# Lancer les tests
mvn test

# Lancer les tests en générant le rapport de couverture JaCoCo
mvn test jacoco:report
```

*Le rapport détaillé de couverture de code (généré par JaCoCo) sera consultable dans `target/site/jacoco/index.html` après l'exécution de la dernière commande.*

## Outils Utilisés
- **JUnit 5 (Jupiter) :** Pour l'exécution et la structure des tests unitaires.
- **Mockito :** Pour simuler (mocker) le comportement des dépendances (Repositories, bases de données) et tester chaque service de façon isolée.
- **JaCoCo :** Pour l'analyse et la vérification de la couverture de test.
