# 📱 Guide d'Intégration Frontend-Backend

**Date** : Avril 5, 2026  
**Frontend** : Kotlin Android  
**Backend** : Java Spring Boot  
**Status** : ✅ Prêt pour intégration complète

---

## 1. Architecture d'Intégration

```
┌─────────────────────────────────────────────────────────┐
│              Frontend Android (Kotlin)                  │
│  ┌─────────────────────────────────────────────────────┐│
│  │  UI Layer (Fragments)                               ││
│  │  - LoginFragment, DashboardFragment, etc            ││
│  └────────────────────┬────────────────────────────────┘│
│                       │ observe                         │
│  ┌────────────────────▼────────────────────────────────┐│
│  │  ViewModel + LiveData (State Management)            ││
│  │  - AuthViewModel, CoachViewModel, SessionViewModel  ││
│  └────────────────────┬────────────────────────────────┘│
│                       │ calls                           │
│  ┌────────────────────▼────────────────────────────────┐│
│  │  Repository Pattern (Data Access)                   ││
│  │  - AuthRepository, CoachRepository, etc             ││
│  └────────────────────┬────────────────────────────────┘│
│                       │ uses                            │
│  ┌────────────────────▼────────────────────────────────┐│
│  │  Retrofit API Service + Interceptors                ││
│  │  - AuthInterceptor (JWT Bearer token)               ││
│  │  - HttpLoggingInterceptor                           ││
│  └────────────────────┬────────────────────────────────┘│
└──────────────────────│─────────────────────────────────┘
                       │ HTTP REST API
                       │ (JWT Authorization)
┌──────────────────────▼─────────────────────────────────┐
│           Backend Java Spring Boot                     │
│  ┌─────────────────────────────────────────────────────┐
│  │  Controllers (API Endpoints)                        │
│  │  - AuthController, CoachController, etc             │
│  └────────────────────┬────────────────────────────────┘
│                       │
│  ┌────────────────────▼────────────────────────────────┐
│  │  Services (Business Logic)                          │
│  │  - AuthService, CoachService, SessionService, etc   │
│  └────────────────────┬────────────────────────────────┘
│                       │
│  ┌────────────────────▼────────────────────────────────┐
│  │  Repositories (JPA/Database)                        │
│  │  - UserRepository, CoachProfileRepository, etc      │
│  └────────────────────┬────────────────────────────────┘
│                       │
│  ┌────────────────────▼────────────────────────────────┐
│  │  Database (MySQL)                                   │
│  │  - users, coach_profiles, sessions, messages, etc   │
│  └─────────────────────────────────────────────────────┘
└─────────────────────────────────────────────────────────┘
```

---

## 2. Points de Connexion Frontend ↔ Backend

### 2.1 Configuration API

**Frontend (`build.gradle.kts`)** :
```kotlin
buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8080/\"")
```

**Note** :
- `10.0.2.2` = accès à localhost depuis l'émulateur Android
- En production : remplacer par l'URL du serveur réel (ex: `https://api.gameboost.com/`)

### 2.2 Flux Authentification

**Frontend ↔ Backend** :
```
1. User: Login(email, password)
2. Frontend → POST /auth/login → Backend
3. Backend → Response: { token, user }
4. Frontend → TokenManager.saveToken(token)
5. Frontend → Redirect vers Dashboard
6. Suivant requests → Authorization: Bearer {token}
```

### 2.3 Endpoints API Principaux

#### Auth
```
POST   /auth/register        (Public)
POST   /auth/login           (Public)
GET    /users/me             (Protected)
PUT    /users/me             (Protected)
```

#### Coachs
```
GET    /coaches              (Protected) → Retourne liste complète
GET    /coaches/{id}         (Protected) → Détails + avis
POST   /coaches/profile      (Protected) → Candidature coach
PUT    /coaches/me/profile   (Protected) → Mise à jour profil
GET    /coaches/me/earnings  (Protected) → Revenus totaux
```

