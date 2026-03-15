package com.kitchen.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 高德地图工具类
 * 用于经纬度逆地理编码，将坐标转换为中文地址
 */
@Slf4j
@Component
public class GaodeMapUtil {

    @Value("${gaode.key:}")
    private String gaodeKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String RE_GEO_URL = "https://restapi.amap.com/v3/geocode/regeo";

    /**
     * 根据经纬度获取详细地址
     * 
     * @param longitude 经度
     * @param latitude 纬度
     * @return 中文地址，失败时返回null
     */
    public String getAddressByLocation(Double longitude, Double latitude) {
        if (gaodeKey == null || gaodeKey.isEmpty()) {
            log.warn("高德地图Key未配置，无法获取地址");
            return null;
        }

        try {
            String url = UriComponentsBuilder.fromHttpUrl(RE_GEO_URL)
                    .queryParam("key", gaodeKey)
                    .queryParam("location", longitude + "," + latitude)
                    .queryParam("extensions", "base")
                    .queryParam("output", "JSON")
                    .toUriString();

            log.info("请求高德地图API: {}", url);
            
            String response = restTemplate.getForObject(url, String.class);
            log.info("高德地图响应: {}", response);

            JsonNode root = objectMapper.readTree(response);
            
            if ("1".equals(root.path("status").asText())) {
                JsonNode regeocode = root.path("regeocode");
                String formattedAddress = regeocode.path("formatted_address").asText();
                return formattedAddress;
            } else {
                log.error("高德地图API返回错误: {}", root.path("info").asText());
                return null;
            }
        } catch (Exception e) {
            log.error("调用高德地图API失败", e);
            return null;
        }
    }

    /**
     * 根据经纬度获取详细地址信息（包含省市区等）
     * 
     * @param longitude 经度
     * @param latitude 纬度
     * @return 地址信息对象
     */
    public AddressInfo getDetailedAddress(Double longitude, Double latitude) {
        if (gaodeKey == null || gaodeKey.isEmpty()) {
            log.warn("高德地图Key未配置，无法获取地址");
            return null;
        }

        try {
            String url = UriComponentsBuilder.fromHttpUrl(RE_GEO_URL)
                    .queryParam("key", gaodeKey)
                    .queryParam("location", longitude + "," + latitude)
                    .queryParam("extensions", "all")
                    .queryParam("output", "JSON")
                    .toUriString();

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            if ("1".equals(root.path("status").asText())) {
                JsonNode regeocode = root.path("regeocode");
                JsonNode addressComponent = regeocode.path("addressComponent");

                AddressInfo info = new AddressInfo();
                info.setFormattedAddress(regeocode.path("formatted_address").asText());
                info.setProvince(addressComponent.path("province").asText());
                info.setCity(addressComponent.path("city").asText());
                info.setDistrict(addressComponent.path("district").asText());
                info.setTowncode(addressComponent.path("towncode").asText());
                info.setStreet(addressComponent.path("streetNumber").path("street").asText());
                info.setStreetNumber(addressComponent.path("streetNumber").path("number").asText());

                return info;
            }
        } catch (Exception e) {
            log.error("调用高德地图API失败", e);
        }
        return null;
    }

    /**
     * 地址信息类
     */
    @lombok.Data
    public static class AddressInfo {
        private String formattedAddress;
        private String province;
        private String city;
        private String district;
        private String towncode;
        private String street;
        private String streetNumber;
    }
}
