package com.komlancz.idomsoft.test.okmanyellenorzo.segito;

import com.komlancz.idomsoft.test.okmanyellenorzo.model.OkmanyDTO;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class ValidatorBeanImplTest {

    private static ValidatorBeanImpl validatorBean;
    private static List<OkmanyDTO> okmanyok;
    private static Calendar calendar;
    private static List<String> hibak;
    private static byte[] jokepHash;
    private static byte[] rosszkepHash;
    private static Date ERVENYES_DATUM;
    private static final String VALID_UTL_OKM_SZAM = "UT7777777";
    private static final String INVALID_UTL_OKM_SZAM = "88ABCDEFG";

    private static final String SZ_IG_NEV= "Személyazonosító igazolvány";
    private static final String UTL_NEV= "Útlevél";
    private static final String VEZ_ENG_NEV= "Vezetői engedély";

    @BeforeClass
    public static void init() throws IOException {
        validatorBean = new ValidatorBeanImpl();

        calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        ERVENYES_DATUM = ervenyesDatum();
    }

    @Before
    public void beforeTest() throws IOException {

        kepInit();

        OkmanyDTO okmanyDTO = new OkmanyDTO();
        okmanyDTO.setLejarDat(ERVENYES_DATUM);
        okmanyDTO.setOkmTipus("1");
        okmanyDTO.setOkmanySzam("777999SA");
        okmanyDTO.setOkmanyKep(jokepHash);

        okmanyok = new ArrayList<>();
        okmanyok.add(okmanyDTO);
    }

    @Test
    public void mindenOkTest() throws IOException {

        // When
        hibak = validatorBean.ellenorzes(okmanyok);

        // Then
        assertTrue(hibak.isEmpty());
        assertTrue(okmanyok.get(0).isErvenyes());
    }

    @Test public void okmanytipusTobbKarakterTest() throws IOException {
        // Given
        okmanyok.get(0).setOkmTipus("12345");

        // When
        hibak = validatorBean.ellenorzes(okmanyok);

        // Then
        assertEquals(1, hibak.size());
        assertTrue(hibak.get(0).contains("Okmánytípus túl hosszú"));
    }

    @Test public void ismeretlenOkmanyTipusTest() throws IOException {
        // Given
        okmanyok.get(0).setOkmTipus("9");

        // When
        hibak = validatorBean.ellenorzes(okmanyok);

        // Then
        assertEquals(1, hibak.size());
        assertTrue(hibak.get(0).contains("Nem ismert okmanytipus!"));
    }

    // ********************** SZEMELYI IGAZOLVANY ADATOK **********************

    @Test public void validSzIgOkmanySzamTest() throws IOException {
        // Given
        okmanyok.get(0).setOkmTipus("1");

        // When
        hibak = validatorBean.ellenorzes(okmanyok);

        // Then
        assertEquals(0, hibak.size());
    }

    @Test public void invalidSzIgOkmanySzamHosszTest() throws IOException {
        // Given
        okmanyok.get(0).setOkmTipus("1");
        okmanyok.get(0).setOkmanySzam("999999999");

        // When
        hibak = validatorBean.ellenorzes(okmanyok);

        // Then
        assertEquals(1, hibak.size());
        assertTrue(hibak.get(0).contains(String.format("Nem megfelelő %s karakterhossz", SZ_IG_NEV)));
    }

    @Test public void invalidSzIgOkmanySzamTest() throws IOException {
        // Given
        okmanyok.get(0).setOkmTipus("1");
        String rosszOkmanyszam = "88888888";
        okmanyok.get(0).setOkmanySzam(rosszOkmanyszam);

        // When
        hibak = validatorBean.ellenorzes(okmanyok);

        // Then
        assertEquals(1, hibak.size());
        assertTrue(hibak.get(0).contains(String.format("Invalid %s!", SZ_IG_NEV)));
    }

    // ********************** UTLEVEL ADATOK **********************

    @Test public void validUTLOkmanySzamTest() throws IOException {
        // Given
        okmanyok.get(0).setOkmTipus("2");
        okmanyok.get(0).setOkmanySzam(VALID_UTL_OKM_SZAM);

        // When
        hibak = validatorBean.ellenorzes(okmanyok);

        // Then
        assertEquals(0, hibak.size());
    }

    @Test public void invalidUTLOkmanySzamHosszTest() throws IOException {
        // Given
        okmanyok.get(0).setOkmTipus("2");
        okmanyok.get(0).setOkmanySzam("9999999990");

        // When
        hibak = validatorBean.ellenorzes(okmanyok);

        // Then
        assertEquals(1, hibak.size());
        assertTrue(hibak.get(0).contains(String.format("Nem megfelelő %s karakterhossz", UTL_NEV)));
    }

    @Test public void invalidUTLOkmanySzamTest() throws IOException {
        // Given
        okmanyok.get(0).setOkmTipus("2");
        okmanyok.get(0).setOkmanySzam(INVALID_UTL_OKM_SZAM);

        // When
        hibak = validatorBean.ellenorzes(okmanyok);

        // Then
        assertEquals(1, hibak.size());
        assertTrue(hibak.get(0).contains(String.format("Invalid %s!", UTL_NEV)));
    }

    // ********************** VEZ ENG ES EGYEB IGAZOLVANY ADATOK **********************

    @Test public void validVezEngOkmanySzamTest() throws IOException {
        // Given
        okmanyok.get(0).setOkmTipus("3");
        okmanyok.get(0).setOkmanySzam("1111111111");

        // When
        hibak = validatorBean.ellenorzes(okmanyok);

        // Then
        assertEquals(0, hibak.size());
    }

    @Test public void invalidVezEngOkmanySzamHosszTest() throws IOException {
        // Given
        okmanyok.get(0).setOkmTipus("3");
        okmanyok.get(0).setOkmanySzam("123");

        // When
        hibak = validatorBean.ellenorzes(okmanyok);

        // Then
        assertEquals(1, hibak.size());
        assertTrue(hibak.get(0).contains(String.format("Nem megfelelő %s karakterhossz", VEZ_ENG_NEV)));
    }

    @Test public void invalidOkmanyKepTest() throws IOException {
        // Given
        okmanyok.get(0).setOkmTipus("1");
        okmanyok.get(0).setOkmanyKep(rosszkepHash);

        // When
        hibak = validatorBean.ellenorzes(okmanyok);

        // Then
        assertEquals(1, hibak.size());
        assertTrue(hibak.get(0).contains("kép mérete nem megfelelő!"));
    }

    @Test public void okmanytipusNullTest() throws IOException {
        // Given
        okmanyok.get(0).setOkmTipus(null);

        // When
        hibak = validatorBean.ellenorzes(okmanyok);

        // Then
        assertEquals(1, hibak.size());
        assertTrue(hibak.get(0).contains("Okmánytípus üres!"));
    }

    @Test public void okmanyLejartTest() throws IOException {
        // Given
        okmanyok.get(0).setOkmTipus("1");
        okmanyok.get(0).setLejarDat(ervenytelenDatum());

        // When
        hibak = validatorBean.ellenorzes(okmanyok);

        // Then
        assertFalse(okmanyok.get(0).isErvenyes());
        assertFalse(okmanyok.get(0).isErvenyes());
    }

    private void kepInit() throws IOException {
        File fajl = new ClassPathResource("test/arckep_jo.jpg", this.getClass().getClassLoader()).getFile();
        jokepHash = Files.readAllBytes(fajl.toPath());

        fajl = new ClassPathResource("test/arckep_rossz.jpg", this.getClass().getClassLoader()).getFile();
        rosszkepHash = Files.readAllBytes(fajl.toPath());
    }

    private static Date ervenyesDatum(){
        calendar.add(Calendar.HOUR, 32);
        return calendar.getTime();
    }

    private Date ervenytelenDatum(){
        calendar.add(Calendar.YEAR, -2);
        return calendar.getTime();
    }
}