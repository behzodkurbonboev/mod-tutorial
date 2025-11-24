package tutorial.modtutorial.service;

import tutorial.modtutorial.domain.dto.sms.SMSDTO;


public interface SMSService {
    String send(SMSDTO dto);
}
