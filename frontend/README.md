# 🎮 GameBoost - Frontend Android (Kotlin)

**Plateforme** : Application mobile Android pour GameBoost - Plateforme de coaching gaming.

**État** : ✅ Opérationnel et intégré au backend Java.

---

## 1. Présentation du Projet

**GameBoost Frontend** est une application Android native développée en **Kotlin** qui interagit avec le backend Java Spring Boot. L'application suit une architecture **MVVM** (Model-View-ViewModel) avec injection de dépendances via **Hilt**.

### Features Principales
- ✅ Authentification JWT secure
- ✅ Recherche et filtrage de coachs
- ✅ Gestion complète des sessions
- ✅ Chat en temps réel entre joueur et coach
- ✅ Système de notation/avis
- ✅ Dashboard utilisateur dynamique
- ✅ Candidature coach avec validation admin
- ✅ Suivi des revenus (pour coaches)

---

## 2. Architecture du Projet

```
frontend/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/gameboost/frontend/
│   │   │   │   ├── data/
│   │   │   │   │   ├── api/           # Services Retrofit + Interceptors
│   │   │   │   │   ├── models/        # Data classes (User, Coach, Session, etc)
│   │   │   │   │   └── repository/    # Repositories (couche persistence)
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screens/       # Fragments (Écrans/Vues)
│   │   │   │   │   ├── components/    # Composants réutilisables
│   │   │   │   │   └── viewmodel/     # ViewModels (State Management)
│   │   │   │   ├── utils/             # Utilitaires (JWT, validation, constantes)
│   │   │   │   ├── di/                # Dependency Injection (Hilt)
│   │   │   │   └── GameBoostApplication.kt
│   │   │   └── res/
│   │   │       ├── layout/            # XML layouts des Fragments
│   │   │       ├── navigation/        # Navigation graph
│   │   │       ├── values/            # Strings, colors, styles
│   │   │       └── ...
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts               # Configuration Gradle
├── settings.gradle.kts
├── build.gradle.kts
└── README.md
```

### Architecture N-Tier
```
┌─────────────────────────────────┐
│  UI Layer (Fragments/Screens)   │  ← Affichage utilisateur
├─────────────────────────────────┤
│  ViewModel + LiveData            │  ← Gestion d'état
├─────────────────────────────────┤
│  Repository Pattern              │  ← Abstraction données
├─────────────────────────────────┤
│  API Service (Retrofit)          │  ← Appels REST + JWT
├─────────────────────────────────┤
│  Backend Java Spring Boot        │  ← Serveur distant
└─────────────────────────────────┘
```

---

## 3. Stack Technique

| Composant | Version | Utilisation |
|-----------|---------|-------------|
| **Android SDK** | 34 (API Level) | Target SDK |
| **Kotlin** | 1.9.10 | Langage |
| **Jetpack** | Latest | AndroidX libraries |
| **Retrofit** | 2.10.0 | HTTP client |
| **OkHttp** | 4.11.0 | HTTP interceptors + logging |
| **Hilt** | 2.48 | Dependency Injection |
| **Lifecycle** | 2.6.2 | ViewModel + LiveData |
| **Navigation** | 2.7.6 | Navigation entre fragments |
| **Room** | 2.6.1 | Local database (optionnel) |
| **DataStore** | 1.0.0 | Stockage JWT + préférences |
| **Coroutines** | 1.7.3 | Async programming |
| **Gson** | 2.10.1 | JSON serialization |

---

## 4. Configuration API Backend

### Configuration dans build.gradle.kts
```kotlin
buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8080/\"")
```

**Note** : `10.0.2.2` est l'adresse de la machine host depuis l'émulateur Android.

### En production
Remplacer par l'URL réelle du serveur (ex: `https://api.gameboost.com/`)

---

## 5. Modèles de Données

### Structures Principales
```kotlin
// Authentification
data class User(id: UUID, email: String, username: String, role: Role, ...)
data class AuthResponse(token: String, user: User)

// Coachs
data class CoachProfile(id: Long, gameTitle: String, rank: String, ...)
data class CoachResponse(id: Long, username: String, hourlyRate: BigDecimal, ...)

// Sessions
data class Session(id: Long, player: User, coach: CoachProfile, status: Status, ...)
data class SessionResponse(id: Long, coach: CoachInfo, status: String, ...)

// Messages
data class Message(id: Long, sessionId: Long, content: String, ...)
data class MessageResponse(id: Long, senderName: String, content: String, ...)

// Avis
data class Review(id: Long, rating: Int, comment: String, ...)
data class ReviewRequest(rating: Int, comment: String?)
```

---

## 6. Services API et Repositories

### ApiService (Retrofit Interface)
```kotlin
// Authentification
POST /auth/register
POST /auth/login
GET /users/me
PUT /users/me

// Coachs
GET /coaches
GET /coaches/{id}
POST /coaches/profile
PUT /coaches/me/profile
GET /coaches/me/earnings

// Sessions
POST /sessions/request
GET /sessions
PATCH /sessions/{id}/accept
PATCH /sessions/{id}/complete
PATCH /sessions/{id}/confirm
POST /sessions/{id}/pay
POST /sessions/{id}/review

// Messages
GET /sessions/{id}/messages
POST /sessions/{id}/messages

// Admin
GET /admin/coaches/pending
PATCH /admin/coaches/{id}/approve
GET /admin/dashboard
```

