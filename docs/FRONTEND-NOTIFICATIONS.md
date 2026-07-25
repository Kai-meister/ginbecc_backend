# Meeting Notifications — Frontend Guide

How to trigger, display, and receive notifications for the Inspectorate Management System.

**Base URL:** `https://ginbecc-backend.onrender.com`
**Auth:** every endpoint below needs `Authorization: Bearer <accessToken>`

---

## TL;DR

| What you want | What to do |
|---|---|
| Notify attendees when a meeting is created | Call `POST /meetings`, then `POST /meetings/{id}/attendees/bulk`. Nothing else. |
| Stop duplicate notifications | **Remove** your manual `POST /api/v1/notifications/bulk` call — the backend already sends it. |
| Show a bell + notification list | `GET /notifications/unread-count` and `GET /notifications/my` |
| Get push notifications in the browser | Firebase JS SDK + service worker + `POST /devices` (see Part C) |

---

## Response envelope

**Every** endpoint returns this wrapper. Your data is always in `data`.

```json
{
  "success": true,
  "message": "ទាញយកបញ្ជីសារជូនដំណឹងរបស់អ្នកបានដោយជោគជ័យ",
  "data": { },
  "timestamp": "2026-07-25T14:00:13.507"
}
```

Paginated endpoints put a page object in `data`:

```json
{
  "content": [ ],
  "pageNumber": 0,
  "pageSize": 20,
  "totalElements": 42,
  "totalPages": 3,
  "first": true,
  "last": false
}
```

Errors use the same envelope with `success: false`, a Khmer `message`, and `data: null`. Show `message` directly — it's already user-facing Khmer.

---

## Authentication

CORS allows all origins, so `localhost` dev works without any backend change.

### Login

```http
POST /api/v1/auth/login
Content-Type: application/json
```

```json
{ "email": "admin@system.kh", "password": "Admin@1234" }
```

`data` returns:

```json
{
  "accessToken": "eyJ...",
  "refreshToken": "eyJ...",
  "tokenType": "Bearer",
  "userId": 1,
  "userNameKh": "...",
  "userNameEn": "...",
  "email": "admin@system.kh",
  "roleName": "SUPER_ADMIN",
  "permissions": ["MEETING_VIEW", "MEETING_MANAGE_ATTENDEES", "..."]
}
```

- **Access token lasts 24 h, refresh token 7 d.**
- `permissions` is the full list for the logged-in user — use it to show/hide UI. Adding attendees needs `MEETING_MANAGE_ATTENDEES`; viewing meetings needs `MEETING_VIEW`.
- 5 failed logins locks the account for 30 minutes.

### Refresh on 401

```http
POST /api/v1/auth/refresh-token
Refresh-Token: <refreshToken>
```

The token goes in the **`Refresh-Token` header** (or `{ "refreshToken": "..." }` in the body). Returns a fresh `LoginResponse`. Wire this into an interceptor that retries once on 401.

### Logout

```http
POST /api/v1/auth/logout
```

Tokens are blacklisted server-side on logout, so they stop working immediately. Also call `DELETE /api/v1/devices` here (Part C, step 4).

Only `/api/v1/auth/**`, Swagger, and `/actuator/health` are public. **Everything else needs the bearer token.**

**Full API reference:** https://ginbecc-backend.onrender.com/swagger-ui.html

---

## Part A — Triggering notifications when a meeting is created

### This is two API calls, not one

`MeetingRequest` has **no attendees field**. Creating a meeting notifies nobody. The notification fires when you **add attendees**.

#### Step 0 — populating the form

You need real IDs for the two dropdowns before you can submit anything.

**Attendee picker** — needs the `USER_VIEW` permission:

```http
GET /api/v1/users?page=0&size=20&keyword=sokha&status=ACTIVE&roleId=3
```

`keyword`, `status`, and `roleId` are all optional. Returns a paginated `UserResponse` list; use `userId` for the `userIds` array in step 2.

