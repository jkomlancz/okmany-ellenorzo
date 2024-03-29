package com.komlancz.idomsoft.test.okmanyellenorzo.segito;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.github.fge.jackson.JsonLoader;

import com.komlancz.idomsoft.test.okmanyellenorzo.model.OkmanyDTO;
import com.komlancz.idomsoft.test.okmanyellenorzo.segito.entitas.Okmanytipus;

import org.apache.commons.lang3.StringUtils;

import org.springframework.core.io.ClassPathResource;

import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.URLConnection;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

public class ValidatorBeanImpl implements ValidatorBean
{

    private static final String OKMANY_TIPUS_KODSZOTAR_FAJLNEV = "kodszotar46_okmanytipus";
    private static final String OKMANYKEP_ELFOGADOTT_KITERJESZTES = "image/jpeg";
    private static final int OKMANYKEP_MAX_MAGASSAG = 1063;
    private static final int OKMANYKEP_MAX_SZELESSEG = 827;

    @Override public List<String> ellenorzes(List<OkmanyDTO> okmanyok) throws IOException
    {
        List<String> hibak = new ArrayList<>();
        List<String> ervenyesOkmanytipusAzonositok = new ArrayList<>();

        for (OkmanyDTO okmany : okmanyok)
        {

            // okmany tipusat rogzitjuk ezzel elerve a pontos nevet
            Okmanytipus okmanytipus = okmanyTipusEllenorzes(hibak, okmany.getOkmTipus(), okmany.getOkmanySzam());
            okmanyKepMeretEllenorzes(hibak, okmany.getOkmanyKep(), okmanytipus.getErtek());
            okmanyErvenyessegEllenorzes(ervenyesOkmanytipusAzonositok, hibak, okmanytipus.getErtek(), okmany);
        }

        return hibak;
    }

    private JsonNode kodszotarFelolvasasa(String fajlNev) throws IOException
    {
        if (!fajlNev.endsWith(".json"))
        {
            fajlNev += ".json";
        }

        File fajl = new ClassPathResource(fajlNev, this.getClass().getClassLoader()).getFile();

        return JsonLoader.fromFile(fajl);
    }

    private void okmanyErvenyessegEllenorzes(List<String> ervenyesOkmanytipusAzonositok, List<String> hibak, String okmanytipus,
        OkmanyDTO okmany)
    {

        if (okmany.getLejarDat() == null)
        {
            hibak.add(String.format("%s lejárati dátuma üres!", okmanytipus));
        }
        else
        {
            if (new Date().after(okmany.getLejarDat()))
            {
                okmany.setErvenyes(false);
                hibak.add(String.format("%s érvényessége lejárt ekkor: %s", okmanytipus, okmany.getLejarDat()));
            }
            else
            {
                if (ervenyesOkmanytipusAzonositok.contains(okmany.getOkmTipus()))
                {
                    hibak.add(String.format("%s okmanytipusbol több érvényes is érkezett, de csak 1 lehet!", okmanytipus));
                }
                else
                {
                    okmany.setErvenyes(true);
                    ervenyesOkmanytipusAzonositok.add(okmany.getOkmTipus());
                }
            }
        }
    }

    private void okmanyKepMeretEllenorzes(List<String> hibak, byte[] kepByte, String okmanytipus) throws IOException
    {
        if ((kepByte != null) && (kepByte.length > 0))
        {
            try
            {
                InputStream in = new ByteArrayInputStream(kepByte);
                BufferedImage foto = ImageIO.read(in);

                String kepKiterjesztese = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(kepByte));

                if (!OKMANYKEP_ELFOGADOTT_KITERJESZTES.equals(kepKiterjesztese))
                {
                    hibak.add(String.format("%s kép formátuma nem megfelelő! Elfogadott formátum: %s! Aktuális kép formátuma: %s",
                            okmanytipus, OKMANYKEP_ELFOGADOTT_KITERJESZTES, kepKiterjesztese));
                }

                if ((foto.getHeight() != OKMANYKEP_MAX_MAGASSAG) || (foto.getWidth() != OKMANYKEP_MAX_SZELESSEG))
                {
                    hibak.add(String.format("%s kép mérete nem megfelelő! Elfogadott méret: %s x %s! Aktuális kép mérete: %s x %s",
                            okmanytipus, OKMANYKEP_MAX_MAGASSAG, OKMANYKEP_MAX_SZELESSEG, foto.getHeight(), foto.getWidth()));
                }

                foto.getWidth();
                foto.getHeight();
            }
            catch (IOException e)
            {
                hibak.add(okmanytipus + " kép feldolgozása sikertelen");
                throw e;
            }
        }
        else
        {
            hibak.add(okmanytipus + "hoz(hez) nem tartozik kép");
        }
    }

    private Okmanytipus okmanyTipusEllenorzes(List<String> hibak, String okmanytipus, String okmanyszam) throws IOException
    {

        if (okmanytipus == null)
        {
            hibak.add("Okmánytípus üres!");
        }
        else if (okmanytipus.length() > 1)
        {
            hibak.add(String.format("Okmánytípus túl hosszú (%s)! Maximum hossz: 1", okmanytipus));
        }
        else
        {
            JsonNode okmanytipusKodSzotar = kodszotarFelolvasasa(OKMANY_TIPUS_KODSZOTAR_FAJLNEV);
            List<Okmanytipus> okmanytipusok = okmanytipusokMappalese(okmanytipusKodSzotar);

            Optional<Okmanytipus> talalat = okmanytipusok.stream().filter(o -> o.getKod().equals(okmanytipus)).findFirst();

            if (talalat.isPresent())
            {
                String talaltHiba = talalat.get().okmanyszamEllenorzes(okmanyszam);

                if (StringUtils.isNotBlank(talaltHiba))
                {
                    hibak.add(talaltHiba);
                }

                return talalat.get();
            }
            else
            {
                hibak.add("Nem ismert okmanytipus!");
            }
        }

        // Abban az esetben, ha az ellenorzés sikertelen
        return new Okmanytipus("-1", "Nem ismert okmanytipus");
    }

    private List<Okmanytipus> okmanytipusokMappalese(JsonNode okmanytipusNode) throws JsonProcessingException
    {
        List<Okmanytipus> okmanytipusok = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        for (JsonNode node : okmanytipusNode.get("rows"))
        {
            Okmanytipus okmanytipusElem = objectMapper.treeToValue(node, Okmanytipus.class);
            okmanytipusok.add(okmanytipusElem);
        }

        return okmanytipusok;
    }
}
