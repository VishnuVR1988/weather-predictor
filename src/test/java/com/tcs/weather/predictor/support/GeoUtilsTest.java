package com.tcs.weather.predictor.support;

import com.tcs.weather.predictor.dto.Geocode;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.*;


/**
 * This class contains test method for GeoUtils
 * @author Vishnu
 */

public class GeoUtilsTest {


    @Test
    public void testBuildElevationUrl () {
        assertEquals("Failed to return elevation url", "https://maps.googleapis.com/maps/api/elevation/json?locations=-33.8688197,151.2092955"
                , GeoUtils.buildElevationUrl(-33.8688197, 151.2092955));
    }

    @Test
    public void testGetLatLngForAddr () throws IOException {
        double[] latLngArray = {-33.8688197, 151.2092955};
        assertTrue("LatLng is not matching", Arrays.equals(latLngArray, GeoUtils.getLatLngForAddr("sydney")));
    }

    @Test
    public void testGetLatLngForAddrIsNotValid () throws IOException {
        double[] latLngArray = {0, 0};
        assertTrue("LatLng is not matching", !Arrays.equals(latLngArray, GeoUtils.getLatLngForAddr("sydney")));
    }


    @Test
    public void testGetElevationForAddr(){
        String addr = GeoUtils.buildElevationUrl(-33.8688197, 151.2092955);
        assertEquals("Failed to return elevation",24.5399284362793,GeoUtils.getElevationForAddr(addr));
    }

    @Test
    public void testGetElevationForAddrIsNotValid(){
        String addr = GeoUtils.buildElevationUrl(-33.8688197, 151.2092955);
        assertNotEquals("Failed to return elevation",0,GeoUtils.getElevationForAddr(addr));
    }


    @Test
    public void testSendGetRequest(){
        String addr = GeoUtils.buildElevationUrl(-33.8688197, 151.2092955);
        assertNotNull("Get response is null",GeoUtils.sendGetRequest(addr));
    }


    @Test
    public void testGetLatLongAlt() throws IOException{
        Geocode geocode = new Geocode();
        geocode.setAltitude(24.5399284362793);
        geocode.setLatitude(-33.8688197);
        geocode.setLongitude(151.2092955);
        assertEquals("Failed to return geocode",geocode,GeoUtils.getLatLongAlt("sydney"));
    }
}
