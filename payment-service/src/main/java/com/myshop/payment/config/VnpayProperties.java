package com.myshop.payment.config;

import com.myshop.payment.constant.VnpayConstants;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Configuration
@ConfigurationProperties(prefix = "payment.vn-pay")
@Getter
@Setter
public class VnpayProperties {

    private String url;
    private String tmnCode;
    private String secretKey;
    private String returnUrl;
    private String version = "2.1.0";
    private String command = "pay";
    private String orderType = "other";

    public Map<String, String> getVNPayConfig() {
        Map<String, String> params = new HashMap<>();
        params.put(VnpayConstants.Param.VERSION, this.version);
        params.put(VnpayConstants.Param.COMMAND, this.command);
        params.put(VnpayConstants.Param.TMN_CODE, this.tmnCode);
        params.put(VnpayConstants.Param.CURR_CODE, VnpayConstants.CURRENCY_VND);
        params.put(VnpayConstants.Param.ORDER_TYPE, this.orderType);
        params.put(VnpayConstants.Param.LOCALE, VnpayConstants.LOCALE_VN);
        params.put(VnpayConstants.Param.RETURN_URL, this.returnUrl);

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(VnpayConstants.TIMEZONE));
        SimpleDateFormat fmt = new SimpleDateFormat(VnpayConstants.DATE_PATTERN);
        params.put(VnpayConstants.Param.CREATE_DATE, fmt.format(cal.getTime()));
        cal.add(Calendar.MINUTE, 15);
        params.put(VnpayConstants.Param.EXPIRE_DATE, fmt.format(cal.getTime()));

        return params;
    }
}
