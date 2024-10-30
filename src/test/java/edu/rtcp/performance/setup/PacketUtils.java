package edu.rtcp.performance.setup;

import edu.rtcp.common.message.rtcp.factory.PacketFactory;
import edu.rtcp.common.message.rtcp.packet.Bye;
import edu.rtcp.common.message.rtcp.packet.ReceiverReport;
import edu.rtcp.common.message.rtcp.packet.SenderReport;
import edu.rtcp.common.message.rtcp.parts.ReportBlock;
import io.netty.buffer.Unpooled;

import java.util.ArrayList;
import java.util.List;

public class PacketUtils {
    // Size: 233 bytes
    private static final String BIG_TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.";
    private static final PacketFactory factory = new PacketFactory();

    // Size: 263 bytes (Total: 329)
    public static SenderReport createInitial(int ssrc) {
        return factory.createSenderReport(
                (byte) 0,
                ssrc,
                null,
                Unpooled.buffer().writeBytes(BIG_TEXT.getBytes())
        );
    }

    // Size: 272 bytes (Total: 338)
    public static SenderReport createData(int ssrc) {
        List<ReportBlock> blocks = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            blocks.add(factory.createReportBlock(ssrc, (byte) 0));
        }

        return factory.createSenderReport(
                (byte) blocks.size(),
                ssrc,
                blocks,
                null
        );
    }

    // Size: 252 bytes (Total: 318)
    public static ReceiverReport createResponse(int ssrc) {
        List<ReportBlock> blocks = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            blocks.add(factory.createReportBlock(ssrc, (byte) 0));
        }

        return factory.createReceiverReport(
                (byte) blocks.size(),
                ssrc,
                blocks
        );
    }

    // Size: 244 bytes (Total: 310)
    public static Bye createBye(int ssrc) {
        return factory.createBye(
                (byte) 0,
                ssrc,
                BIG_TEXT
        );
    }
}
