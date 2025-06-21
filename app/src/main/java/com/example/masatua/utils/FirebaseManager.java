package com.example.masatua.utils; // Anda bisa sesuaikan nama package ini

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

/**
 * Singleton class to manage Firebase Authentication and Firestore instances
 * and provide convenient methods for common Firebase operations.
 */
public class FirebaseManager {

    private static FirebaseManager instance; // Instance tunggal dari kelas ini
    private FirebaseAuth mAuth; // Instance untuk Firebase Authentication
    private FirebaseFirestore db; // Instance untuk Cloud Firestore

    // Konstruktor pribadi untuk mencegah instansiasi dari luar kelas (singleton pattern)
    private FirebaseManager() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Mengembalikan satu-satunya instance dari FirebaseManager.
     * Menggunakan 'synchronized' untuk memastikan thread-safe saat instansiasi pertama.
     *
     * @return Instance tunggal dari FirebaseManager.
     */
    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    // --- Metode untuk Akses Instansi Firebase ---

    /**
     * Mendapatkan instance FirebaseAuth.
     *
     * @return Instance FirebaseAuth.
     */
    public FirebaseAuth getAuth() {
        return mAuth;
    }

    /**
     * Mendapatkan instance FirebaseFirestore.
     *
     * @return Instance FirebaseFirestore.
     */
    public FirebaseFirestore getFirestore() {
        return db;
    }

    // --- Metode untuk Autentikasi User ---

    /**
     * Mendapatkan objek FirebaseUser saat ini.
     *
     * @return FirebaseUser objek user yang sedang login, atau null jika tidak ada.
     */
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    /**
     * Mendapatkan UID (User ID) dari user yang sedang login.
     *
     * @return String UID user, atau null jika tidak ada user login.
     */
    public String getCurrentUserId() {
        FirebaseUser currentUser = getCurrentUser();
        return currentUser != null ? currentUser.getUid() : null;
    }

    /**
     * Memeriksa apakah ada user yang sedang login.
     *
     * @return true jika ada user login, false jika tidak.
     */
    public boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }

    /**
     * Melakukan sign out user dari Firebase Authentication.
     */
    public void signOutUser() {
        mAuth.signOut();
    }

    // --- Metode untuk Akses Data Firestore ---

    /**
     * Mendapatkan referensi ke koleksi 'users'.
     *
     * @return CollectionReference ke koleksi 'users'.
     */
    public CollectionReference getUsersCollection() {
        return db.collection("users");
    }

    /**
     * Mendapatkan referensi dokumen untuk user yang sedang login di koleksi 'users'.
     *
     * @return DocumentReference ke dokumen user yang sedang login, atau null jika user tidak login.
     * @throws IllegalStateException jika user tidak login.
     */
    public DocumentReference getCurrentUserDocument() {
        String uid = getCurrentUserId();
        if (uid != null) {
            return getUsersCollection().document(uid);
        }
        throw new IllegalStateException("User is not logged in. Cannot get user's document.");
    }

    /**
     * Mendapatkan referensi ke subkoleksi 'items' di dalam dokumen user yang sedang login.
     * Ini adalah tempat item-item milik user disimpan.
     *
     * @return CollectionReference ke subkoleksi 'items' user, atau null jika user tidak login.
     * @throws IllegalStateException jika user tidak login.
     */
    public CollectionReference getUserGoalsCollection() {
        String uid = getCurrentUserId();
        if (uid != null) {
            return getCurrentUserDocument().collection("goals");
        }
        throw new IllegalStateException("User is not logged in. Cannot get user's items collection.");
    }

    // --- Metode Bantuan Lainnya (Contoh) ---

    /**
     * Mengirim email verifikasi ke user yang baru mendaftar atau login.
     *
     * @param user Objek FirebaseUser yang akan diverifikasi.
     * @return true jika user valid dan email verifikasi dikirim, false jika tidak.
     */
    public boolean sendEmailVerification(FirebaseUser user) {
        if (user != null) {
            user.sendEmailVerification();
            return true;
        }
        return false;
    }

    /**
     * Mengirim email reset password ke alamat email yang diberikan.
     *
     * @param email Alamat email untuk reset password.
     * @return Task<Void> yang bisa digunakan untuk melampirkan listener.
     */
    public com.google.android.gms.tasks.Task<Void> sendPasswordResetEmail(String email) {
        return mAuth.sendPasswordResetEmail(email);
    }
}