#### Sessions
```
POST   /sessions/request     (Protected) → Créer demande
GET    /sessions             (Protected) → Lister sessions
PATCH  /sessions/{id}/accept (Protected) → Coach accepte
PATCH  /sessions/{id}/complete (Protected) → Fin service
PATCH  /sessions/{id}/confirm (Protected) → Joueur confirme
POST   /sessions/{id}/pay    (Protected) → Simulation paiement
POST   /sessions/{id}/review (Protected) → Laisser avis
GET    /sessions/{id}/messages (Protected) → Chat messages
POST   /sessions/{id}/messages (Protected) → Envoyer message
```

#### Admin
```
GET    /admin/coaches/pending (ADMIN) → Coachs en attente
PATCH  /admin/coaches/{id}/approve (ADMIN) → Approuver coach
PATCH  /admin/coaches/{id}/reject (ADMIN) → Rejeter candidature
GET    /admin/dashboard      (ADMIN) → Statistiques
GET    /admin/users          (ADMIN) → Liste utilisateurs
```

---

## 3. Modèles de Données Partagés

### User (Frontend ↔ Backend)
```json
{
  "id": "uuid",
  "email": "user@example.com",
  "username": "ProGamer",
  "role": "JOUEUR|COACH|ADMIN",
  "isApproved": true,
  "createdAt": "2026-02-28T10:00:00Z"
}
```

### CoachProfile (Frontend ↔ Backend)
```json
{
  "id": 1,
  "userId": "uuid",
  "gameTitle": "Valorant",
  "rank": "Immortal",
  "bio": "Expert coach",
  "hourlyRate": 25.00,
  "proofImage": null|"base64...",
  "createdAt": "2026-02-28T10:00:00Z"
}
```

### Session (Frontend ↔ Backend)
```json
{
  "id": 15,
  "playerId": "uuid",
  "coachId": 1,
  "status": "REQUESTED|ACCEPTED|COMPLETED|CONFIRMED|PAID",
  "durationHours": 2,
  "amount": 50.00,
  "scheduledAt": "2026-03-05T18:00:00Z",
  "createdAt": "2026-02-28T16:00:00Z"
}
```

### Message (Frontend ↔ Backend)
```json
{
  "id": 42,
  "sessionId": 15,
  "senderId": "uuid",
  "senderName": "ProGamer",
  "content": "Salut! Comment ça va?",
  "createdAt": "2026-02-28T16:30:00Z"
}
```

### Review (Frontend ↔ Backend)
```json
{
  "id": 7,
  "sessionId": 15,
  "rating": 5,
  "comment": "Excellent coach!",
  "createdAt": "2026-02-28T17:00:00Z"
}
```

---

## 4. Authentification & Sécurité

### JWT Token Structure

**Header** :
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

**Payload** :
```json
{
  "user_id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "user@example.com",
  "role": "JOUEUR",
  "is_approved": true,
  "iat": 1709097600,
  "exp": 1709184000
}
```

**Signature** : HMAC SHA-256 avec `jwt.secret` (32+ chars)

### Flow Sécurité

1. **Frontend** :
   - Token stocké dans DataStore (persistant, chiffré)
   - Automatiquement ajouté à chaque request via AuthInterceptor
   - Si 401 → Clear token + Redirect vers Login

2. **Backend** :
   - JwtUtils valide signature et expiration
   - JwtAuthFilter applique la validation
   - SecurityConfig protège les endpoints

---

## 5. Gestion des Erreurs

### Codes HTTP Standardisés

| Code | Meaning | Frontend Action |
|------|---------|-----------------|
| 200 | OK | Traiter réponse normalement |
| 201 | Created | Afficher success message |
| 400 | Bad Request | Afficher erreur de validation |
| 401 | Unauthorized | Token expiré → Redirect Login |
| 403 | Forbidden | Role insuffisant → Afficher message |
| 404 | Not Found | Ressource inexistante → Afficher message |
| 409 | Conflict | Email/pseudo déjà utilisé → Afficher message |
| 500 | Server Error | Erreur serveur → Toast error |

### Format Réponse Unifié

**Succès** :
```json
{
  "success": true,
  "message": "Opération réussie",
  "data": { ... }
}
```

