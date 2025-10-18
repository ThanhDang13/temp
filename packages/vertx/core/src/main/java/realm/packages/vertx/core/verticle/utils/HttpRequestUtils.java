package realm.packages.vertx.core.verticle.utils;

import java.net.URLDecoder;
import java.util.regex.Pattern;

public class HttpRequestUtils {

    private static final String regex = "%[0-9][0-9,a-f|A-F]";

    public static String correct(String url) {
        try {

            String[] list = url.split("%");
            StringBuilder builder = new StringBuilder();
            builder.append(list[0]);

            if (list.length > 1) {
                for (int i = 1; i < list.length; i++) {
                    if (Pattern.compile("(?=(" + regex + "))").matcher("%" + list[i]).find()) {
                        builder.append("%").append(list[i]);
                    } else {
                        builder.append("%25").append(list[i]);
                    }
                }
            }

            String newRequest = builder.toString();
            URLDecoder.decode(newRequest, "UTF-8");
            return newRequest;
        } catch (Exception e) {
            e.printStackTrace();
            return "/";
        }
    }
}
