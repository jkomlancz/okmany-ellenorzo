package com.komlancz.idomsoft.test.okmanyellenorzo.segito.entitas;

import org.apache.commons.lang3.StringUtils;

public class Okmanytipus {

    private static final String SZEMELYI_IGAZOLVANY_TIPUS = "1";

    private String kod;
    private String ertek;

    public Okmanytipus() {
    }

    public Okmanytipus(String kod, String ertek) {
        this.kod = kod;
        this.ertek = ertek;
    }

    public String getKod() {
        return kod;
    }

    public void setKod(String kod) {
        this.kod = kod;
    }

    public String getErtek() {
        return ertek;
    }

    public void setErtek(String ertek) {
        this.ertek = ertek;
    }

    public String okmanyszamEllenorzes(String okmanyszam){

        switch (this.kod){
            case SZEMELYI_IGAZOLVANY_TIPUS:
                return szemelyiszamEllenorzes(okmanyszam);
        }

        return "";
    }

    private String szemelyiszamEllenorzes(String okmanyszam){
        if (okmanyszam.length() != 8){
            return String.format("Nem megfelelő személyiszám karakterhossz! (%s)", okmanyszam.length());
        }
        else {
            String elsoHatKarakter = okmanyszam.substring(0, 6);
            String utolsoKetKArakter = okmanyszam.substring(6, 8);

            if (!StringUtils.isNumeric(elsoHatKarakter) ||
                    elsoHatKarakter.length() != 6 ||
                    StringUtils.isNumeric(utolsoKetKArakter) ||
                    utolsoKetKArakter.length() != 2
            ){
                return "Invalid szemelyi igazolvany szam!: " + okmanyszam;
            }
        }
        return "";
    }
}
