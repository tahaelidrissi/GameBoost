# 🎮 GameBoost - Frontend Documentation (Android)

**Plateforme** : Application mobile Android pour connecter joueurs et coachs.

**Contexte** : Projet académique - MVP fonctionnel.

---

## 1. Stack Technique Frontend (Android)

| Composant | Options possibles |
|-----------|-------------------|
| **Framework** | Flutter / React Native / Android natif (Kotlin/Java) |
| **State Management** | Provider / Riverpod / GetX / Bloc / MobX |
| **HTTP Client** | Dio / http / Retrofit (Kotlin) / Axios (React Native) |
| **Storage Local** | SharedPreferences / Hive / SQLite / AsyncStorage |
| **Navigation** | Navigator 2.0 / Named Routes / React Navigation / Jetpack Navigation |
| **UI Components** | Material Design / Cupertino / Custom Design System |

---

## 2. Architecture Frontend

L'architecture doit supporter :
- **Models** : Structure des données (User, Coach, Session, Message, Review)
- **State Management** : Gestion centralisée de l'authentification, coachs, sessions, messages, utilisateur
- **Services** : Couche API (authentification, coachs, sessions, messages, reviews), Stockage local (JWT, préférences)
- **Screens/Pages** : Interface utilisateur (28 écrans détaillés en section 4)
- **Components/Widgets** : Éléments réutilisables (boutons, champs de saisie, cartes, barres de navigation)
- **Utils** : Validations, constantes, formatage, utilitaires

---

## 3. Flux d'Authentification

```
┌─────────────────┐
│  Splash Screen  │
└────────┬────────┘
         │
         ├─ JWT stocké ? ─→ Vérifier validité
         │                      │
         │                      ├─ Valide → Dashboard selon rôle
         │                      └─ Expiré → Écran Login
         │
         └─ Pas de JWT → Écran Login
                             │
                   ┌─────────┼─────────┐
                   │         │         │
                Register  Login    Forgot Password
                   │         │         │
                   └─────────┼─────────┘
                             │
                        Dashboard
```

---

## 4. Pages et Écrans par Rôle

### A. Écrans Communs (Tous les rôles)

| # | Écran | Description | Éléments |
|---|-------|-------------|----------|
| 1 | **Splash Screen** | Affichage au démarrage | Logo, vérification JWT |
| 2 | **Login** | Authentification | Email, Password, Bouton Login, Lien Register |
| 3 | **Register** | Création de compte | Email, Password, Username, Role (Joueur/Coach), Bouton Register |
| 4 | **Profil** | Affichage profil utilisateur | Username, Email, Role, Date inscription, Bouton Modifier |
| 5 | **Profil - Édition** | Modifier informations | Champs éditables, Bouton Sauvegarder |
| 6 | **Paramètres** | Paramètres app | Langue, Notifications, Déconnexion |

### B. Écrans Joueur (Bénéficiaire)

| # | Écran | Description | Éléments |
|---|-------|-------------|----------|
| 7 | **Accueil Joueur** | Dashboard principal | Bienvenue, Boutons navigation rapide |
| 8 | **Liste des Coachs** | Recherche et filtrage | Recherche (jeu, rang), Liste coachs avec filtres |
| 9 | **Détail Coach** | Informations complètes | Profil, Bio, Rang, Tarif, Notes/Avis, Bouton "Demander session" |
| 10 | **Demander Session** | Créer une session | Sélection durée, Date/Heure, Bouton Confirmer |
| 11 | **Mes Sessions** | Liste des sessions | Filtres (tous, en attente, acceptées, terminées), Cartes sessions |
| 12 | **Détail Session** | Informations session | Coach, Statut, Date, Montant, Boutons actions (accepter si coach, payer si confirmée) |
| 13 | **Chat** | Conversation avec coach | Messages, Input message, Bouton Envoyer |
| 14 | **Évaluation Coach** | Noter le coach | Slider/Stars (1-5), Commentaire optionnel, Bouton Confirmer |
| 15 | **Paiement (Simulation)** | Paiement session | Montant total, Récapitulatif, Bouton "Simuler Paiement" |

### C. Écrans Coach (Fournisseur)

| # | Écran | Description | Éléments |
|---|-------|-------------|----------|
| 16 | **Accueil Coach** | Dashboard principal | Stats (sessions, revenus), Boutons navigation |
| 17 | **Candidature Coach** | Inscription coach | Jeu, Rang, Bio, Tarif horaire, Upload preuve (JPG/PNG max 5MB → Base64), Soumettre candidature |
| 18 | **Profil Coach** | Affichage profil | Jeu, Rang, Bio, Tarif, Statut (En attente/Approuvé), Évaluations |
| 19 | **Profil Coach - Édition** | Modifier profil | Tous les champs éditables, Bouton Sauvegarder |
| 20 | **Mes Sessions** | Liste sessions coach | Filtres (demandes, acceptées, en cours, terminées), Cartes sessions |
| 21 | **Détail Session** | Demande coaching | Joueur, Statut, Date, Montant, Boutons (Accepter/Refuser, Terminer, Chat) |
| 22 | **Chat** | Conversation avec joueur | Messages, Input message, Bouton Envoyer |
| 23 | **Mes Revenus** | Historique revenus | Tableau revenus par session, Total généré |
| 24 | **Évaluations** | Notes reçues | Liste avis des joueurs, Moyenne des notes |

