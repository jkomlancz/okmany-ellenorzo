package com.komlancz.idomsoft.test.okmanyellenorzo.segito.entitas;

import org.apache.commons.lang3.StringUtils;

public class Okmanytipus {

    private static final String SZEMELYI_IG_TIPUS = "1";
    private static final String UTLEVEL_TIPUS = "2";
    private static final String VEZETOI_ENG_TIPUS = "3";

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
            case SZEMELYI_IG_TIPUS:
                return szemelyiszamEllenorzes(okmanyszam);

            case UTLEVEL_TIPUS:
                return utlevelszamEllenorzes(okmanyszam);

            // Nem volt hozza definicio, minek kell megfelelniuk a 4, 5, 6 kodu okmanyoknak, ezert kozosen kezeltem oket
            default:
                return egyebSzamEllenorzes(okmanyszam);
        }
    }

    private String egyebSzamEllenorzes(String okmanyszam){

        if (okmanyszam.length() != 10){

            return String.format("Nem megfelelő %s karakterhossz! (%s)", this.ertek, okmanyszam.length());
        }
        return "";
    }

    private String utlevelszamEllenorzes(String okmanyszam){

        if (okmanyszam.length() != 9){
            return String.format("Nem megfelelő %s karakterhossz! (%s)", this.ertek, okmanyszam.length());
        }

        char[] elsoKetKarakter = okmanyszam.substring(0, 2).toCharArray();
        String utolsoHetKarakter = okmanyszam.substring(2, 9);

        if (Character.isDigit(elsoKetKarakter[0]) ||
                Character.isDigit(elsoKetKarakter[1])||
                !StringUtils.isNumeric(utolsoHetKarakter))
        {
            return String.format("Invalid %s!: %s", this.ertek, okmanyszam);
        }
        return "";
    }

    private String szemelyiszamEllenorzes(String okmanyszam){

        if (okmanyszam.length() != 8){
            return String.format("Nem megfelelő %s karakterhossz! (%s)", this.ertek, okmanyszam.length());
        }
        String elsoHatKarakter = okmanyszam.substring(0, 6);
        char[] utolsoKetKArakter = okmanyszam.substring(6, 8).toCharArray();

        if (!StringUtils.isNumeric(elsoHatKarakter) ||
                elsoHatKarakter.length() != 6 ||
                Character.isDigit(utolsoKetKArakter[0]) ||
                Character.isDigit(utolsoKetKArakter[1]))
        {
            return String.format("Invalid %s!: %s", this.ertek, okmanyszam);
        }
        return "";
    }
}