**Erreur** :
```json
{
  "success": false,
  "error": "VALIDATION_ERROR",
  "message": "Email déjà utilisé",
  "details": { ... }
}
```

---

## 6. Validation & Règles Métier

### Frontend Validation
```kotlin
// ValidationUtils.kt
isEmailValid(email)              → Format email correct
isPasswordValid(password)        → Min 8 chars, 1 maj, 1 chiffre
isUsernameValid(username)       → 3-20 chars, alphanumériques
```

### Backend Validation (double check)
```java
// Dans RequestDTOs + Controllers
@Email
@NotBlank
@Size(min=8)
// ... annotations Jakarta Validation
```

### Règles Métier

| Règle | Enforcement |
|-------|------------|
| Coach doit être approuvé | Backend: SecurityConfig + JwtUtils |
| Session date future seulement | Backend: SessionService.createSession() |
| Un joueur ne peut noter qu'une fois | Backend: ReviewService |
| Amount = hourly_rate × duration | Backend: SessionService |
| Coach isolation (ne voit que ses sessions) | Backend: SessionService |

---

## 7. Workflow Complet : Créer une Session

### User Story
```
Joueur trouve un coach → Clique "Demander session" → 
Coach accepte → Chat → Coach termine → 
Joueur confirme → Paiement simulé → Évaluation coach
```

### Détails Technique

#### 1️⃣ Frontend: CoachListFragment
```kotlin
viewModel.fetchCoaches(game = "Valorant", rank = "Immortal")
// API: GET /coaches?game=Valorant&rank=Immortal
// Response: List<CoachResponse>
```

#### 2️⃣ Frontend: Navigation vers SessionRequest
```kotlin
findNavController().navigate(
    CoachDetailFragmentDirections.actionToSessionRequest(coachId = 3)
)
```

#### 3️⃣ Frontend: SessionRequestFragment (créer)
```kotlin
// User entre: durationHours = 2, scheduledAt = "2026-03-05T18:00:00Z"
val request = SessionRequest(coachId = 3, durationHours = 2, scheduledAt = ...)
sessionViewModel.createSession(request)
```

#### 4️⃣ Frontend → Backend
```
POST /sessions/request
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "coach_id": 3,
  "duration_hours": 2,
  "scheduled_at": "2026-03-05T18:00:00Z"
}
```

#### 5️⃣ Backend: SessionController
```java
@PostMapping("/request")
public ResponseEntity<ApiResponse<Session>> createSession(
    @AuthenticationPrincipal UserDetails userDetails,
    @Valid @RequestBody SessionRequest request
) {
    // SessionService.createSession valide:
    // - User est JOUEUR
    // - Coach existe et is_approved = true
    // - Scheduled_at > now()
    // - Crée Session(status = REQUESTED)
    return ResponseEntity.ok(apiResponse);
}
```

#### 6️⃣ Backend → Frontend (Response)
```json
{
  "success": true,
  "message": "Demande de session créée",
  "data": {
    "id": 15,
    "player_id": "uuid",
    "coach_id": 3,
    "status": "REQUESTED",
    "duration_hours": 2,
    "amount": 50.00,
    "scheduled_at": "2026-03-05T18:00:00Z"
  }
}
```

#### 7️⃣ Frontend: SessionViewModel
```kotlin
// Response reçue → MAJ LiveData
_sessions.value = [ ... nouvelle session REQUESTED ... ]
// Redirect vers SessionListFragment
```

#### 8️⃣ Coach Accepte (Backend)
```
PATCH /sessions/15/accept
Authorization: Bearer {coach_jwt}

// Backend:
// - Vérifie user = coach de la session
// - Change status: REQUESTED → ACCEPTED
// - Notifie joueur (futur: WebSocket)
```

#### 9️⃣ Frontend: Affichage Chat
```kotlin
// Joueur et Coach peut discuter via messages
GET /sessions/15/messages
POST /sessions/15/messages { "content": "..." }
```

#### 🔟 Coach Termine
```
PATCH /sessions/15/complete
// status: ACCEPTED → COMPLETED
```

