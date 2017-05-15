/*
Copyright(C) 2016 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html
Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
package org.opensrp.web.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import java.util.List;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * @author Haris Asif - haris.asif@ihsinformatics.com
 */
public class PdfUtil {
	
	private static final float[] MARGINS = { 90f, 0, 30f, 30f };
	
	private static final float TABLE_WIDTH = 100f;
	
	public static ByteArrayOutputStream generatePdf(List<String> data, int width, int height, int copiesImage,
	                                                int columnLimit) {
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			Document document = new Document();
			document.setMargins(MARGINS[0], MARGINS[1], MARGINS[2], MARGINS[3]);
			
			PdfWriter.getInstance(document, byteArrayOutputStream);
			document.open();
			
			PdfPTable table = new PdfPTable(columnLimit);
			table.setTotalWidth(TABLE_WIDTH);
			table.setHorizontalAlignment(Element.ALIGN_LEFT);
			
			int length = 0;
			int count = 0;
			
			for (String str : data) {
				if (str.length() > 0 && str.length() <= 5) {
					length = 54;
				} else if (str.length() >= 6 && str.length() <= 9) {
					length = 44;
				} else if (str.length() >= 10 && str.length() <= 11) {
					length = 36;
				} else if (str.length() >= 12 && str.length() <= 14) {
					length = 27;
				} else if (str.length() >= 15 && str.length() <= 17) {
					length = 22;
				} else {
					length = 15;
				}
				Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
				hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
				
				QRCodeWriter qrCodeWriter = new QRCodeWriter();
				BitMatrix byteMatrix = null;
				
				byteMatrix = qrCodeWriter.encode(str, BarcodeFormat.QR_CODE, width, height, hintMap);
				
				int matrixWidth = byteMatrix.getWidth();
				int matrixHeight = byteMatrix.getHeight();
				BufferedImage image = new BufferedImage(matrixWidth, matrixHeight, BufferedImage.TYPE_INT_RGB);
				image.createGraphics();
				Graphics2D graphics = (Graphics2D) image.getGraphics();
				graphics.setColor(Color.WHITE);
				graphics.fillRect(0, 0, matrixWidth + 5, matrixHeight + 5);
				graphics.setFont(graphics.getFont().deriveFont(13f));
				graphics.setColor(Color.BLACK);
				graphics.drawString(str, length, height - 10);
				for (int i = 0; i < matrixHeight; i++) {
					for (int j = 0; j < matrixHeight; j++) {
						if (byteMatrix.get(i, j)) {
							graphics.fillRect((i), j, 1, 1);
						}
					}
				}
				Image itextImage = null;
				itextImage = Image.getInstance(Toolkit.getDefaultToolkit().createImage(image.getSource()), null);
				
				for (int i = 0; i < copiesImage; i++) {
					PdfPCell cell = new PdfPCell(itextImage);
					cell.setBorder(Rectangle.NO_BORDER);
					count++;
					table.addCell(cell);
				}
			}
			for (int i = 0; i < 6; i++) {
				if (count % columnLimit != 0) {
					PdfPCell cell = new PdfPCell(new Phrase());
					cell.setBorder(Rectangle.NO_BORDER);
					table.addCell(cell);
					count++;
				}
			}
			document.add(table);
			document.close();
			
			return byteArrayOutputStream;
		}
		catch (Exception e) {
			return null;
		}
	}
}