**Room dropdown** — needs `MEETING_VIEW` or `ROOM_MANAGE`:

```http
GET /api/v1/meeting-rooms
```

Use `roomId` in step 1. Two extras worth wiring into the form:

```http
GET /api/v1/meeting-rooms/{id}/availability?date=2026-08-01
GET /api/v1/meeting-rooms/{id}/schedule
```

The backend rejects double-booked rooms, so checking availability before submit gives a much better error experience than catching the rejection.

#### Step 1 — create the meeting

```http
POST /api/v1/meetings
Content-Type: application/json
```

```json
{
  "title": "ប្រជុំពិនិត្យរបាយការណ៍",
  "description": "optional, max 1000 chars",
  "meetingType": "INTERNAL",
  "meetingDate": "2026-08-01",
  "startTime": "09:00:00",
  "endTime": "10:30:00",
  "roomId": 3,
  "meetingLink": null,
  "statusCode": "SCHEDULED"
}
```

- `meetingType`: `INTERNAL` | `EXTERNAL` | `ONLINE`
- `statusCode`: `DRAFT` | `SCHEDULED` | `CONFIRMED` | `IN_PROGRESS` | `COMPLETED` | `CANCELLED` | `POSTPONED` | `RESCHEDULED`
- `meetingDate` must be **today or later** — a past date is rejected by validation.

Grab `data.meetingId` from the response.

#### Step 2 — add attendees (this sends the notifications)

```http
POST /api/v1/meetings/{meetingId}/attendees/bulk
Content-Type: application/json
```

```json
{
  "userIds": [17, 18, 20],
  "role": "ATTENDEE"
}
```

- `role`: `ORGANIZER` | `PRESENTER` | `ATTENDEE` | `OBSERVER` (applies to everyone in the list)
- Requires the `MEETING_MANAGE_ATTENDEES` permission.
- Already-added users are skipped silently, so this is safe to retry.

**One bulk call, not a loop.** There's also `POST /meetings/{meetingId}/attendees` for a single attendee, but use `/bulk` when adding several.

That's it. Each attendee automatically gets a notification titled `ការអញ្ជើញប្រជុំ` with body `អញ្ជើញ: {meeting title}`, plus a phone push if they have a device registered.

### ⚠️ Remove your manual notification call

Right now every attendee receives **two** notifications per meeting:

| Title | Source |
|---|---|
| `ការអញ្ជើញប្រជុំ` | Backend, automatic |
| `ការអញ្ជើញចូលរួមកិច្ចប្រជុំ` | A manual `POST /api/v1/notifications/bulk` from the web |

That second title doesn't exist anywhere in the backend source, so it's being sent by the frontend. Confirm before changing anything:

```bash
grep -rn "notifications/bulk\|ការអញ្ជើញចូលរួម" src/
```

Remove whatever that turns up — step 2 already sends the invite. (Diagnosed from production data, not from reading your repo, so verify the grep hits before deleting.)

### Things to know

- The **meeting creator does not get notified** — only the attendees.
- A user only gets a *phone* push if they've registered a device. Users with no registered device still get the in-app notification, just no push.

---

## Part B — Displaying notifications in the web UI

### Bell badge

```http
GET /api/v1/notifications/unread-count
```

`data` is a plain number.

There is **no WebSocket or SSE** on this backend — notifications are not pushed to the web. Poll this endpoint every 30–60 s to keep the badge fresh.

### Notification list

```http
GET /api/v1/notifications/my?type=MEETING&isRead=false&page=0&size=20
```

All query params are optional:

| Param | Values |
|---|---|
| `type` | `MEETING` \| `DOCUMENT` \| `ANNOUNCEMENT` \| `SYSTEM` |
| `isRead` | `true` \| `false` (omit for both) |
| `page` | default `0` |
| `size` | default `20` |

Each item in `data.content`:

