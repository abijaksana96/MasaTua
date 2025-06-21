package com.example.masatua.models;

import java.io.Serializable;

public class Goal implements Serializable {
    private String namaGoals;
    private double targetDana;
    private double danaSekarang;
    private int tahunTarget;
    private double invest;
    private double returnInvest;
    private String deskripsi;

    public Goal() {}

    public Goal(String namaGoals, double targetDana, double danaSekarang, int tahunTarget, double invest, double returnInvest, String deskripsi) {
        this.namaGoals = namaGoals;
        this.targetDana = targetDana;
        this.danaSekarang = danaSekarang;
        this.tahunTarget = tahunTarget;
        this.invest = invest;
        this.returnInvest = returnInvest;
        this.deskripsi = deskripsi;
    }

    public String getNamaGoals() {
        return namaGoals;
    }

    public void setNamaGoals(String namaGoals) {
        this.namaGoals = namaGoals;
    }

    public double getTargetDana() {
        return targetDana;
    }

    public void setTargetDana(double targetDana) {
        this.targetDana = targetDana;
    }

    public double getDanaSekarang() {
        return danaSekarang;
    }

    public void setDanaSekarang(double danaSekarang) {
        this.danaSekarang = danaSekarang;
    }

    public int getTahunTarget() {
        return tahunTarget;
    }

    public void setTahunTarget(int tahunTarget) {
        this.tahunTarget = tahunTarget;
    }

    public double getInvest() {
        return invest;
    }

    public void setInvest(double invest) {
        this.invest = invest;
    }

    public double getReturnInvest() {
        return returnInvest;
    }

    public void setReturnInvest(double returnInvest) {
        this.returnInvest = returnInvest;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public int getProgres() {
        if (targetDana <= 0) return 0;
        int progress = (int) ((danaSekarang / targetDana) * 100);
        return Math.min(progress, 100); // batasi max 100%
    }
}
