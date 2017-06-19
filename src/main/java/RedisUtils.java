import java.nio.charset.Charset;

/**
 * Created by gcoder on 2017/6/19.
 */
public final class RedisUtils {

    private static final String CHARSET_UTF8 = "UTF-8";

    public static final byte[] getKey(String... key) {
        String strKey = new String();
        for (int i = 0; i < key.length; i++) {
            strKey.concat(key[i]).concat(":");
        }
        return strKey.getBytes(Charset.forName(CHARSET_UTF8));
    }

}
