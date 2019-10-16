package com.komlancz.idomsoft.test.okmanyellenorzo.segito;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.komlancz.idomsoft.test.okmanyellenorzo.model.OkmanyDTO;
import com.komlancz.idomsoft.test.okmanyellenorzo.segito.entitas.Okmanytipus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ValidatorBeanImpl implements ValidatorBean {

    private static final String OKMANY_TIPUS_KODSZOTAR_FAJLNEV = "kodszotar46_okmanytipus";

    @Override
    public List<String> ellenorzes(List<OkmanyDTO> okmanyok) throws IOException {
        List<String> hibak = new ArrayList<>();

        for (OkmanyDTO okmany : okmanyok){
            okmanyTipusEllenorzes(hibak, okmany.getOkmTipus(), okmany.getOkmanySzam());
        }
        return hibak;
    }

    private void okmanyTipusEllenorzes(List<String> hibak, String okmanytipus, String okmanyszam) throws IOException {

        JsonNode okmanytipusKodSzotar = kodszotarFelolvasasa(OKMANY_TIPUS_KODSZOTAR_FAJLNEV);
        List<Okmanytipus> okmanytipusok = okmanytipusokMappalese(okmanytipusKodSzotar);

        Optional<Okmanytipus> talalat = okmanytipusok.stream().filter(o -> o.getKod().equals(okmanytipus)).findFirst();

        if (talalat.isPresent()){
            String eredmeny = talalat.get().okmanyszamEllenorzes(okmanyszam);

            if (StringUtils.isNotBlank(eredmeny)){
                hibak.add(eredmeny);
            }
        }
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
