package com.group16.grocery_app.utils;

import com.group16.grocery_app.model.Order;
import com.group16.grocery_app.model.OrderItem;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

public class InvoiceGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Generates a text invoice for an order.
     *
     * @param order the order to generate invoice for
     * @param customerName the customer's name
     * @param customerAddress the customer's address
     * @return the invoice as a formatted text string
     * @author Ege Usug
     */
    public static String generateInvoiceText(Order order, String customerName, String customerAddress) {
        StringJoiner invoice = new StringJoiner("\n");

        invoice.add("=".repeat(60));
        invoice.add("GROUP16 GREENGROCER - INVOICE");
        invoice.add("=".repeat(60));
        invoice.add("");
        invoice.add("Customer: " + customerName);
        if (customerAddress != null && !customerAddress.isEmpty()) {
            invoice.add("Address: " + customerAddress);
        }
        invoice.add("Order ID: " + order.getId());
        invoice.add("Order Date: " + (order.getCreatedAt() != null ?
                order.getCreatedAt().format(DATE_FORMATTER) : "N/A"));
        if (order.getDeliveryDate() != null) {
            invoice.add("Delivery Date: " + order.getDeliveryDate().format(DATE_FORMATTER));
        }
        invoice.add("Status: " + order.getStatus());
        invoice.add("");
        invoice.add("-".repeat(60));
        invoice.add(String.format("%-30s %10s %10s %10s", "Item", "Quantity", "Price", "Total"));
        invoice.add("-".repeat(60));

        double subtotal = 0;
        for (OrderItem item : order.getItems()) {
            double itemTotal = item.getTotalPrice();
            subtotal += itemTotal;
            invoice.add(String.format("%-30s %10.2f kg %9.2f ₺ %9.2f ₺",
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    itemTotal));
        }

        double vat = subtotal * 0.18;
        double total = subtotal + vat;

        invoice.add("-".repeat(60));
        invoice.add(String.format("%-30s %31.2f ₺", "Subtotal:", subtotal));
        invoice.add(String.format("%-30s %31.2f ₺", "VAT (18%):", vat));
        invoice.add(String.format("%-30s %31.2f ₺", "Total:", total));
        invoice.add("=".repeat(60));
        invoice.add("");
        invoice.add("Thank you for your purchase! :))");

        return invoice.toString();
    }

    /**
     * Generates a PDF invoice for an order.
     *
     * @param order the order to generate invoice for
     * @param customerName the customer's name
     * @param customerAddress the customer's address
     * @return the invoice as a PDF byte array
     * @throws IOException if an error occurs while generating the PDF
     * @author Ege Usug
     */
    public static byte[] generateInvoicePDF(Order order, String customerName, String customerAddress) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        PDType1Font boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

        float yPosition = 750;
        float lineHeight = 20;
        float margin = 50;
        float width = page.getMediaBox().getWidth() - 2 * margin;

        contentStream.beginText();
        contentStream.setFont(boldFont, 18);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("GROUP16 GREENGROCER - INVOICE");
        contentStream.endText();
        yPosition -= lineHeight * 2;

        contentStream.moveTo(margin, yPosition);
        contentStream.lineTo(margin + width, yPosition);
        contentStream.stroke();
        yPosition -= lineHeight;

        contentStream.beginText();
        contentStream.setFont(boldFont, 12);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("Customer: " + customerName);
        contentStream.endText();
        yPosition -= lineHeight;

        if (customerAddress != null && !customerAddress.isEmpty()) {
            contentStream.beginText();
            contentStream.setFont(font, 12);
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Address: " + customerAddress);
            contentStream.endText();
            yPosition -= lineHeight;
        }

        contentStream.beginText();
        contentStream.setFont(font, 12);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("Order ID: " + order.getId());
        contentStream.endText();
        yPosition -= lineHeight;

        if (order.getCreatedAt() != null) {
            contentStream.beginText();
            contentStream.setFont(font, 12);
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Order Date: " + order.getCreatedAt().format(DATE_FORMATTER));
            contentStream.endText();
            yPosition -= lineHeight;
        }

        if (order.getDeliveryDate() != null) {
            contentStream.beginText();
            contentStream.setFont(font, 12);
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Delivery Date: " + order.getDeliveryDate().format(DATE_FORMATTER));
            contentStream.endText();
            yPosition -= lineHeight;
        }

        contentStream.beginText();
        contentStream.setFont(font, 12);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("Status: " + order.getStatus());
        contentStream.endText();
        yPosition -= lineHeight * 2;

        contentStream.beginText();
        contentStream.setFont(boldFont, 12);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText(String.format("%-30s %10s %12s %12s", "Item", "Quantity", "Unit Price", "Total"));
        contentStream.endText();
        yPosition -= lineHeight;

        contentStream.moveTo(margin, yPosition);
        contentStream.lineTo(margin + width, yPosition);
        contentStream.stroke();
        yPosition -= lineHeight;

        double subtotal = 0;
        for (OrderItem item : order.getItems()) {
            if (yPosition < 100) {
                contentStream.close();
                page = new PDPage();
                document.addPage(page);
                contentStream = new PDPageContentStream(document, page);
                yPosition = 750;
            }

            double itemTotal = item.getTotalPrice();
            subtotal += itemTotal;

            contentStream.beginText();
            contentStream.setFont(font, 10);
            contentStream.newLineAtOffset(margin, yPosition);
            String itemLine = String.format("%-30s %8.2f kg %10.2f TRY %10.2f TRY",
                    item.getProduct().getName().length() > 30 ?
                            item.getProduct().getName().substring(0, 27) + "..." :
                            item.getProduct().getName(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    itemTotal);
            contentStream.showText(itemLine);
            contentStream.endText();
            yPosition -= lineHeight;
        }

        yPosition -= lineHeight;
        contentStream.moveTo(margin, yPosition);
        contentStream.lineTo(margin + width, yPosition);
        contentStream.stroke();
        yPosition -= lineHeight;

        double vat = subtotal * 0.18;
        double total = subtotal + vat;

        contentStream.beginText();
        contentStream.setFont(font, 12);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText(String.format("Subtotal: %.2f TRY", subtotal));
        contentStream.endText();
        yPosition -= lineHeight;

        contentStream.beginText();
        contentStream.setFont(font, 12);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText(String.format("VAT (18%%): %.2f TRY", vat));
        contentStream.endText();
        yPosition -= lineHeight;

        contentStream.beginText();
        contentStream.setFont(boldFont, 14);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText(String.format("Total: %.2f TRY", total));
        contentStream.endText();
        yPosition -= lineHeight * 2;

        contentStream.beginText();
        contentStream.setFont(font, 12);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("Thank you for your purchase!");
        contentStream.endText();

        contentStream.close();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.save(baos);
        document.close();

        return baos.toByteArray();
    }
}
