package edu.rtcp.common.decoder;

import io.netty.util.CharsetUtil;
import java.nio.charset.Charset;

public final class DecodingUtils {
    public static final Charset CHARSET = CharsetUtil.UTF_8;

    // FRAME SIZE = 36 + 1 + 23 = 60
    public static final int UUID_SIZE = 36;
    public static final int TYPE_SIZE = 1;
    public static final int CONTENT_SIZE = 83;

    public static final int MAX_FRAME_SIZE = DecodingUtils.UUID_SIZE
            + DecodingUtils.TYPE_SIZE
            + DecodingUtils.CONTENT_SIZE;
}
