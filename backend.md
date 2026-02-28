# 🎮 GameBoost - Backend Documentation

**Plateforme** : Connecte des joueurs (bénéficiaires) avec des coachs (fournisseurs) via une API REST avec authentification JWT.

---

## 1. Stack Technique Backend

| Composant | Options possibles |
|-----------|-------------------|
| **Framework/Runtime** | Node.js (Express) / Python (Django, Flask) / Java (Spring Boot) / PHP (Laravel) |
| **Base de données** | SQLite / PostgreSQL / MySQL / MongoDB |
| **Authentification** | JWT (HS256) |
| **Hachage mot de passe** | bcrypt |
| **Format données** | JSON |

---

## 2. Architecture Logicielle (N-Tier Backend)

Le backend est structuré en quatre couches :

- **Couche Service (API)** : Point d'entrée du serveur qui réceptionne les requêtes JSON et gère la sécurité (JWT).
- **Couche Logique Métier (Core)** : Contient les règles (calcul des prix, workflow d'approbation, logique du chat).
- **Couche d'Accès aux Données (DAL)** : Gère les interactions avec la base de données SQL.
- **Couche Utilitaires** : Validators, error handlers, middleware d'authentification.

---

## 3. Authentification et Sécurité (JWT)

### Paramètres Techniques

| Paramètre | Valeur |
|-----------|--------|
| **Algorithme** | HS256 (HMAC SHA-256) |
| **Durée de validité** | 24 heures (86400 secondes) |
| **Secret Key** | Stocké dans `.env` (Minimum 32 caractères) |
| **Hachage mot de passe** | bcrypt (salt rounds: 10) |
| **Refresh Token** | ❌ Non implémenté (optionnel pour MVP) |

### Contenu du JWT (Payload)

```json
{
  "user_id": 5,
  "email": "user@example.com",
  "role": "JOUEUR",
  "is_approved": true,
  "iat": 1709097600,
  "exp": 1709184000
}
```

### Utilisation dans les Requêtes

**Toutes les requêtes authentifiées** doivent inclure :
```
Authorization: Bearer {jwt_token}
```

### Endpoints Publics vs Protégés

| Type | Endpoints | Authentification |
|------|-----------|------------------|
| **Publics** | `/auth/register`, `/auth/login` | ❌ Non requis |
| **Authentifiés** | Tous les autres endpoints | ✅ JWT requis |
| **Admin uniquement** | `/admin/*` | ✅ JWT + role = ADMIN |
| **Coach uniquement** | `/coaches/me/*` | ✅ JWT + role = COACH + is_approved = true |

### Validation des Droits

**Pour chaque endpoint protégé :**
1. Vérifier présence du token dans `Authorization: Bearer`
2. Décoder et valider la signature du JWT
3. Vérifier l'expiration du token
4. Vérifier le rôle de l'utilisateur si requis
5. Exécuter la logique métier

---

## 4. Gestion des Erreurs HTTP

| Code | Signification | Utilisation |
|------|---------------|-------------|
| `200 OK` | Succès | Requête GET ou modification réussie |
| `201 Created` | Ressource créée | POST réussi (création) |
| `400 Bad Request` | Requête invalide | Données manquantes ou invalides |
| `401 Unauthorized` | Non authentifié | Token manquant, expiré ou invalide |
| `403 Forbidden` | Accès refusé | Permissions insuffisantes |
| `404 Not Found` | Ressource introuvable | ID inexistant |
| `409 Conflict` | Conflit d'état | Ex: Email déjà utilisé |
| `500 Internal Server Error` | Erreur serveur | Erreur inattendue côté serveur |

### Format Standardisé des Erreurs

**Succès :**
```json
{
  "success": true,
  "message": "Opération réussie",
  "data": { ... }
}
```

**Erreur :**
```json
{
  "success": false,
  "error": "Error code",
  "message": "Description lisible de l'erreur",
  "details": { ... }
}
```

---

## 5. Validation des Données

### Validations Obligatoires

| Champ | Type | Contraintes |
|-------|------|-------------|
| **email** | String | Format email valide, unique en BD |
| **password** | String | Min 8 caractères, doit contenir au moins une majuscule et un chiffre |
| **username** | String | 3-20 caractères, alphanumérique + underscore |
| **role** | Enum | JOUEUR, COACH, ADMIN |
| **hourly_rate** | Decimal | > 0, max 2 décimales |
| **duration_hours** | Integer | Entre 1 et 24 heures |
| **rating** | Integer | Entre 1 et 5 |
| **game_title** | String | Valeur prédéfinie (Valorant, League of Legends, etc.) |
| **rank** | String | Valeur prédéfinie selon le jeu |

### Validations Métier

| Logique | Description |
|--------|-------------|
| **Coach approval** | Un coach ne peut accepter de sessions que s'il est approuvé (is_approved = true) |
| **Session state machine** | Les transitions d'état doivent suivre : REQUESTED → ACCEPTED → COMPLETED → CONFIRMED → PAID |
| **Coach-Player isolation** | Un coach ne peut pas voir les sessions des autres coachs |
| **Scheduled date** | Une session ne peut être programmée que dans le futur (scheduled_at > now) |
| **Amount calculation** | amount = hourly_rate × duration_hours (calculé côté backend) |
| **Review uniqueness** | Un joueur ne peut noter qu'une seule fois par session |

---

# API Endpoints Documentation

## 1. Module Authentification & Utilisateurs
Ce module gère l'entrée dans l'application.

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/auth/register` | Création de compte (Joueur ou Coach) |
| `POST` | `/auth/login` | Authentification et génération de jeton (token) |
| `GET` | `/users/me` | Récupération du profil de l'utilisateur connecté |
| `PUT` | `/users/me` | Mise à jour des informations personnelles |

### Exemples

#### `POST /auth/register`
**Requête :**
```json
{
  "email": "joueur@example.com",
  "password": "motdepasse123",
  "username": "ProGamer",
  "role": "JOUEUR"
}
```

**Réponse (201 Created) :**
```json
{
  "message": "Compte créé avec succès",
  "user": {
    "id": 5,
    "email": "joueur@example.com",
    "username": "ProGamer",
    "role": "JOUEUR"
  }
}
```

**Erreurs :**
- `400 Bad Request` : "Email déjà utilisé"
- `400 Bad Request` : "Mot de passe trop court (minimum 8 caractères)"

---

#### `POST /auth/login`
**Requête :**
```json
{
  "email": "joueur@example.com",
  "password": "motdepasse123"
}
```

**Réponse (200 OK) :**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 5,
    "email": "joueur@example.com",
    "username": "ProGamer",
    "role": "JOUEUR",
    "is_approved": false
  }
}
```

**Erreurs :**
- `401 Unauthorized` : "Email ou mot de passe incorrect"

---

#### `GET /users/me`
**Headers :**
```
Authorization: Bearer {token}
```

**Réponse (200 OK) :**
```json
{
  "id": 5,
  "email": "joueur@example.com",
  "username": "ProGamer",
  "role": "JOUEUR",
  "is_approved": false,
  "created_at": "2026-02-28T10:00:00Z"
}
```

**Erreurs :**
- `401 Unauthorized` : "Token d'authentification manquant"

---

#### `PUT /users/me`
**Headers :**
```
Authorization: Bearer {token}
```

**Requête :**
```json
{
  "username": "NewUsername",
  "email": "newemail@example.com"
}
```

**Réponse (200 OK) :**
```json
{
  "message": "Profil mis à jour avec succès",
  "user": {
    "id": 5,
    "username": "NewUsername",
    "email": "newemail@example.com",
    "updated_at": "2026-02-28T15:00:00Z"
  }
}
```

---

## 2. Module Coachs (Vue Joueur)
Permet aux bénéficiaires de trouver le service.

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/coaches` | Liste des coachs dont le statut est "approuvé" |
| `GET` | `/coaches/{id}` | Détails complets d'un coach (bio, rang, avis) |
| `PUT` | `/coaches/me/profile` | Le coach met à jour son profil (bio, tarif, jeu, rang) |

### Exemples

#### `GET /coaches`
**Headers :**
```
Authorization: Bearer {token}
```

**Query Parameters (optionnels) :**
- `game` : Filtrer par jeu (ex: `?game=Valorant`)
- `rank` : Filtrer par rang (ex: `?rank=Immortal`)
- `limit` : Nombre de résultats (défaut: 20, max: 100)
- `offset` : Position de départ (défaut: 0)

**Réponse (200 OK) :**
```json
{
  "coaches": [
    {
      "id": 3,
      "username": "ProCoach",
      "game_title": "Valorant",
      "rank": "Immortal",
      "bio": "Coach expérimenté avec 5 ans d'expérience",
      "hourly_rate": 25.00,
      "average_rating": 4.8,
      "total_reviews": 12
    },
    {
      "id": 7,
      "username": "EliteGamer",
      "game_title": "Valorant",
      "rank": "Radiant",
      "bio": "Ancien joueur pro, coaching personnalisé",
      "hourly_rate": 40.00,
      "average_rating": 5.0,
      "total_reviews": 8
    }
  ],
  "total": 2
}
```

---

#### `GET /coaches/{id}`
**Headers :**
```
Authorization: Bearer {token}
```

**Réponse (200 OK) :**
```json
{
  "id": 3,
  "username": "ProCoach",
  "email": "coach@example.com",
  "game_title": "Valorant",
  "rank": "Immortal",
  "bio": "Coach expérimenté avec 5 ans d'expérience",
  "hourly_rate": 25.00,
  "average_rating": 4.8,
  "total_reviews": 12,
  "reviews": [
    {
      "rating": 5,
      "comment": "Excellent coach, très pédagogue !",
      "created_at": "2026-02-20T14:30:00Z"
    },
    {
      "rating": 4,
      "comment": "Bon coaching, j'ai bien progressé",
      "created_at": "2026-02-15T10:00:00Z"
    }
  ]
}
```

**Erreurs :**
- `404 Not Found` : "Coach non trouvé"

---

#### `PUT /coaches/me/profile`
**Headers :**
```
Authorization: Bearer {token}
```

**Requête :**
```json
{
  "bio": "Coach Valorant spécialisé dans les agents contrôleurs",
  "hourly_rate": 30.00,
  "rank": "Radiant"
}
```

**Réponse (200 OK) :**
```json
{
  "message": "Profil mis à jour avec succès",
  "profile": {
    "id": 3,
    "bio": "Coach Valorant spécialisé dans les agents contrôleurs",
    "hourly_rate": 30.00,
    "rank": "Radiant",
    "updated_at": "2026-02-28T15:45:00Z"
  }
}
```

**Erreurs :**
- `403 Forbidden` : "Seuls les coachs peuvent modifier ce profil"

---

## 3. Module Sessions (Le cœur du service)
Gère le cycle de vie du service, de la demande à la réalisation.

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/sessions/request` | Le joueur demande un service |
| `GET` | `/sessions` | Liste des sessions (pour le joueur et le coach) |
| `PATCH` | `/sessions/{id}/accept` | Le coach accepte la demande |
| `PATCH` | `/sessions/{id}/complete` | Le coach déclare la fin du service |
| `PATCH` | `/sessions/{id}/confirm` | Le joueur confirme la réalisation |

### Exemples

#### `POST /sessions/request`
**Headers :**
```
Authorization: Bearer {token}
```

**Requête :**
```json
{
  "coach_id": 3,
  "duration_hours": 2,
  "scheduled_at": "2026-03-05T18:00:00Z"
}
```

**Réponse (201 Created) :**
```json
{
  "message": "Demande de session créée",
  "session": {
    "id": 15,
    "player_id": 5,
    "coach_id": 3,
    "status": "REQUESTED",
    "duration_hours": 2,
    "amount": 50.00,
    "scheduled_at": "2026-03-05T18:00:00Z",
    "created_at": "2026-02-28T16:00:00Z"
  }
}
```

**Erreurs :**
- `403 Forbidden` : "Seuls les joueurs peuvent demander des sessions"
- `404 Not Found` : "Coach non trouvé"
- `400 Bad Request` : "Date de session invalide (doit être dans le futur)"

---

#### `GET /sessions`
**Headers :**
```
Authorization: Bearer {token}
```

**Query Parameters (optionnels) :**
- `status` : Filtrer par statut (ex: `?status=REQUESTED`)
- `limit` : Nombre de résultats (défaut: 20, max: 100)
- `offset` : Position de départ (défaut: 0)

**Réponse (200 OK) - Pour un Joueur :**
```json
{
  "sessions": [
    {
      "id": 15,
      "coach": {
        "id": 3,
        "username": "ProCoach",
        "game_title": "Valorant"
      },
      "status": "REQUESTED",
      "duration_hours": 2,
      "amount": 50.00,
      "scheduled_at": "2026-03-05T18:00:00Z"
    }
  ]
}
```

**Réponse (200 OK) - Pour un Coach :**
```json
{
  "sessions": [
    {
      "id": 15,
      "player": {
        "id": 5,
        "username": "ProGamer"
      },
      "status": "REQUESTED",
      "duration_hours": 2,
      "amount": 50.00,
      "scheduled_at": "2026-03-05T18:00:00Z"
    }
  ]
}
```

---

#### `PATCH /sessions/{id}/accept`
**Headers :**
```
Authorization: Bearer {token}
```

**Réponse (200 OK) :**
```json
{
  "message": "Session acceptée",
  "session": {
    "id": 15,
    "status": "ACCEPTED",
    "updated_at": "2026-02-28T16:30:00Z"
  }
}
```

**Erreurs :**
- `403 Forbidden` : "Seul le coach concerné peut accepter cette session"
- `400 Bad Request` : "Cette session ne peut pas être acceptée (statut actuel: COMPLETED)"

---

#### `PATCH /sessions/{id}/complete`
**Headers :**
```
Authorization: Bearer {token}
```

**Réponse (200 OK) :**
```json
{
  "message": "Session marquée comme terminée",
  "session": {
    "id": 15,
    "status": "COMPLETED",
    "updated_at": "2026-03-05T20:00:00Z"
  }
}
```

---

#### `PATCH /sessions/{id}/confirm`
**Headers :**
```
Authorization: Bearer {token}
```

**Réponse (200 OK) :**
```json
{
  "message": "Session confirmée par le joueur",
  "session": {
    "id": 15,
    "status": "CONFIRMED",
    "updated_at": "2026-03-05T20:15:00Z"
  }
}
```

---

## 4. Module Paiement & Notation
Gère la finalisation de la transaction et la qualité.

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/sessions/{id}/pay` | Le joueur simule le paiement (projet académique) |
| `POST` | `/sessions/{id}/review` | Le joueur note le service |
| `GET` | `/coaches/me/earnings` | Le coach consulte ses indemnités reçues |

### Exemples

#### `POST /sessions/{id}/pay`
**Headers :**
```
Authorization: Bearer {token}
```

**Réponse (200 OK) :**
```json
{
  "message": "Paiement simulé avec succès",
  "session": {
    "id": 15,
    "status": "PAID",
    "amount": 50.00,
    "updated_at": "2026-03-05T20:20:00Z"
  }
}
```

**Erreurs :**
- `400 Bad Request` : "La session doit être confirmée avant le paiement"

---

#### `POST /sessions/{id}/review`
**Headers :**
```
Authorization: Bearer {token}
```

**Requête :**
```json
{
  "rating": 5,
  "comment": "Excellent coach, très pédagogue et patient !"
}
```

**Réponse (201 Created) :**
```json
{
  "message": "Avis enregistré avec succès",
  "review": {
    "id": 42,
    "session_id": 15,
    "rating": 5,
    "comment": "Excellent coach, très pédagogue et patient !",
    "created_at": "2026-03-05T20:30:00Z"
  }
}
```

**Erreurs :**
- `400 Bad Request` : "Vous avez déjà noté cette session"
- `400 Bad Request` : "La note doit être entre 1 et 5"

---

#### `GET /coaches/me/earnings`
**Headers :**
```
Authorization: Bearer {token}
```

**Réponse (200 OK) :**
```json
{
  "total_earnings": 450.00,
  "sessions_count": 9,
  "sessions": [
    {
      "id": 15,
      "player_username": "ProGamer",
      "amount": 50.00,
      "status": "PAID",
      "completed_at": "2026-03-05T20:00:00Z"
    },
    {
      "id": 12,
      "player_username": "NoobPlayer",
      "amount": 75.00,
      "status": "PAID",
      "completed_at": "2026-03-01T19:00:00Z"
    }
  ]
}
```

---

## 5. Module Chat (Communication)
Permet l'échange de messages pour le service.

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/sessions/{id}/messages` | Historique de la conversation |
| `POST` | `/sessions/{id}/messages` | Envoi d'un nouveau message |

### Exemples

#### `GET /sessions/{id}/messages`
**Headers :**
```
Authorization: Bearer {token}
```

**Réponse (200 OK) :**
```json
{
  "messages": [
    {
      "id": 1,
      "sender_id": 5,
      "sender_username": "ProGamer",
      "content": "Salut ! Prêt pour la session ?",
      "sent_at": "2026-03-05T17:45:00Z"
    },
    {
      "id": 2,
      "sender_id": 3,
      "sender_username": "ProCoach",
      "content": "Oui, on commence dans 15 minutes !",
      "sent_at": "2026-03-05T17:50:00Z"
    }
  ],
  "total": 2
}
```

---

#### `POST /sessions/{id}/messages`
**Headers :**
```
Authorization: Bearer {token}
```

**Requête :**
```json
{
  "content": "Merci pour la session, c'était top !"
}
```

**Réponse (201 Created) :**
```json
{
  "message": "Message envoyé",
  "data": {
    "id": 3,
    "session_id": 15,
    "sender_id": 5,
    "content": "Merci pour la session, c'était top !",
    "sent_at": "2026-03-05T20:05:00Z"
  }
}
```

**Erreurs :**
- `403 Forbidden` : "Vous n'avez pas accès à cette conversation"
- `400 Bad Request` : "Le message ne peut pas être vide"

---

## 6. Module Administration (Modération)
Actions réservées à l'administrateur.

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/admin/coaches/pending` | Liste des coachs en attente de validation |
| `PATCH` | `/admin/coaches/{id}/approve` | Approuver un nouveau coach |
| `PATCH` | `/admin/coaches/{id}/reject` | Rejeter un profil |
| `GET` | `/admin/dashboard` | Statistiques globales du système |

### Exemples

#### `GET /admin/coaches/pending`
**Headers :**
```
Authorization: Bearer {token}
```

**Réponse (200 OK) :**
```json
{
  "pending_coaches": [
    {
      "id": 10,
      "username": "NewCoach",
      "email": "newcoach@example.com",
      "game_title": "League of Legends",
      "rank": "Challenger",
      "proof_image": "data:image/png;base64,iVBORw0KG...",
      "created_at": "2026-02-27T10:00:00Z"
    }
  ],
  "total": 1
}
```

**Erreurs :**
- `403 Forbidden` : "Accès réservé aux administrateurs"

---

#### `PATCH /admin/coaches/{id}/approve`
**Headers :**
```
Authorization: Bearer {token}
```

**Réponse (200 OK) :**
```json
{
  "message": "Coach approuvé avec succès",
  "coach": {
    "id": 10,
    "username": "NewCoach",
    "is_approved": true,
    "updated_at": "2026-02-28T16:45:00Z"
  }
}
```

---

#### `PATCH /admin/coaches/{id}/reject`
**Headers :**
```
Authorization: Bearer {token}
```

**Réponse (200 OK) :**
```json
{
  "message": "Coach rejeté",
  "coach_id": 10
}
```

---

#### `GET /admin/dashboard`
**Headers :**
```
Authorization: Bearer {token}
```

**Réponse (200 OK) :**
```json
{
  "statistics": {
    "total_users": 152,
    "total_players": 120,
    "total_coaches": 30,
    "approved_coaches": 25,
    "pending_coaches": 5,
    "total_sessions": 89,
    "completed_sessions": 67,
    "total_revenue": 3450.00
  }
}
```

---

## Codes d'Erreur Standards

| Code | Signification | Utilisation |
|------|---------------|-------------|
| `200 OK` | Succès | Requête GET ou modification réussie |
| `201 Created` | Ressource créée | POST réussi (création) |
| `400 Bad Request` | Requête invalide | Données manquantes ou invalides |
| `401 Unauthorized` | Non authentifié | Token manquant, expiré ou invalide |
| `403 Forbidden` | Accès refusé | Permissions insuffisantes |
| `404 Not Found` | Ressource introuvable | ID inexistant |
| `500 Internal Server Error` | Erreur serveur | Erreur inattendue côté serveur |

---

# Structure de Base de Données

## 1. Table `Users` (Utilisateurs)
Gère l'accès de base pour tous les rôles.

| Colonne | Type | Description |
|---------|------|-------------|
| `id` | Clé primaire | Identifiant unique de l'utilisateur |
| `email` | String (unique) | Email de connexion |
| `password` | String (hash) | Mot de passe chiffré (bcrypt) |
| `username` | String | Nom d'utilisateur/pseudo |
| `role` | Enum (`JOUEUR`, `COACH`, `ADMIN`) | Rôle de l'utilisateur |
| `is_approved` | Booléen (Défaut: `false`) | Validation admin (uniquement pour COACH) |
| `created_at` | Timestamp | Date d'inscription |
| `updated_at` | Timestamp | Date de dernière modification |

## 2. Table `Coach_Profiles`
Détails spécifiques aux fournisseurs de service.

| Colonne | Type | Description |
|---------|------|-------------|
| `id` | Clé primaire | Identifiant unique du profil coach |
| `user_id` | Clé étrangère (vers `Users`) | Référence à l'utilisateur |
| `game_title` | String | Nom du jeu (ex: Valorant, League of Legends) |
| `rank` | String | Niveau actuel (ex: Immortal, Diamond) |
| `bio` | Text | Description du coach (max 500 caractères) |
| `hourly_rate` | Decimal(10,2) | Prix par heure défini librement par le coach |
| `proof_image` | Text (Base64) | Image preuve coach (max 5MB), encodée Base64 (JPG/PNG), uploadée en création profil |
| `created_at` | Timestamp | Date de création du profil |
| `updated_at` | Timestamp | Date de dernière modification |

## 3. Table `Sessions`
Cœur de l'application : gère le cycle de vie du service.

| Colonne | Type | Description |
|---------|------|-------------|
| `id` | Clé primaire | Identifiant unique de la session |
| `player_id` | Clé étrangère (vers `Users`) | Référence au joueur |
| `coach_id` | Clé étrangère (vers `Coach_Profiles`) | Référence au coach |
| `status` | Enum (`REQUESTED`, `ACCEPTED`, `COMPLETED`, `CONFIRMED`, `PAID`) | Statut de la session |
| `duration_hours` | Integer | Durée de la session en heures (1, 2, 3...) |
| `amount` | Decimal(10,2) | Montant total (hourly_rate × duration) |
| `scheduled_at` | Timestamp | Date et heure prévue de la session |
| `created_at` | Timestamp | Date de création de la demande |
| `updated_at` | Timestamp | Date de dernière modification |

## 4. Table `Messages` (Chat)
Stocke la communication en temps réel entre les deux parties.

| Colonne | Type | Description |
|---------|------|-------------|
| `id` | Clé primaire | Identifiant unique du message |
| `session_id` | Clé étrangère (vers `Sessions`) | Référence à la session |
| `sender_id` | Clé étrangère (vers `Users`) | Référence à l'expéditeur |
| `content` | Text | Contenu du message |
| `sent_at` | Timestamp | Horodatage d'envoi |

## 5. Table `Reviews`
Évaluations post-service.

| Colonne | Type | Description |
|---------|------|-------------|
| `id` | Clé primaire | Identifiant unique de l'évaluation |
| `session_id` | Clé étrangère (vers `Sessions`) | Référence à la session |
| `rating` | Integer (1 à 5) | Note attribuée |
| `comment` | Text (optionnel) | Texte de l'avis |
| `created_at` | Timestamp | Date de l'évaluation |

---