```json
{
  "notificationId": 412,
  "title": "ការអញ្ជើញប្រជុំ",
  "message": "អញ្ជើញ: ប្រជុំពិនិត្យរបាយការណ៍",
  "type": "MEETING",
  "typeLabel": "ការប្រជុំ",
  "referenceId": 77,
  "navigatePath": "/meetings/77",
  "isRead": false,
  "createdAt": "2026-07-25T14:00:13.507",
  "readAt": null,
  "referenceData": {
    "meetingId": 77,
    "title": "ប្រជុំពិនិត្យរបាយការណ៍",
    "meetingDate": "01/08/2026"
  }
}
```

Two fields worth using:

- **`navigatePath`** — ready-made route (`/meetings/77`, `/documents/12`, `/announcements/4`, `/profile`). Use it directly for click-through instead of building the URL yourself.
- **`referenceData`** — pre-resolved details so you can render a rich card **without a second API call**. Shape varies by `type`, and it can be `null` if the referenced object was deleted, so guard for that.

### Other endpoints

```http
GET    /api/v1/notifications/{id}       # single notification
PUT    /api/v1/notifications/{id}/read  # mark one read
PUT    /api/v1/notifications/read-all   # mark all read → data = count updated
```

---

## Part C — Browser push notifications (optional)

Part B covers the in-app bell. If you want notifications to appear **outside the tab** (OS-level, tab closed), you need Firebase Cloud Messaging for web.

The backend already supports this — `platform` accepts `WEB`. There are currently zero web devices registered.

You need the config from the **same Firebase project the mobile app uses** — ask for `firebaseConfig` and the **VAPID key**.

### 1. Service worker

`public/firebase-messaging-sw.js` — must be at the web root:

```js
importScripts('https://www.gstatic.com/firebasejs/10.12.0/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/10.12.0/firebase-messaging-compat.js');

firebase.initializeApp({ /* same firebaseConfig */ });
firebase.messaging();
```

### 2. Request permission and register the token

```js
import { initializeApp } from 'firebase/app';
import { getMessaging, getToken, onMessage } from 'firebase/messaging';

const app = initializeApp(firebaseConfig);
const messaging = getMessaging(app);

async function registerForPush(accessToken) {
  const permission = await Notification.requestPermission();
  if (permission !== 'granted') return;

  const token = await getToken(messaging, { vapidKey: VAPID_KEY });
  if (!token) return;

  await fetch(`${BASE_URL}/api/v1/devices`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${accessToken}`,
    },
    body: JSON.stringify({ token, platform: 'WEB' }),
  });
}
```

Call this **after login**, once permission is granted.

### 3. Foreground messages

When the tab is focused the service worker does not fire — handle it yourself:

```js
onMessage(messaging, (payload) => {
  // payload.notification.title / .body
  // payload.data.type, payload.data.path, payload.data.referenceId
  showToast(payload.notification, payload.data.path);
});
```

The `data` payload carries `type`, `path`, and `referenceId` — same values as `navigatePath` / `referenceId` above, so click-through works identically.

### 4. Unregister on logout

```js
await fetch(`${BASE_URL}/api/v1/devices?token=${encodeURIComponent(token)}`, {
  method: 'DELETE',
  headers: { Authorization: `Bearer ${accessToken}` },
});
```

**Please don't skip this.** Skipping it is why the mobile app accumulated 49 stale tokens across 4 users. You can only delete your own tokens — passing someone else's is a silent no-op.

---

## Gotchas

1. **Meeting creation alone notifies nobody.** You must call the attendees endpoint.
2. **No WebSocket/SSE.** Poll `unread-count` for the web bell. Part C is the only way to get real push.
3. **Creator is never notified** — only attendees.
4. **`referenceData` can be null.** Guard before reading it.
5. **`meetingDate` must not be in the past** or meeting creation fails validation.
6. **Khmer text is intentional** — titles and messages come back in Khmer by design.
7. **Free-tier cold starts.** The backend can take ~60 s to wake after idling. Don't treat the first slow request as a failure.
