package com.tcs.weather.predictor.support;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.*;

import com.tcs.weather.predictor.constants.Constants;
import com.tcs.weather.predictor.dto.Geocode;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * This class consists collection of static methods for generating geo code
 * @author Vishnu
 * @version 1.0.0
 * @since 1.0.0
 */

public class GeoUtils {

    private static final Logger logger = LoggerFactory.getLogger(GeoUtils.class);

    /**
     * Google elevation api response codes
     */
    private enum GoogleElevationStatus {
        OK, INVALID_REQUEST, OVER_QUERY_LIMIT,
        REQUEST_DENIED, UNKNOWN_ERROR
    }


    /**
     * Extracts the latitude and longitude using google map services api
     *
     * @param stationName -
     * @return lat-long array
     * @throws IOException
     */
    private static double[] getLatLngForAddr ( final String stationName ) throws IOException {
        if (stationName == null) return ArrayUtils.EMPTY_DOUBLE_ARRAY;
        logger.debug(" LatLng Address is {}", stationName);
        final Geocoder geocoder = new Geocoder();
        final GeocoderRequest geocoderRequest;
        final GeocodeResponse geocoderResponse;
        geocoderRequest = new GeocoderRequestBuilder()
                .setAddress(stationName)
                .setLanguage("en").getGeocoderRequest();
        geocoderResponse = geocoder.geocode(geocoderRequest);
        if (geocoderResponse != null && geocoderResponse.getStatus() == GeocoderStatus.OK && !geocoderResponse.getResults().isEmpty()) {
            // Get the first result
            GeocoderResult geocoderResult =
                    geocoderResponse.getResults().iterator().next();
            double[] loc = new double[2];
            LatLng ll = geocoderResult.getGeometry().getLocation();
            loc[0] = ll.getLat().doubleValue();
            loc[1] = ll.getLng().doubleValue();
            return loc;

        }
        return ArrayUtils.EMPTY_DOUBLE_ARRAY;
    }

    /**
     * @param addr - google elevation api full address
     * @return - altitude/elevation
     * @throws IOException
     */

    protected static double getElevationForAddr ( final String addr ) {
        double elevation = Double.NaN;
        if (addr == null) return elevation;
        logger.debug("Address is {}", addr);
        final JSONObject jsonObj = new JSONObject(sendGetRequest(addr));
        final GoogleElevationStatus status = GoogleElevationStatus.valueOf(
                jsonObj.optString("status"));
        if (status != GoogleElevationStatus.OK) {
            logger.error(
                    "Error retrieving elevation data. Status returned by Google = {}", status);
            return elevation;
        }
        JSONArray results = jsonObj.getJSONArray("results");
        for (int i = 0; i < results.length(); i++) {
            JSONObject cur = results.getJSONObject(i);
            elevation = cur.optDouble("elevation");
        }
        return elevation;
    }

    /**
     * This method builds the elevation api url
     *
     * @param latitude
     * @param longitude
     * @return google elevation api url
     */

    protected static String buildElevationUrl ( final double latitude, final double longitude ) {
        return String.format(Constants.GEO_ELEVATION_URL, latitude, longitude);
    }


    /**
     * A method to send HTTP Webservice GET request
     *
     * @param url
     * @return
     */
    private static String sendGetRequest ( final String url ) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault();) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (IOException e) {
            logger.info(
                    "Error retrieving response from webservice.");
        }
        return null;
    }


    /**
     * This method returns the geocode of a location by hitting google api
     *
     * @param stationName
     * @return Geocode
     */

    public static Geocode getLatLongAlt ( final String stationName ) throws IOException {

        final double[] latLngForAddr = getLatLngForAddr(stationName);
        double altitude = Double.NaN;

        if (ArrayUtils.isNotEmpty(latLngForAddr)) {
            altitude = getElevationForAddr(buildElevationUrl(latLngForAddr[0], latLngForAddr[1]));
        }

        Geocode geocode = new Geocode();
        geocode.setAltitude(altitude);
        geocode.setLatitude(latLngForAddr[0]);
        geocode.setLatitude(latLngForAddr[1]);
        return geocode;
    }


}