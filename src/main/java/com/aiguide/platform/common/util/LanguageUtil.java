package com.aiguide.platform.common.util;

import com.aiguide.platform.common.constant.BusinessConstant;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

public final class LanguageUtil {

    private LanguageUtil() {
    }

    public static String getLanguageCode(HttpServletRequest request) {
        if (request == null) {
            return BusinessConstant.LANG_ZH;
        }
        return normalizeLanguageCode(request.getHeader("Accept-Language"));
    }

    public static String normalizeLanguageCode(String languageCode) {
        if (StringUtils.isBlank(languageCode)) {
            return BusinessConstant.LANG_ZH;
        }
        String normalized = languageCode.trim();
        int commaIndex = normalized.indexOf(',');
        if (commaIndex >= 0) {
            normalized = normalized.substring(0, commaIndex);
        }
        int semicolonIndex = normalized.indexOf(';');
        if (semicolonIndex >= 0) {
            normalized = normalized.substring(0, semicolonIndex);
        }
        normalized = normalized.trim();
        if (normalized.equalsIgnoreCase("zh")
                || normalized.equalsIgnoreCase("zh-CN")
                || normalized.equalsIgnoreCase("zh-Hans")) {
            return BusinessConstant.LANG_ZH;
        }
        if (normalized.equalsIgnoreCase("en")
                || normalized.equalsIgnoreCase("en-US")) {
            return BusinessConstant.LANG_EN;
        }
        if (normalized.equalsIgnoreCase("ja")
                || normalized.equalsIgnoreCase("ja-JP")) {
            return BusinessConstant.LANG_JA;
        }
        if (normalized.equalsIgnoreCase("ko")
                || normalized.equalsIgnoreCase("ko-KR")) {
            return BusinessConstant.LANG_KO;
        }
        return BusinessConstant.LANG_ZH;
    }

    public static boolean isDefaultLanguage(String languageCode) {
        return BusinessConstant.LANG_ZH.equals(normalizeLanguageCode(languageCode));
    }
}
