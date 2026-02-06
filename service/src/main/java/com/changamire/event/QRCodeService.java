package com.changamire.event;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * QR Code Service
 * 
 * This service provides QR code generation and reading capabilities for tickets.
 * Generated QR codes contain ticket verification URLs and are used for
 * ticket validation at event entrances.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Service
public class QRCodeService {
    
    private final String QR_CODE_DIR = "qrcodes/";

    public String generateQRCode(String text, int width, int height) throws IOException {
        String fileName = QR_CODE_DIR + UUID.randomUUID() + ".png";
        Path path = Paths.get(fileName);
        Files.createDirectories(path.getParent());
        
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix matrix = null;
        try {
            matrix = new MultiFormatWriter().encode(
                text,
                BarcodeFormat.QR_CODE,
                width,
                height,
                hints
            );
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }

        MatrixToImageWriter.writeToPath(matrix, "PNG", path);
        return fileName;
    }

    public String readQRCode(String filePath) throws Exception {
        BufferedImage image = ImageIO.read(new File(filePath));
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Result result = new MultiFormatReader().decode(bitmap);
        return result.getText();
    }
}