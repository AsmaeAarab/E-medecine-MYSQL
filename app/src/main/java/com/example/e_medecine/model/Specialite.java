package com.example.e_medecine.model;

import java.io.Serializable;

public  class Specialite implements Serializable {
    private int id_specialite;
    private String labe;
    private Integer imageSpecialite;

    public Specialite() {
    }
    public Specialite(int id_specialite, String labe, Integer imageSpecialite) {
        this.id_specialite = id_specialite;
        this.labe = labe;
        this.imageSpecialite = imageSpecialite;
    }

    public Integer getImageSpecialite() {
        return imageSpecialite;
    }

    public void setImageSpecialite(Integer imageSpecialite) {
        this.imageSpecialite = imageSpecialite;
    }

    public int getId_specialite() {
        return id_specialite;
    }

    public void setId_specialite(Integer id_specialite) {
        this.id_specialite = id_specialite;
    }

    public String getLabe() {
        return labe;
    }

    public void setLabe(String labe) {
        this.labe = labe;
    }

}
