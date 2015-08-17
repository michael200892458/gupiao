package com.liubin.socket.utils;

import com.liubin.socket.pojo.SinaSocketInfo;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class SinaSocketInfoParserTest {

    @Test
    public void testParseSinaSocketInfoList() throws Exception {
        String content = "var hq_str_sh601006=\"大秦铁路,10.99,10.98,10.90,11.03,10.70,10.89,10.90,75667797,820841225,130407,10.89,187869,10.88,7600,10.87,7000,10.86,136900,10.85,803500,10.90,112531,10.91,136610,10.92,181000,10.93,121500,10.94,2015-08-14,15:04:10,00\";\n" +
                "var hq_str_sz300276=\"三丰智能,34.900,34.740,34.290,35.500,34.120,34.280,34.290,18483319,642483686.750,20000,34.280,2200,34.270,7700,34.260,20900,34.250,1200,34.240,65804,34.290,32300,34.300,1100,34.320,10200,34.330,3800,34.340,2015-08-14,15:05:33,00\";\n" +
                "var hq_str_sz300353=\"东土科技,0.000,76.220,0.000,0.000,0,0.000,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,2015-08-14,15:05:33,03\";";
        List<SinaSocketInfo> sinaSocketInfoList = SinaSocketInfoParser.parseSinaSocketInfoList(content);
        assertNotNull(sinaSocketInfoList);
    }
}