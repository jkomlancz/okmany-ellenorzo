package com.komlancz.idomsoft.test.okmanyellenorzo.segito;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.komlancz.idomsoft.test.okmanyellenorzo.model.OkmanyDTO;
import com.komlancz.idomsoft.test.okmanyellenorzo.segito.entitas.Okmanytipus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ValidatorBeanImpl implements ValidatorBean {

    private static final String OKMANY_TIPUS_KODSZOTAR_FAJLNEV = "kodszotar46_okmanytipus";
    private static final String OKMANYKEP_ELFOGADOTT_KITERJESZTES = "image/jpeg";
    private static final int OKMANYKEP_MAX_MAGASSAG = 1063;
    private static final int OKMANYKEP_MAX_SZELESSEG = 827;

    @Override
    public List<String> ellenorzes(List<OkmanyDTO> okmanyok) throws IOException {
        List<String> hibak = new ArrayList<>();

        for (OkmanyDTO okmany : okmanyok){
            Okmanytipus okmanytipus = okmanyTipusEllenorzes(hibak, okmany.getOkmTipus(), okmany.getOkmanySzam());
            okmanyKepMeretEllenorzes(hibak, okmany.getOkmanyKep(), okmanytipus.getErtek());
        }

        return hibak;
    }

    private void okmanyKepMeretEllenorzes(List<String> hibak, byte[] kepByte, String okmanytipus) throws IOException {
        if (kepByte != null && kepByte.length > 0){
            try {
                InputStream in = new ByteArrayInputStream(kepByte);
                BufferedImage foto = ImageIO.read(in);

                String kepKiterjesztese = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(kepByte));

                if (!OKMANYKEP_ELFOGADOTT_KITERJESZTES.equals(kepKiterjesztese)){
                    hibak.add(String.format("%s kép formátuma nem megfelelő! Elfogadott formátum: %s! Aktuális kép formátuma: %s",
                            okmanytipus, OKMANYKEP_ELFOGADOTT_KITERJESZTES, kepKiterjesztese));
                }

                if (foto.getHeight() != OKMANYKEP_MAX_MAGASSAG || foto.getWidth() != OKMANYKEP_MAX_SZELESSEG){
                    hibak.add(String.format("%s kép mérete nem megfelelő! Elfogadott méret: %s x %s! Aktuális kép mérete: %s x %s",
                            okmanytipus, OKMANYKEP_MAX_MAGASSAG, OKMANYKEP_MAX_SZELESSEG, foto.getHeight(), foto.getWidth()));
                }
                foto.getWidth();
                foto.getHeight();
            } catch (IOException e) {
                hibak.add(okmanytipus + " kép feldolgozása sikertelen");
                throw e;
            }
        }
        else {
            hibak.add(okmanytipus + "hoz(hez) nem tartozik kép");
        }
    }

    private Okmanytipus okmanyTipusEllenorzes(List<String> hibak, String okmanytipus, String okmanyszam) throws IOException {

        JsonNode okmanytipusKodSzotar = kodszotarFelolvasasa(OKMANY_TIPUS_KODSZOTAR_FAJLNEV);
        List<Okmanytipus> okmanytipusok = okmanytipusokMappalese(okmanytipusKodSzotar);

        Optional<Okmanytipus> talalat = okmanytipusok.stream().filter(o -> o.getKod().equals(okmanytipus)).findFirst();

        if (talalat.isPresent()){
            String talaltHiba = talalat.get().okmanyszamEllenorzes(okmanyszam);

            if (StringUtils.isNotBlank(talaltHiba)){
                hibak.add(talaltHiba);
            }
            return talalat.get();
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

    private JsonNode kodszotarFelolvasasa(String fajlNev) throws IOException
    {
        if (!fajlNev.endsWith(".json"))
        {
            fajlNev += ".json";
        }

        File fajl = new ClassPathResource(fajlNev, this.getClass().getClassLoader()).getFile();

        return JsonLoader.fromFile(fajl);
    }
}
