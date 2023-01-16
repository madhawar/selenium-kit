package tests;

import org.testng.annotations.Test;
import pages.SampathBankEX;
import services.Engine;

import java.io.IOException;

public class SampathBank extends Engine {

    @Test
    public void ex_rate() throws IOException {
        SampathBankEX sampathBankEX = new SampathBankEX(driver);

        sampathBankEX.sampath_bank_uk_gbp_tt_buying_rate();
    }

}