### AuthInterceptor
```kotlin
// Ajoute automatiquement le JWT à chaque request
Authorization: Bearer {jwt_token}
```

### Repositories (Data Access Layer)
```kotlin
class AuthRepository { 
    suspend fun register(...): Result<AuthResponse>
    suspend fun login(...): Result<AuthResponse>
    suspend fun getProfile(): Result<User>
}

class CoachRepository { 
    suspend fun getCoaches(...): Result<List<CoachResponse>>
    suspend fun getCoachById(...): Result<CoachResponse>
}

class SessionRepository { 
    suspend fun createSession(...): Result<Session>
    suspend fun getSessions(...): Result<List<SessionResponse>>
    suspend fun acceptSession(...): Result<Session>
}

// ... etc
```

---

## 7. Gestion d'État (ViewModels)

### Architecture LiveData + ViewModel
```kotlin
class AuthViewModel {
    // État
    val authState: LiveData<AuthState>
    val currentUser: LiveData<User?>
    
    // Actions
    fun login(email: String, password: String)
    fun register(...)
    fun logout()
    
    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        data class Success(val message: String) : AuthState()
        data class Error(val message: String) : AuthState()
    }
}

class CoachViewModel { ... }
class SessionViewModel { ... }
class MessageViewModel { ... }
```

### Pattern Observer dans les Fragments
```kotlin
viewModel.authState.observe(viewLifecycleOwner) { state ->
    when (state) {
        is AuthViewModel.AuthState.Loading -> showLoading()
        is AuthViewModel.AuthState.Success -> navigateToDashboard()
        is AuthViewModel.AuthState.Error -> showError(state.message)
        else -> hideLoading()
    }
}
```

---

## 8. Navigation

### Navigation Graph (nav_graph.xml)
```xml
LoginFragment
  ├─ RegisterFragment
  └─ DashboardFragment
       ├─ CoachListFragment
       ├─ SessionListFragment
       └─ CoachProfileFragment
```

### Navigation Programmatique
```kotlin
findNavController().navigate(
    LoginFragmentDirections.actionLoginToDashboard()
)
```

---

## 9. Authentification & Sécurité

### Flux JWT
```
1. Login/Register → Backend retourne token JWT
2. TokenManager.saveToken(token, role, userId)
   ↓ Stocké dans DataStore (chiffré)
3. Chaque request → AuthInterceptor ajoute Authorization: Bearer
4. Response 401 → Rediriger vers Login
5. Logout → TokenManager.clearToken()
```

### TokenManager
```kotlin
// Stockage sécurisé du JWT
fun saveToken(token: String, role: String, userId: String)
fun getToken(): String?
fun getTokenFlow(): Flow<String?>
fun clearToken()
```

---

## 10. Écrans Principaux

### 1. **LoginFragment**
- Champs: Email, Password
- Actions: Login, Version Inscription
- Validation: Format email, longueur password

### 2. **RegisterFragment**
- Champs: Email, Username, Password, Role (Joueur/Coach)
- Actions: S'inscrire, Retour Login
- Validation: Email unique, password fort, pseudo valide

### 3. **DashboardFragment**
- Affichage: Welcome message, Rôle utilisateur
- Actions: Voir Coachs, Mes Sessions, Déconnexion
- Dynamique selon rôle (Joueur/Coach/Admin)

### 4. **CoachListFragment**
- Recherche: Jeu, Rang
- Affichage: Liste coachs approuvés avec tarifs, notes
- Actions: Cliquer sur coach → détails

### 5. **SessionListFragment**
- Filtrage: Statut (REQUESTED, ACCEPTED, COMPLETED, PAID)
- Affichage: Tableau sessions avec montant, date
- Actions: Accepter (coach), Payer (joueur), Chat

### 6. **CoachProfileFragment**
- Formulaire: Jeu, Rang, Bio, Tarif horaire
- Actions: Soumettre candidature
- Statut: En attente d'approbation admin

---

## 11. Installation & Configuration

### Prérequis
```
- Android Studio 2023.1+
- Android SDK 34
- Kotlin 1.9.10+
- Java 17+
```

### Étapes d'installation

1. **Clone le repository**
```bash
cd frontend/
```

2. **Configurer le serveur backend**
   - Éditer `build.gradle.kts`
   - Remplacer `API_BASE_URL` par l'URL réelle du backend
   ```kotlin
   buildConfigField("String", "API_BASE_URL", "\"http://your-server:8080/\"")
   ```

3. **Ouvrir dans Android Studio**
   - File → Open → frontend/
   - Synchroniser Gradle

4. **Lancer l'app**
   - Créer or sélectionner un émulateur/device
   - Run → app

---

## 12. Workflow Utilisateur

