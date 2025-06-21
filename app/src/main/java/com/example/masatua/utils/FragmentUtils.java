package com.example.masatua.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentUtils {

    /**
     * Menambahkan sebuah Fragment ke container yang ditentukan.
     * Fungsi ini ideal untuk menambahkan Fragment baru di atas yang sudah ada,
     * atau untuk transisi yang tidak mengharuskan Fragment lama dihancurkan (misalnya, Bottom Navigation Bar).
     *
     * @param fragmentManager FragmentManager dari Activity atau Fragment induk.
     * @param fragment        Instance Fragment yang ingin ditambahkan.
     * @param containerId     ID dari ViewGroup (misalnya FrameLayout atau FragmentContainerView) di layout XML yang akan menampung Fragment.
     * @param tag             Opsional: String tag untuk Fragment ini. Berguna untuk mencari Fragment ini nanti atau untuk back stack. Bisa null.
     * @param addToBackStack  True jika Fragment ingin ditambahkan ke back stack (agar bisa kembali dengan tombol back), false sebaliknya.
     */
    public static void addFragment(FragmentManager fragmentManager, Fragment fragment, int containerId, String tag, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Opsional: Menambahkan animasi transisi antar Fragment
        // Anda bisa menyesuaikan animasi ini sesuai kebutuhan desain Anda
//        fragmentTransaction.setCustomAnimations(
//                android.R.anim.fade_in,     // Animasi saat fragment baru masuk
//                android.R.anim.fade_out,    // Animasi saat fragment lama keluar (jika diganti)
//                android.R.anim.fade_in,     // Animasi saat fragment masuk kembali dari back stack
//                android.R.anim.fade_out     // Animasi saat fragment keluar dari back stack
//        );

        // Menambahkan Fragment ke container
        fragmentTransaction.add(containerId, fragment, tag);

        // Menambahkan ke back stack (opsional)
        // Jika ditambahkan, menekan tombol kembali akan memunculkan fragment sebelumnya.
        // Jika tidak, menekan tombol kembali akan keluar dari activity (jika tidak ada fragment lain di back stack).
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(tag); // Menggunakan tag yang sama untuk back stack
        }

        // Commit transaksi
        fragmentTransaction.commit();
    }
}