### D. Écrans Admin (Administrateur)

| # | Écran | Description | Éléments |
|---|-------|-------------|----------|
| 25 | **Accueil Admin** | Dashboard admin | Statistiques clés |
| 26 | **Coachs en attente** | Validation coachs | Liste coachs, Preuve image, Boutons (Approuver/Rejeter) |
| 27 | **Détail Coach (Admin)** | Infos complètes coach | Profil, Preuves, Avis, Boutons actions |
| 28 | **Gestion Utilisateurs & Statistiques** | Liste utilisateurs + Analyses | Recherche, Filtres (joueurs, coachs, admins), Actions, Graphiques (users, sessions, revenus), KPIs |

---

## 5. Récapitulatif Total des Écrans

**Total : 28 écrans principaux**

| Rôle | Nombre d'écrans |
|------|-----------------|
| **Communs** | 6 |
| **Joueur** | 9 |
| **Coach** | 9 |
| **Admin** | 4 |
| **Total** | 28 |

---

## 6. Composants Réutilisables

### Composants Common (Material Design Android)

| Composant | Utilisation |
|-----------|-------------|
| **CustomAppBar** | AppBar Material standardisée |
| **CustomButton** | Boutons Material (ElevatedButton, TextButton) |
| **CustomInputField** | TextField avec validation |
| **LoadingWidget** | CircularProgressIndicator |
| **ErrorWidget** | AlertDialog pour erreurs |
| **BottomNavBar** | BottomNavigationBar Material |
| **ConfirmDialog** | Confirmations/Alertes |
| **EmptyState** | État vide (pas de données) |
| **DateTimePicker** | showDatePicker / showTimePicker Android |
| **RatingBar** | Sélection note (1-5 étoiles) |

### Composants Métier

| Composant | Utilisation |
|-----------|-------------|
| **CoachCard** | Affichage coach (mini + détail) |
| **SessionCard** | Affichage session |
| **MessageBubble** | Bulle chat |
| **ReviewCard** | Affichage avis |
| **FilterBar** | Filtrage (jeu, rang, etc) |
| **StatCard** | Affichage statistiques |

---

## 7. State Management (Données Globales)

### AuthProvider
- **État** : user, isAuthenticated, isLoading, token
- **Actions** : login, register, logout, checkAuth

### CoachProvider
- **État** : coaches[], selectedCoach, isLoading
- **Actions** : fetchCoaches, fetchCoachDetail, searchCoaches

### SessionProvider
- **État** : sessions[], selectedSession, isLoading
- **Actions** : fetchSessions, createSession, updateSessionStatus, fetchSessionDetail

### MessageProvider
- **État** : messages[], isLoading
- **Actions** : fetchMessages, sendMessage

### UserProvider
- **État** : currentUser, isLoading
- **Actions** : fetchProfile, updateProfile

---

## 8. Flux de Données - Exemple : Demande de Session Joueur

```
┌──────────────────┐
│ CoachDetailPage  │ ← Affichage coach
└────────┬─────────┘
         │ Bouton "Demander session"
         ↓
┌──────────────────────────┐
│ SessionRequestPage       │ ← Saisie durée, date
└────────┬─────────────────┘
         │ Bouton "Confirmer"
         ↓
┌──────────────────────────────────┐
│ SessionProvider.createSession()   │ ← Appel API POST /sessions/request
└────────┬─────────────────────────┘
         │ Réponse API
         ↓
┌──────────────────────────────────┐
│ SessionProvider met à jour état  │
└────────┬─────────────────────────┘
         │
         ↓
┌──────────────────┐
│ MySessionsPage   │ ← Nouvelle session affichée
└──────────────────┘
```

---

## 9. Intégration API

### Service de Base

```dart
class ApiService {
  final String baseUrl = "http://localhost:3000/api";
  final String token;  // Récupéré du storage
  
  Future<T> get<T>(String endpoint, ...) async {
    // Ajouter Authorization header avec JWT
    // Gérer erreurs
  }
  
  Future<T> post<T>(String endpoint, Map data, ...) async {
    // Ajouter Authorization header
    // Encoder JSON
    // Gérer erreurs
  }
  
  Future<T> patch<T>(String endpoint, Map data, ...) async {
    // Similaire à post
  }
}
```

### Appels API typiques

**Login :**
```dart
POST /auth/login
Réponse : { token, user }
```

**Récupérer coachs :**
```dart
GET /coaches?game=Valorant&rank=Immortal
Réponse : { coaches: [...], total }
```

