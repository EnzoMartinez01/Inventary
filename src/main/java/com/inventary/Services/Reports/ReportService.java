package com.inventary.Services.Reports;

import com.inventary.Dto.Movements.InventoryMovementDto;
import com.inventary.Dto.Reports.KardexDto;
import com.inventary.Model.Authentication.Users;
import com.inventary.Model.Movements.InventoryMovement;
import com.inventary.Model.Movements.MovementType;
import com.inventary.Model.Products.Inventory;
import com.inventary.Model.Products.Product;
import com.inventary.Model.Products.WareHouse;
import com.inventary.Repository.Movements.InventoryMovementRepository;
import com.inventary.Repository.Products.InventoryRepository;
import com.inventary.Repository.Products.ProductRepository;
import com.inventary.Repository.Products.WareHouseRepository;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {
    private final InventoryMovementRepository inventoryMovementRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final WareHouseRepository wareHouseRepository;

    public ReportService(InventoryMovementRepository inventoryMovementRepository,
                         InventoryRepository inventoryRepository,
                         ProductRepository productRepository,
                         WareHouseRepository wareHouseRepository) {
        this.inventoryMovementRepository = inventoryMovementRepository;
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.wareHouseRepository = wareHouseRepository;
    }

    public Page<Inventory> generateSocketReport(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return inventoryRepository.findAll(pageable);
    }

    public Page<InventoryMovementDto> generateMovementReport(Long warehouseId, LocalDateTime startDate,
                                                             LocalDateTime endDate, int page, int size, Long userId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryMovement> movements = inventoryMovementRepository.findByFilters(warehouseId, startDate, endDate, userId, pageable);
        return movements.map(this::mapToDto);
    }

    public InventoryMovementDto mapToDto(InventoryMovement inventoryMovement) {
        InventoryMovementDto dto = new InventoryMovementDto();
        dto.setIdInventoryMovement(inventoryMovement.getIdMovement());
        dto.setIdWarehouse(inventoryMovement.getWarehouse().getIdWarehouse());
        dto.setWarehouseName(inventoryMovement.getWarehouse().getName());
        dto.setIdProduct(inventoryMovement.getProduct().getIdProduct());
        dto.setProductName(inventoryMovement.getProduct().getProductName());
        dto.setBeforeStock(inventoryMovement.getBeforeStock());
        dto.setQuantity(inventoryMovement.getQuantity());
        dto.setAfterStock(inventoryMovement.getAfterStock());
        dto.setMovementType(inventoryMovement.getType().name());
        dto.setReason(inventoryMovement.getReason());
        dto.setMovementDate(inventoryMovement.getMovementDate());
        dto.setPrice(inventoryMovement.getPrice());
        dto.setIdUser(inventoryMovement.getCreatedBy().getIdUser());
        dto.setUserName(inventoryMovement.getCreatedBy().getUsername());
        return dto;
    }


    public List<KardexDto> generateKardex(Long warehouseId, LocalDate startDate, LocalDate endDate) {

        WareHouse warehouse = wareHouseRepository.findById(warehouseId)
                .orElseThrow(() -> new RuntimeException("No se encontró el almacén especificado con ID: " + warehouseId));

        // Filtrar movimientos por almacén y rango de fechas
        List<InventoryMovement> movements = inventoryMovementRepository
                .findByWarehouseAndMovementDateBetweenOrderByMovementDateAsc(warehouse, startDate.atStartOfDay(), endDate.atTime(23, 59, 59));

        if (movements.isEmpty()) {
            throw new RuntimeException("No se encontraron movimientos en el rango de fechas especificado.");
        }

        List<Inventory> inventoryList = inventoryRepository.findByWarehouse_IdWarehouse(warehouseId);
        int currentStock = inventoryList.isEmpty() ? 0 : inventoryList.get(0).getQuantity();

        List<KardexDto> kardex = new ArrayList<>();
        for (InventoryMovement movement : movements) {
            if (movement.getType() == MovementType.ENTRY) {
                currentStock += movement.getQuantity();
            } else if (movement.getType() == MovementType.EXIT) {
                currentStock -= movement.getQuantity();
            }

            KardexDto entry = new KardexDto(
                    movement.getMovementDate(),
                    movement.getMovementIdentifier(),
                    movement.getType().toString(),
                    movement.getBeforeStock(),
                    movement.getQuantity(),
                    movement.getAfterStock(),
                    movement.getProduct() != null ? movement.getProduct().getProductName() : "Sin nombre",
                    movement.getReason() != null ? movement.getReason() : "Sin razón",
                    movement.getPrice() != null ? movement.getPrice() : 0.0,
                    Optional.ofNullable(movement.getCreatedBy()).map(Users::getName).orElse("N/A"),
                    currentStock
            );

            kardex.add(entry);
        }
        return kardex;
    }



    // Exportar el kardex en formato Excel
    public ByteArrayInputStream generateKardexExcel(List<KardexDto> kardex) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Kardex");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            CellStyle numericStyle = workbook.createCellStyle();
            numericStyle.cloneStyleFrom(dataStyle);
            numericStyle.setDataFormat(workbook.createDataFormat().getFormat("0")); // Formato entero

            CellStyle priceStyle = workbook.createCellStyle();
            priceStyle.cloneStyleFrom(dataStyle);
            priceStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00")); // Formato decimal con dos dígitos

            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.cloneStyleFrom(dataStyle);
            dateStyle.setDataFormat(workbook.createDataFormat().getFormat("dd/MM/yyyy")); // Formato de fecha

            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setBorderTop(BorderStyle.THIN);
            titleStyle.setBorderBottom(BorderStyle.THIN);
            titleStyle.setBorderLeft(BorderStyle.THIN);
            titleStyle.setBorderRight(BorderStyle.THIN);

            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Reporte de Kardex");
            titleCell.setCellStyle(titleStyle);

            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));

            Row headerRow = sheet.createRow(1);
            String[] headers = {"Fecha", "Identificador", "Tipo de Movimiento", "Stock Anterior", "Cantidad", "Stock Actual", "Producto", "Razón", "Precio Unitario", "Creado por"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIndex = 2;
            for (KardexDto entry : kardex) {
                Row row = sheet.createRow(rowIndex++);

                Cell dateCell = row.createCell(0);
                dateCell.setCellValue(entry.getMovementDate());
                dateCell.setCellStyle(dateStyle);

                Cell identifierCell = row.createCell(1);
                identifierCell.setCellValue(entry.getMovementIdentifier());
                identifierCell.setCellStyle(dataStyle);

                Cell typeCell = row.createCell(2);
                typeCell.setCellValue(entry.getMovementType());
                typeCell.setCellStyle(dataStyle);

                Cell beforeStockCell = row.createCell(3);
                beforeStockCell.setCellValue(entry.getBeforeStock());
                beforeStockCell.setCellStyle(numericStyle);

                Cell quantityCell = row.createCell(4);
                quantityCell.setCellValue(entry.getQuantity());
                quantityCell.setCellStyle(numericStyle);

                Cell afterStockCell = row.createCell(5);
                afterStockCell.setCellValue(entry.getAfterStock());
                afterStockCell.setCellStyle(numericStyle);

                Cell productCell = row.createCell(6);
                productCell.setCellValue(entry.getProductName());
                productCell.setCellStyle(dataStyle);

                Cell reasonCell = row.createCell(7);
                reasonCell.setCellValue(entry.getReason());
                reasonCell.setCellStyle(dataStyle);

                Cell priceCell = row.createCell(8);
                priceCell.setCellValue(entry.getPrice());
                priceCell.setCellStyle(priceStyle);

                Cell createdByCell = row.createCell(9);
                createdByCell.setCellValue(entry.getCreatedBy());
                createdByCell.setCellStyle(dataStyle);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            return new ByteArrayInputStream(out.toByteArray());
        }
    }

}
