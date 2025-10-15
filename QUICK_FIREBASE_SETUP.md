# 🚀 Quick Start - Firebase Setup (5 Minutes)

## Step 1: Create Firebase Project
1. Go to: https://console.firebase.google.com/
2. Click "Add project"
3. Name it: `venue-booking-system`
4. Create project

## Step 2: Add Web App
1. Click Web icon `</>`
2. Nickname: `Venue Booking Web`
3. Register app
4. **COPY the firebaseConfig code** (you'll need it!)

## Step 3: Enable Realtime Database
1. Left sidebar → Build → Realtime Database
2. Click "Create Database"
3. Choose location
4. Select "Start in **test mode**"
5. Click Enable

## Step 4: Update Your Code
1. Open `index.html`
2. Find line ~283 with `firebaseConfig`
3. Replace `YOUR_API_KEY`, `YOUR_PROJECT_ID`, etc. with YOUR values
4. Save file

## Step 5: Test It!
1. Open `index.html` in browser
2. Press F12 to open console
3. Look for: `✅ Firebase initialized successfully`
4. Create a booking and check it appears in Firebase Console!

---

## 🔑 What to Replace in index.html

Find this:
```javascript
const firebaseConfig = {
    apiKey: "YOUR_API_KEY",                          // ← Replace this
    authDomain: "YOUR_PROJECT_ID.firebaseapp.com",   // ← Replace this
    databaseURL: "https://YOUR_PROJECT_ID-default-rtdb.firebaseio.com",  // ← Replace this
    projectId: "YOUR_PROJECT_ID",                    // ← Replace this
    storageBucket: "YOUR_PROJECT_ID.appspot.com",    // ← Replace this
    messagingSenderId: "YOUR_MESSAGING_SENDER_ID",   // ← Replace this
    appId: "YOUR_APP_ID"                             // ← Replace this
};
```

With YOUR values from Firebase Console!

---

## ✅ Done!
Your venue booking system now has a **real database**! 🎉

Data will be saved permanently and synced across all devices in real-time!