### 👤 Joueur
```
1. Login/Register (Joueur)
   ↓
2. Dashboard → Voir Coachs
   ↓
3. Rechercher par jeu/rang
   ↓
4. Cliquer coach → Détails + Avis
   ↓
5. Bouton "Demander session" → Saisir durée/date
   ↓
6. Mes Sessions → Voir demande (REQUESTED)
   ↓
7. Coach accepte → Status ACCEPTED
   ↓
8. Chat avec coach
   ↓
9. Coach termine → Status COMPLETED
   ↓
10. Confirmer réception → CONFIRMED
    ↓
11. Simuler paiement → PAID
    ↓
12. Noter le coach (1-5 étoiles) + commentaire
```

### 👑 Coach
```
1. Register (Coach) → Soumettre candidature
   ↓
2. En attente approbation admin
   ↓
3. Admin approuve → is_approved = true
   ↓
4. Dashboard Coach → Voir sessions entrantes
   ↓
5. Sessionsdemandes → List (REQUESTED)
   ↓
6. Accepter/Refuser demande
   ↓
7. Chat avec joueur
   ↓
8. Marquer terminée → COMPLETED
   ↓
9. Voir revenus accumulés
   ↓
10. Voir notes reçues
```

---

## 13. Testing

### Unit Tests (à ajouter)
```kotlin
// Tests ViewModels
class AuthViewModelTest { ... }
class CoachViewModelTest { ... }

// Tests Repositories
class AuthRepositoryTest { ... }

// Tests Utils
class ValidationUtilsTest { ... }
```

### Integration Tests
```kotlin
// Tests API calls
class ApiServiceTest { ... }

// Tests Navigation
class NavigationTest { ... }
```

---

## 14. Build & Deployment

### Build Debug APK
```bash
./gradlew assembleDebug
```

### Build Release APK
```bash
./gradlew assembleRelease
```

### Build App Bundle (Play Store)
```bash
./gradlew bundleRelease
```

---

## 15. Troubleshooting

### Problem: "Failed to connect to API"
**Solution**: 
- Vérifier que le backend est lancé sur `http://localhost:8080`
- Utiliser `10.0.2.2:8080` depuis l'émulateur Android
- Vérifier les permissions internet dans AndroidManifest.xml

### Problem: "401 Unauthorized"
**Solution**:
- Token expiré → Re-login
- Token corrompu → Vider DataStore et re-login
- Backend refuse le token → Vérifier jwt.secret (backend)

### Problem: Layout XML not found
**Solution**:
- Vérifier que les layouts XML sont dans `res/layout/`
- Vérifier spelling dans `@layout/fragment_*`

---

## 16. Points d'Extension Futurs

### V2 (À venir)
- [ ] Refresh Token implementation
- [ ] Real-time chat avec WebSocket
- [ ] Video call coaching
- [ ] Upload images/preuves de niveau
- [ ] Notifications push
- [ ] Payment Gateway (Stripe/PayPal)
- [ ] Rating system amélioré
- [ ] Admin dashboard complet
- [ ] Maps intégration
- [ ] Dark mode

---

## 17. Dépannage Gradle

```bash
# Nettoyer et rebuild
./gradlew clean build

# Synchroniser Gradle
./gradlew sync

# Vérifier dépendances
./gradlew dependencies
```

---

## 18. Structure des Fichiers Clés

| Fichier | Utilisation |
|---------|------------|
| `AndroidManifest.xml` | Permissions, Activities, Configuration |
| `build.gradle.kts` | Dépendances, Versions, Configuration build |
| `nav_graph.xml` | Navigation entre Fragments |
| `AppModule.kt` | Injection de dépendances (Hilt) |
| `TokenManager.kt` | Gestion JWT |
| `ApiService.kt` | Endpoints REST |
| `RetrofitClient.kt` | Configuration HTTP |
| `*ViewModel.kt` | Gestion d'état |
| `*Fragment.kt` | UI Logic |
| `*Models.kt` | Data classes |
| `*Repository.kt` | Data access layer |

---

## 19. Support & Contact

Pour toute question ou bug report:
- GitHub Issues: [Issues]
- Email: support@gameboost.com
- Documentation: [Frontend.md](../frontend.md)

---

## ✅ Status

- **Architecture** : ✅ Complète (MVVM + Repository)
- **Authentification** : ✅ JWT intégré
- **API Integration** : ✅ Retrofit + Interceptors
- **State Management** : ✅ ViewModel + LiveData
- **Navigation** : ✅ Jetpack Navigation
- **DI** : ✅ Hilt
- **UI Screens** : ✅ 7 fragments principaux
- **Error Handling** : ✅ Global + Spécifique
- **Validation** : ✅ Input + Business logic
- **Testing** : ⏳ À ajouter (Jest/MockK)
- **CI/CD** : ⏳ À configurer

---

**Frontend Version** : 1.0.0  
**Last Updated** : Avril 5, 2026  
**Compatible with Backend** : v1.0.0+  
**Min Android SDK** : 24 | **Target** : 34

---

🎮 **GameBoost - Coaching Gaming Platform**
