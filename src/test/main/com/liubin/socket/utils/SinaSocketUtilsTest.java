package com.liubin.socket.utils;

import com.liubin.socket.pojo.SinaSocketInfo;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SinaSocketUtilsTest {

    @Test
    public void testGetSinaSockets() throws Exception {
        List<String> codes = new ArrayList<String>();
        codes.add("sh601006");
        codes.add("sz300276");
        codes.add("sz300353");
        List<SinaSocketInfo> sinaSocketInfoList = SinaSocketUtils.getSinaSockets(codes);
        assertNotNull(sinaSocketInfoList);
    }
}