package club.sentinc.bilibili.songhime.entity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class LivePacket {
        public final int packetLength;

        public  short headerLength;

        public final short ver;

        public final int op;

        public final int seq;

        public final Object data;

        public LivePacket(ByteBuffer byteBuffer) {
            Object tempData;
            packetLength = byteBuffer.getInt();
            headerLength = byteBuffer.getShort();
            ver = byteBuffer.getShort();
            op = byteBuffer.getInt();
            seq = byteBuffer.getInt();
            if (op == 5) {
                byte[] data = new byte[byteBuffer.limit() - 16];
                byteBuffer.get(data);
                Inflater decompressor = new Inflater();
                try (ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length)) {
                    decompressor.setInput(data);
                    final byte[] buf = new byte[1024];
                    while (!decompressor.finished()) {
                        int count = decompressor.inflate(buf);
                        bos.write(buf, 0, count);
                    }
                    tempData = bos.toString(StandardCharsets.UTF_8).substring(16);
                } catch (IOException | DataFormatException e) {
                    tempData = new String(data, StandardCharsets.UTF_8);
                } finally {
                    decompressor.end();
                }
            } else if (op == 3) {
                tempData = byteBuffer.getInt();
            } else {
                byte[] data = new byte[byteBuffer.limit() - 16];
                byteBuffer.get(data);
                tempData = new String(data, StandardCharsets.UTF_8);
            }
            this.data = tempData;
        }



        @Override
        public String toString() {
            return "DanmuCmd{" +
                    "packetLength=" + packetLength +
                    ", headerLength=" + headerLength +
                    ", ver=" + ver +
                    ", op=" + op +
                    ", seq=" + seq +
                    ", data=" + data +
                    '}';
        }
    }