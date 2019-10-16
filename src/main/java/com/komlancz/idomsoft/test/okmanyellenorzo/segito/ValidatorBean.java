package com.komlancz.idomsoft.test.okmanyellenorzo.segito;

import com.komlancz.idomsoft.test.okmanyellenorzo.model.OkmanyDTO;

import java.io.IOException;
import java.util.List;

public interface ValidatorBean {
    List<String> ellenorzes(List<OkmanyDTO> okmanyok) throws IOException;
}
