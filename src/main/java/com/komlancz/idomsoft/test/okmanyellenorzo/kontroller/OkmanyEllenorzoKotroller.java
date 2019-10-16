package com.komlancz.idomsoft.test.okmanyellenorzo.kontroller;

import com.komlancz.idomsoft.test.okmanyellenorzo.model.OkmanyDTO;
import com.komlancz.idomsoft.test.okmanyellenorzo.model.Valasz;
import com.komlancz.idomsoft.test.okmanyellenorzo.segito.ValidatorBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OkmanyEllenorzoKotroller {

    @Autowired
    ValidatorBean validatorBean;

    @PostMapping(path = "/okmany-ellenorzes")
    public Valasz okmanyokEllenorzese(@RequestBody List<OkmanyDTO> okmanyok){
        Valasz valasz = new Valasz();

        return valasz;
    }
}
