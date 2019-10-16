package com.komlancz.idomsoft.test.okmanyellenorzo.kontroller;

import com.komlancz.idomsoft.test.okmanyellenorzo.model.OkmanyDTO;
import com.komlancz.idomsoft.test.okmanyellenorzo.model.Valasz;
import com.komlancz.idomsoft.test.okmanyellenorzo.segito.ValidatorBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class OkmanyEllenorzoKotroller {

    @Autowired
    ValidatorBean validatorBean;

    @PostMapping(path = "/okmany-ellenorzes")
    public Valasz okmanyokEllenorzese(@RequestBody List<OkmanyDTO> okmanyok){

        Valasz valasz = new Valasz();
        valasz.setUzenet("Okmanyok ellenorzese");

        try {
            valasz.setHibak(validatorBean.ellenorzes(okmanyok));
            valasz.setOkmanyok(okmanyok);
            valasz.setHttpStatus(HttpStatus.OK);
        } catch (IOException e) {
            valasz.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return valasz;
    }
}
