# GameBoost Frontend - Documentation des Tests

Ce document décrit l'architecture et l'exécution des tests automatisés du frontend Android de l'application GameBoost. 

L'approche de test est divisée en deux catégories principales selon la pyramide des tests Google : les **tests unitaires locaux (JVM)** et les **tests d'interface (Instrumentés)**.

---

## 1. Executer les tests

### Tests Unitaires Locaux (Logique Metier pure)
Ces tests s'exécutent très rapidement sur votre machine (sans avoir besoin d'un téléphone ou d'un émulateur). Ils valident le comportement des ViewModels, de vos Utils et de votre Base de données locale (Repository).

**Commande a lancer dans le terminal :**
```bash
./gradlew test
```

### Tests d'Interface / Jetpack Compose (Instrumentes)
Ces tests nécessitent un émulateur allumé ou un téléphone branché en mode débogage. Ils permettent de simuler des clics utilisateurs sur votre application (ex: CoachListScreen).

**Commande a lancer dans le terminal :**
```bash
./gradlew connectedAndroidTest
```

---

## 2. Structure des dossiers de tests

```text
GameBoost-frontend/
├── src/
│   ├── main/             <-- Votre code source applicatif (Jetpack Compose, MVVM)
│   │
│   ├── test/             <-- TESTS UNITAIRES (Mockito, JUnit4, Coroutines)
│   │   ├── data/             - MockRepositoryTest.kt
│   │   ├── ui/               - Auth, Admin, Player, Coach, Profile ViewModelTest.kt
│   │   └── util/             - UtilsTest.kt
│   │
│   └── androidTest/      <-- TESTS D'INTERFACE (Jetpack Compose Rule)
│       └── ui/player/        - CoachListScreenTest.kt
```

---

## 3. Technologies utilisees

- **JUnit 4** : Le cadriciel standard pour exécuter et vérifier (assertions `assertTrue`, `assertEquals`) le déroulement d'un test.
- **Mockito & Mockito-Kotlin** : Librairie pour "mocker" (simuler) les appels réseaux ou des classes complexes sans avoir besoin de véritables serveurs.
- **Kotlinx-Coroutines-Test** : Essentiel pour gérer et avancer le "temps" de vos fonctions asynchrones (les `viewModelScope.launch { ... }`) avec `runTest` et `advanceUntilIdle()`.
- **Jetpack Compose UI Testing** : Fourni nativement par Android (`createComposeRule()`) pour manipuler les interactions visuelles sur Jetpack Compose.