#### 1️⃣1️⃣ Joueur Confirme
```
PATCH /sessions/15/confirm
// status: COMPLETED → CONFIRMED
```

#### 1️⃣2️⃣ Simulation Paiement
```
POST /sessions/15/pay
// status: CONFIRMED → PAID
```

#### 1️⃣3️⃣ Jouer Évalue
```
POST /sessions/15/review
{
  "rating": 5,
  "comment": "Excellent!"
}
// Crée Review + MAJ average_rating du coach
```

---

## 8. Configuration pour Développement Local

### Démarrer le Backend
```bash
cd backend/
mvn clean spring-boot:run
# Serveur lancé sur http://localhost:8080
```

### Configurer Frontend
```kotlin
// build.gradle.kts
buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8080/\"")
```

### Lancer Frontend
```bash
# Android Studio → Run → app
# OU en CLI:
cd frontend/
./gradlew installDebug
```

### Tester avec Postman (optionnel)
```bash
# 1. Register
POST http://localhost:8080/auth/register
Body: { "email": "test@ex.com", "password": "Password123", "username": "testuser", "role": "JOUEUR" }

# 2. Login
POST http://localhost:8080/auth/login
Body: { "email": "test@ex.com", "password": "Password123" }
Response: { "token": "eyJh...", "user": {...} }

# 3. Get Coaches (avec token)
GET http://localhost:8080/coaches
Header: Authorization: Bearer eyJh...
```

---

## 9. Dépannage Intégration

### Erreur 1: "9 (net::ERR_ADDRESS_UNREACHABLE)"
**Cause** : Backend non lancé ou URL incorrecte  
**Solution** :
```bash
# Vérifier backend lancé
curl http://localhost:8080/coaches
# Si OK, l'émulateur doit utiliser 10.0.2.2 au lieu de 127.0.0.1
```

### Erreur 2: "401 Unauthorized"
**Cause** : Token expiré ou absent  
**Solution** :
```kotlin
// Vérifier TokenManager retourne le token
val token = tokenManager.getToken()
// Si null → User n'est pas connecté, redirect Login
```

### Erreur 3: "Retrofit couldn't parse JSON"
**Cause** : Format réponse API différent du modèle  
**Solution** :
```kotlin
// Vérifier ApiResponse wrapper
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)
// Backend doit retourner exactement ce format
```

### Erreur 4: "403 Forbidden"
**Cause** : Rôle utilisateur insuffisant  
**Solution** :
```kotlin
// Vérifier backend SecurityConfig
@PreAuthorize("hasRole('COACH')")
// Frontend doit afficher appropriately selon le role dans JWT
```

---

## 10. À Faire pour Intégration Complète

- [ ] Configurer URL API réelle (production)
- [ ] Ajouter tests d'intégration (MockK + Retrofit)
- [ ] Implémenter Real-time chat (WebSocket)
- [ ] Ajouter notifications push FCM
- [ ] Upload images (Preuves coach, avatars)
- [ ] CI/CD pipeline (GitHub Actions + Play Store)
- [ ] Monitoring + Logging (Sentry, Firebase)
- [ ] Refresh token
- [ ] Admin dashboard complet
- [ ] Performance optimization (Pagination, Caching)

---

## 11. Documentation de Référence

| Document | Lien |
|----------|------|
| Frontend Spec | [frontend.md](../frontend.md) |
| Backend Spec | [backend.md](../backend.md) |
| System Design | [SystemDesign.md](../SystemDesign.md) |
| Backend README | [backend/README.md](../backend/README.md) |
| Frontend README | [frontend/README.md](README.md) |

---

## 12. Support & Contact

- **Questions Frontend** : Consulter `frontend/README.md`
- **Questions Backend** : Consulter `backend/README.md`
- **Architecture** : Consulter `SystemDesign.md`

---

✅ **Status** : Prêt pour développement complet et déploiement

**Version** : 1.0.0  
**Last Updated** : Avril 5, 2026  
**Maintainers** : GitHub Copilot