**Créer session :**
```dart
POST /sessions/request
Body : { coach_id, duration_hours, scheduled_at }
Réponse : { session }
```

---

## 10. Stockage Local

### SharedPreferences / Hive

| Clé | Type | Utilisation |
|-----|------|-------------|
| `jwt_token` | String | Stockage JWT pour persistence |
| `user_role` | String | Rôle utilisateur (pour routing) |
| `user_id` | String | ID utilisateur |
| `last_sessions` | JSON | Dernières sessions (cache) |
| `theme_mode` | String | Thème (clair/sombre) |

---

## 11. Navigation et Routing

### Routes Principales

```
/ → SplashScreen
  ├─ /auth
  │  ├─ /login
  │  └─ /register
  └─ /dashboard
     ├─ /player
     │  ├─ /home
     │  ├─ /coaches
     │  ├─ /coaches/:id
     │  ├─ /sessions
     │  ├─ /sessions/:id
     │  ├─ /chat/:sessionId
     │  ├─ /review/:sessionId
     │  └─ /profile
     ├─ /coach
     │  ├─ /home
     │  ├─ /profile (edit)
     │  ├─ /sessions
     │  ├─ /sessions/:id
     │  ├─ /chat/:sessionId
     │  ├─ /earnings
     │  └─ /reviews
     └─ /admin
        ├─ /dashboard
        ├─ /pending-coaches
        ├─ /coaches/:id
        ├─ /users
        └─ /statistics
```

---

## 12. Validations Frontend

| Champ | Validations |
|-------|-------------|
| **Email** | Format email, Requis |
| **Password** | Min 8 caractères, Min 1 majuscule, Min 1 chiffre |
| **Username** | 3-20 caractères, Alphanumérique + underscore |
| **Hourly Rate** | Nombre > 0, Max 2 décimales |
| **Duration** | Entre 1 et 24 heures |
| **Rating** | Entre 1 et 5 |
| **Bio** | Max 500 caractères |

---

## 13. Gestion des Erreurs Frontend

| Type d'erreur | Gestion |
|---------------|---------|
| **401 Unauthorized** | Redirection login, Effacer JWT |
| **403 Forbidden** | Affichage message (accès refusé) |
| **404 Not Found** | Affichage page erreur, Bouton retour |
| **Network Error** | Affichage message, Bouton réessayer |
| **Validation Error** | Affichage en rouge sous champ |

---

## 14. Performance & UX (Simplifiées pour MVP Académique)

### Optimisations Prioritaires

| Optimisation | Impact | Priorité |
|--------------|--------|----------|
| **Pagination** | Charger coachs par 10-20 à la fois | ⭐ Moyenne |
| **Caching local** | SharedPreferences pour JWT | ⭐⭐⭐ Critique |
| **Pull-to-Refresh** | Rafraîchir listes | ⭐⭐ Haute |
| **Loading indicators** | CircularProgressIndicator | ⭐⭐⭐ Critique |

### UX Standards Android

- CircularProgressIndicator pendant requête API
- SnackBar pour messages succès/erreur
- AlertDialog pour confirmations
- Bouton retour Android natif

---

## 15. Accès par Rôle (Guard Routes)

```
Login → Stocké JWT + Role

Role = JOUEUR → Accès pages /player/* (interdire /coach/*, /admin/*)
Role = COACH → Accès pages /coach/* (interdire /player/*, /admin/*)
Role = ADMIN → Accès pages /admin/* (peut voir tous les rôles)

Si token expiré → Retour login
```

---

## 16. Notifications (Optionnel pour MVP)

| Type | Déclencheur |
|------|-------------|
| **Session acceptée** | Coach accepte demande du joueur |
| **Nouveau message** | Nouveau message dans conversation |
| **Coach approuvé** | Admin approuve candidature coach |
| **Session confirmée** | Joueur confirme session |

---

## 17. Checklist de Développement (MVP Académique)

**Phase 1 - Setup & Auth**
- [ ] Setup projet
- [ ] Authentification avec JWT
- [ ] Splash screen

**Phase 2 - Fonctionnalités Joueur**
- [ ] Liste coachs
- [ ] Détail coach
- [ ] Demander session
- [ ] Mes sessions
- [ ] Profil utilisateur

**Phase 3 - Fonctionnalités Coach**
- [ ] Candidature coach
- [ ] Gestion sessions
- [ ] Revenus
- [ ] Profil coach

**Phase 4 - Chat**
- [ ] Messages entre joueur et coach
- [ ] Interface chat simple

**Phase 5 - Admin & Finalisation**
- [ ] Validation coachs
- [ ] Dashboard stats
- [ ] Tests et corrections

---

## 18. Responsivité Android

**Pour projet académique** : Se concentrer sur smartphone uniquement (portrait mode).

| Taille | Adaptation |
|--------|------------|
| **Smartphone (portrait)** | Layout simple colonne (prioritaire) |
| **Smartphone (landscape)** | Optionnel (même layout) |
| **Tablet** | ❌ Non nécessaire pour MVP académique |

---

